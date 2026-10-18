package alarm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger; // M5 新增

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

    /** 
     * M5 考点：多线程下保证编号原子自增，避免数据竞争。
     */
    private static final AtomicInteger seq = new AtomicInteger(0);

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
                "ALM_" + seq.incrementAndGet(),
                e.getDeviceId(),
                typeOf(e.getDeviceId()),
                e.getLevel(),
                e.getDescription(),
                LocalDateTime.now());
    }

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

    public boolean confirm() {
        if (status != Status.UNCONFIRMED) return false;
        status = Status.CONFIRMED;
        return true;
    }

    public boolean handle(String note) {
        if (status != Status.CONFIRMED || note == null || note.isBlank()) return false;
        status = Status.HANDLED;
        this.handleNote = note.trim();
        return true;
    }

    public boolean ignore(String reason) {
        if (status == Status.HANDLED || status == Status.IGNORED) return false;
        if (reason == null || reason.isBlank()) return false;
        status = Status.IGNORED;
        this.ignoreReason = reason.trim();
        return true;
    }

    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("报警编号：").append(alarmId).append('\n');
        sb.append("设备编号：").append(deviceId).append('\n');
        sb.append("报警类型：").append(type).append('\n');
        sb.append("报警等级：").append(level.getLabel()).append('\n');
        sb.append("报警描述：").append(description).append('\n');
        sb.append("报警时间：").append(getAlarmTime()).append('\n');
        sb.append("报警状态：").append(status.getLabel());
        if (handleNote != null) sb.append("\n处理说明：").append(handleNote);
        if (ignoreReason != null) sb.append("\n忽略原因：").append(ignoreReason);
        return sb.toString();
    }

    public String getAlarmId() { return alarmId; }
    public String getDeviceId() { return deviceId; }
    public String getType() { return type; }
    public String getLevel() { return level.getLabel(); }
    public AlarmException.Level getLevelEnum() { return level; }
    public String getDescription() { return description; }
    public String getAlarmTime() { return alarmTime.format(FMT); }
    public LocalDateTime getTime() { return alarmTime; }
    public String getStatus() { return status.getLabel(); }
    public Status getStatusEnum() { return status; }
    public String getHandleNote() { return handleNote; }
    public String getIgnoreReason() { return ignoreReason; }
}