package alarm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.AlarmException;

/**
 * 报警记录仓库（内存版，FR-ALM-01 / FR-ALM-03 / FR-ALM-04）。
 * 和设备池（DeviceArray）一样用静态结构保存，程序运行期间有效。
 */
public final class AlarmLog {

    private static final List<AlarmRecord> RECORDS = new ArrayList<>();

    private AlarmLog() {
    }

    /**
     * 捕获到 AlarmException 时调用：自动生成一条报警记录并入库。
     * @return 生成的记录（方便调用方提示"已生成报警记录 ALM_x"）
     */
    public static AlarmRecord record(AlarmException e) {
        AlarmRecord r = AlarmRecord.from(e);
        RECORDS.add(r);
        return r;
    }

    public static List<AlarmRecord> getAll() {
        return Collections.unmodifiableList(RECORDS);
    }

    public static int size() {
        return RECORDS.size();
    }

    /** 按报警编号查找记录（FR-ALM-04 确认与处理前要先定位记录），找不到返回 null。 */
    public static AlarmRecord findById(String alarmId) {
        if (alarmId == null) {
            return null;
        }
        for (AlarmRecord r : RECORDS) {
            if (r.getAlarmId().equalsIgnoreCase(alarmId.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * FR-ALM-03 组合查询：所有条件都可传 null 表示"不限制"。
     * 查询结果按报警时间倒序排列（最新的在最前）。
     */
    public static List<AlarmRecord> query(AlarmRecord.Status status,
            AlarmException.Level level, String deviceId,
            LocalDateTime from, LocalDateTime to) {
        List<AlarmRecord> result = new ArrayList<>();
        for (AlarmRecord r : RECORDS) {
            if (status != null && r.getStatusEnum() != status) {
                continue;
            }
            if (level != null && r.getLevelEnum() != level) {
                continue;
            }
            if (deviceId != null && !deviceId.isBlank()
                    && !r.getDeviceId().equalsIgnoreCase(deviceId.trim())) {
                continue;
            }
            if (from != null && r.getTime().isBefore(from)) {
                continue;
            }
            if (to != null && r.getTime().isAfter(to)) {
                continue;
            }
            result.add(r);
        }
        // FR-ALM-03：查询结果按报警时间倒序排列
        result.sort((a, b) -> b.getTime().compareTo(a.getTime()));
        return result;
    }

    /** FR-ALM-03 查询全部（按时间倒序）。 */
    public static void printAll() {
        print(query(null, null, null, null, null));
    }

    /** FR-ALM-03：以表格形式打印一批查询结果。 */
    public static void print(List<AlarmRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("没有符合条件的报警记录");
            return;
        }
        String[] headers = {"报警编号", "设备编号", "报警类型", "等级", "报警时间", "报警状态", "报警描述"};
        String[][] rows = new String[records.size()][];
        for (int i = 0; i < records.size(); i++) {
            AlarmRecord r = records.get(i);
            rows[i] = new String[]{r.getAlarmId(), r.getDeviceId(), r.getType(), r.getLevel(),
                    r.getAlarmTime(), r.getStatus(), r.getDescription()};
        }
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = displayWidth(headers[i]);
            for (String[] row : rows) {
                widths[i] = Math.max(widths[i], displayWidth(row[i]));
            }
        }
        StringBuilder headerLine = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            headerLine.append(headers[i]).append(" ".repeat(widths[i] + 3 - displayWidth(headers[i])));
        }
        System.out.println(headerLine.toString().stripTrailing());
        for (String[] row : rows) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                line.append(row[i]).append(" ".repeat(widths[i] + 3 - displayWidth(row[i])));
            }
            System.out.println(line.toString().stripTrailing());
        }
        System.out.println("共 " + records.size() + " 条报警记录");
    }

    // 与 FrDev.showInfo 相同的等宽对齐逻辑：中文汉字和全角字符占两格
    private static int displayWidth(String text) {
        return text.codePoints().map(c ->
                Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN
                || (c >= 0x3000 && c <= 0x303F)
                || (c >= 0xFF01 && c <= 0xFF60)
                || (c >= 0xFFE0 && c <= 0xFFE6) ? 2 : 1).sum();
    }
}
