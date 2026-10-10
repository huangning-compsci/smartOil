package alarm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import exceptions.AlarmException;

/**
 * 报警记录（FR-ALM-01）。
 * 捕获到 AlarmException 时自动生成（见 AlarmLog.record），
 * 包含需求要求的六个字段：报警编号、设备编号、报警类型、报警描述、报警时间、报警状态，
 * 另附带报警等级（FR-ALM-02），是 FR-ALM-03 报警查询的数据来源。
 *
 * 状态机（FR-ALM-04）：
 *   未确认 --confirm()--> 已确认 --handle(处理说明)--> 已处理
 *   未确认 / 已确认 --ignore(忽略原因)--> 已忽略
 */
public class AlarmRecord {

    /** 报警状态（FR-ALM-03 筛选项 / FR-ALM-04 操作结果）。 */
    public enum Status {
        UNCONFIRMED("未确认"), CONFIRMED("已确认"), HANDLED("已处理"), IGNORED("已忽略");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 报警编号自增序号（内存计数，程序重启后归零）。 */
    private static int seq;

    private final String alarmId;             // 报警编号
    private final String deviceId;            // 设备编号
    private final String type;                // 报警类型
    private final AlarmException.Level level; // 报警等级（FR-ALM-02）
    private final String description;         // 报警描述
    private final LocalDateTime alarmTime;    // 报警时间
    private Status status;                    // 报警状态
    private String handleNote;                // 处理说明（标记已处理时填写，FR-ALM-04）
    private String ignoreReason;              // 忽略原因（忽略误报时填写，FR-ALM-04）

    private AlarmRecord(String alarmId, String deviceId, String type, AlarmException.Level level,
            String description, LocalDateTime alarmTime) {
        this.alarmId = alarmId;
        this.deviceId = deviceId;
        this.type = type;
        this.level = level;
        this.description = description;
        this.alarmTime = alarmTime;
        this.status = Status.UNCONFIRMED;   // 新生成的记录一律为"未确认"
    }

    /** 工厂方法：从捕获到的报警异常自动生成一条记录。 */
    public static AlarmRecord from(AlarmException e) {
        return new AlarmRecord(
                "ALM_" + (++seq),
                e.getDeviceId(),
                typeOf(e.getDeviceId()),
                e.getLevel(),
                e.getDescription(),
                LocalDateTime.now());
    }

    /**
     * 按设备 id 前缀推断报警类型。
     * 与 FrDev.findById 的前缀路由是同一套约定（PU/CP/PS/TS/FM/CM）。
     */
    private static String typeOf(String deviceId) {
        if (deviceId == null || !deviceId.contains("_")) {
            return "未知类型";
        }
        return switch (deviceId.split("_", -1)[0]) {
            case "PU" -> "载荷异常";
            case "CP" -> "出口压力异常";
            case "CM" -> "排气参数异常";
            case "PS" -> "压力越限";
            case "TS" -> "温度越限";
            case "FM" -> "流量异常";
            default -> "未知类型";
        };
    }

    /** FR-ALM-04：确认报警。仅"未确认"状态可确认，确认后变为"已确认"。 */
    public boolean confirm() {
        if (status != Status.UNCONFIRMED) {
            return false;
        }
        status = Status.CONFIRMED;
        return true;
    }

    /** FR-ALM-04：标记为"已处理"。需先确认，且处理说明不能为空。 */
    public boolean handle(String note) {
        if (status != Status.CONFIRMED || note == null || note.isBlank()) {
            return false;
        }
        status = Status.HANDLED;
        this.handleNote = note.trim();
        return true;
    }

    /** FR-ALM-04：忽略误报。忽略原因不能为空；已终结（已处理/已忽略）的记录不可再操作。 */
    public boolean ignore(String reason) {
        if (status == Status.HANDLED || status == Status.IGNORED) {
            return false;
        }
        if (reason == null || reason.isBlank()) {
            return false;
        }
        status = Status.IGNORED;
        this.ignoreReason = reason.trim();
        return true;
    }

    /** 单条记录的完整详情（报警确认与处理界面用）。 */
    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("报警编号：").append(alarmId).append('\n');
        sb.append("设备编号：").append(deviceId).append('\n');
        sb.append("报警类型：").append(type).append('\n');
        sb.append("报警等级：").append(level.getLabel()).append('\n');
        sb.append("报警描述：").append(description).append('\n');
        sb.append("报警时间：").append(getAlarmTime()).append('\n');
        sb.append("报警状态：").append(status.getLabel());
        if (handleNote != null) {
            sb.append("\n处理说明：").append(handleNote);
        }
        if (ignoreReason != null) {
            sb.append("\n忽略原因：").append(ignoreReason);
        }
        return sb.toString();
    }

    public String getAlarmId() { return alarmId; }
    public String getDeviceId() { return deviceId; }
    public String getType() { return type; }
    /** 等级中文标签（表格展示用）。 */
    public String getLevel() { return level.getLabel(); }
    /** 等级枚举（筛选比较用）。 */
    public AlarmException.Level getLevelEnum() { return level; }
    public String getDescription() { return description; }
    /** 格式化后的报警时间（表格展示用）。 */
    public String getAlarmTime() { return alarmTime.format(FMT); }
    /** 原始时间（时间范围查询、倒序排序用）。 */
    public LocalDateTime getTime() { return alarmTime; }
    /** 状态中文标签（表格展示用）。 */
    public String getStatus() { return status.getLabel(); }
    /** 状态枚举（筛选比较用）。 */
    public Status getStatusEnum() { return status; }
    public String getHandleNote() { return handleNote; }
    public String getIgnoreReason() { return ignoreReason; }
}
