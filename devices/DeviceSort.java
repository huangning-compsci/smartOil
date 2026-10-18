package devices;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** 外部比较器：排序副本，不改变设备池或台账顺序。 */
public final class DeviceSort {
    private DeviceSort() { }

    private static final List<String> STATUS_ORDER = List.of(
            "投运中", "维修中", "故障", "停用", "尚未启动");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT);

    public static final Comparator<Equipment> BY_ID = (a, b) -> {
        String[] left = a.getId().split("_", -1);
        String[] right = b.getId().split("_", -1);
        int prefix = left[0].compareTo(right[0]);
        return prefix != 0 ? prefix : Integer.compare(parseNumber(left), parseNumber(right));
    };

    public static final Comparator<Equipment> BY_STATUS =
            Comparator.comparingInt(DeviceSort::statusRank).thenComparing(BY_ID);

    public static final Comparator<Equipment> BY_INSTALL_DATE =
            Comparator.comparing(DeviceSort::parseDate,
                    Comparator.nullsLast(Comparator.<LocalDate>naturalOrder())).thenComparing(BY_ID);

    public static Equipment[] sorted(Equipment[] devices, Comparator<Equipment> comparator,
            boolean ascending) {
        Equipment[] copy = Arrays.copyOf(devices, devices.length);
        Comparator<Equipment> directed = ascending ? comparator : comparator.reversed();
        // 日期非法与空设备的位置不随升降序翻转。
        if (comparator == BY_INSTALL_DATE) {
            Comparator<Equipment> validDatesFirst = Comparator.comparing(e -> parseDate(e) == null);
            directed = validDatesFirst.thenComparing(directed);
        }
        Arrays.sort(copy, Comparator.nullsLast(directed));
        return copy;
    }

    private static int statusRank(Equipment equipment) {
        int index = STATUS_ORDER.indexOf(equipment.getStatus());
        return index < 0 ? STATUS_ORDER.size() : index;
    }

    private static LocalDate parseDate(Equipment equipment) {
        String date = equipment.getInstallDate();
        if (date == null || date.isBlank()) return null;
        try { return LocalDate.parse(date.trim(), DATE_FORMAT); }
        catch (DateTimeParseException e) { return null; }
    }

    private static int parseNumber(String[] parts) {
        if (parts.length != 2) return Integer.MAX_VALUE;
        try { return Integer.parseInt(parts[1]); }
        catch (NumberFormatException e) { return Integer.MAX_VALUE; }
    }
}
