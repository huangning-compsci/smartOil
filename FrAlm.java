import devices.Equipment;
import exceptions.AlarmException;
import exceptions.DeviceNotFoundException;
import exceptions.DeviceNotRunningException;
import exceptions.InvalidDeviceIdException;
import interfaces.Alarmable;
import interfaces.Collectable;
import java.util.Scanner;

import Task.AlarmLog;
import Task.AlarmRecord;
import Task.MaintenanceTask;

public class FrAlm {
    /** 兼容原调用方，返回本轮报警数量。 */
    public static int patrol(Equipment... devices) {
        return patrolWithResult(devices).alarms();
    }

    public record PatrolResult(int participating, int skipped, int alarms) { }

    /** 只检测本轮成功采集的投运设备；跳过设备不产生新报警。 */
    public static PatrolResult patrolWithResult(Equipment... devices) {
        java.util.List<Alarmable> alarmables = new java.util.ArrayList<>();
        int skipped = 0;
        for (Equipment device : devices) {
            if (device == null) continue;
            if (!device.isRunning() || !(device instanceof Alarmable a)
                    || !(device instanceof Collectable c)) {
                skipped++;
                continue;
            }
            try {
                c.collectData();
                alarmables.add(a);
            } catch (DeviceNotRunningException e) {
                skipped++;
                System.out.println("跳过：" + e.getMessage());
            }
        }
        FrMon.showAll(devices);
        int alarms = Alarmable.patrol(alarmables.toArray(new Alarmable[0]));
        return new PatrolResult(alarmables.size(), skipped, alarms);
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
    /** 报警确认时返回其关联工单；已自动建单的报警不会重复创建。 */
    public static String generateMaintenanceIfSupported(AlarmRecord rec) {
        MaintenanceTask task = FrMnt.createFromAlarm(rec);
        return "（已关联运维工单 " + task.getTaskId() + "）";
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
