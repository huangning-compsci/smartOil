import alarm.AlarmLog;
import alarm.AlarmRecord;
import devices.Equipment;
import exceptions.AlarmException;
import exceptions.DeviceNotFoundException;
import exceptions.DeviceNotRunningException;
import exceptions.InvalidDeviceIdException;
import interfaces.Alarmable;
import interfaces.Collectable;
import interfaces.Maintainable;
import java.util.Scanner;

public class FrAlm {
    /** 先采集最新数据，再巡检全部设备，返回报警数量。 */
    public static int patrol(Equipment... devices) {
        java.util.List<Alarmable> alarmables = new java.util.ArrayList<>();
        for (Equipment device : devices) {
            if (device instanceof Alarmable a) {
                alarmables.add(a);
            }
        }
        FrMon.collectAll(devices);
        return Alarmable.patrol(alarmables.toArray(new Alarmable[0]));
    }

    /** 单设备采集与报警检测；发现报警时先记录，再交给界面提示。 */
    public static String checkAlarm(String alarmId)
            throws InvalidDeviceIdException, DeviceNotFoundException,
            DeviceNotRunningException, AlarmException {
        Equipment dev = FrDev.findById(alarmId);
        if (dev instanceof Alarmable a) {
            if (dev instanceof Collectable c) {
                c.collectData();
            }
            try {
                a.checkAlarm();
            } catch (AlarmException e) {
                AlarmLog.record(e);
                throw e;
            }
            return alarmId + " 状态正常，无报警";
        }
        return "该设备类型不支持报警检测";
    }
    /** FR-ALM-04 可选功能：报警确认后，若设备具备运维能力（Maintainable）则自动生成运维工单。 */
    public static String generateMaintenanceIfSupported(AlarmRecord rec) {
        try {
            Equipment dev = FrDev.findById(rec.getDeviceId());
            if (dev instanceof Maintainable m) {
                m.requestMaintenance("[" + rec.getLevel() + "] " + rec.getDescription());
                return "";
            } else {
                return "（该设备类型不支持自动运维工单，请人工安排检修）";
            }
        } catch (InvalidDeviceIdException | DeviceNotFoundException e) {
            return "（设备 " + rec.getDeviceId() + " 已不在库中，跳过自动运维工单）";
        }
    }

    /** FR-ALM-03：选择报警状态，输入无效返回 null。 */
    public static AlarmRecord.Status chooseStatus(Scanner sc) {
        System.out.println("请选择报警状态：1.未确认 2.已确认 3.已处理 4.已忽略");
        return switch (sc.next()) {
            case "1" -> AlarmRecord.Status.UNCONFIRMED;
            case "2" -> AlarmRecord.Status.CONFIRMED;
            case "3" -> AlarmRecord.Status.HANDLED;
            case "4" -> AlarmRecord.Status.IGNORED;
            default -> {
                System.out.println("输入无效");
                yield null;
            }
        };
    }

    /** FR-ALM-03：选择报警等级，输入无效返回 null。 */
    public static AlarmException.Level chooseLevel(Scanner sc) {
        System.out.println("请选择报警等级：1.一级（紧急） 2.二级（重要） 3.三级（一般）");
        return switch (sc.next()) {
            case "1" -> AlarmException.Level.CRITICAL;
            case "2" -> AlarmException.Level.WARNING;
            case "3" -> AlarmException.Level.INFO;
            default -> {
                System.out.println("输入无效");
                yield null;
            }
        };
    }
}
