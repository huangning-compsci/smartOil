package devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/** 条件筛选返回新数组，不改变原始设备池。 */
public final class DeviceFilter {
    private DeviceFilter() { }

    public static final Predicate<Equipment> RUNNING = Equipment::isRunning;
    public static final Predicate<Equipment> FAULTY = e -> "故障".equals(e.getStatus());

    public static Predicate<Equipment> byType(String type) {
        if (type == null || type.isBlank()) return e -> true;
        String key = type.trim();
        String prefix = switch (key.toUpperCase(Locale.ROOT)) {
            case "PU", "抽油机", "抽油机器", "抽油几" -> "PU";
            case "CP", "离心泵", "离心水泵", "离心磅" -> "CP";
            case "PS", "压力传感器", "压力感应器", "压力传感" -> "PS";
            case "TS", "温度传感器", "温度感应器", "温度传感" -> "TS";
            case "FM", "流量计", "流量表", "流量记" -> "FM";
            case "CM", "压缩机" -> "CM";
            default -> null;
        };
        return e -> prefix == null ? key.equalsIgnoreCase(e.getType())
                : e.getId() != null && e.getId().startsWith(prefix + "_");
    }

    public static Equipment[] filter(Equipment[] devices, Predicate<Equipment> condition) {
        List<Equipment> result = new ArrayList<>();
        for (Equipment device : devices) {
            if (device != null && (condition == null || condition.test(device))) result.add(device);
        }
        return result.toArray(new Equipment[0]);
    }
}
