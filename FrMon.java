import devices.Equipment;
import exceptions.DeviceNotFoundException;
import exceptions.DeviceNotRunningException;
import exceptions.InvalidDeviceIdException;
import interfaces.Collectable;

public class FrMon {
    /** 按编号采集单设备数据，返回采集摘要。 */
    public static String collectById(String id)
            throws InvalidDeviceIdException, DeviceNotFoundException,
            DeviceNotRunningException {
        Equipment dev = FrDev.findById(id);
        if (dev instanceof Collectable c) {
            c.collectData();
            return c.collectSummary();
        }
        return "该设备类型不支持数据采集";
    }

    /** 查看已有数据，不执行采集；未采集状态由设备摘要明确展示。 */
    public static String showById(String id)
            throws InvalidDeviceIdException, DeviceNotFoundException {
        Equipment dev = FrDev.findById(id);
        if (dev instanceof Collectable c) {
            return c.collectSummary();
        }
        return "该设备类型不支持数据采集";
    }

    /** 筛选具备采集能力的设备。 */
    public static Collectable[] allCollectables(Equipment... devices) {
        java.util.List<Collectable> list = new java.util.ArrayList<>();
        for (Equipment e : devices) {
            if (e instanceof Collectable c) {
                list.add(c);
            }
        }
        return list.toArray(new Collectable[0]);
    }

    /** 批量采集，未启动的设备由接口调度方法跳过并提示。 */
    public static void collectAll(Equipment... devices) {
        Collectable.collectAll(allCollectables(devices));
    }
    /** 全部设备的简明实时台账，只查看，不采集。 */
    public static void showAll(Equipment... devices) {
        Collectable.showAll(allCollectables(devices));
    }
}
