package Task;
import java.time.LocalDateTime;

import java.util.concurrent.atomic.AtomicInteger;

/** 一张运维工单，负责保存工单信息并约束状态转换。 */
public class MaintenanceTask {
    
    public enum Status {
        PENDING_ASSIGNMENT("待分派"),
        PENDING_ACCEPTANCE("待接收"),
        ACCEPTED("已接收"),
        IN_PROGRESS("执行中"),
        COMPLETED("已完成");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    private final String taskId;
    private final String deviceId;
    private final String alarmId;
    private final String reason;
    private final LocalDateTime createTime;
    private volatile String handler;
    private volatile Status status;
    private volatile LocalDateTime finishTime;
    private volatile String result;

    public MaintenanceTask(String deviceId, String alarmId, String reason) {
        this.taskId = "MNT_" + SEQUENCE.incrementAndGet();
        this.deviceId = requireText(deviceId, "设备编号不能为空");
        this.alarmId = normalizeOptional(alarmId);
        this.reason = requireText(reason, "报修原因不能为空");
        this.createTime = LocalDateTime.now();
        this.status = Status.PENDING_ASSIGNMENT;
    }

    /** 待分派 -> 待接收。 */
    public synchronized boolean assign(String handler) {
        if (status != Status.PENDING_ASSIGNMENT || handler == null || handler.isBlank()) {
            return false;
        }
        this.handler = handler.trim();
        this.status = Status.PENDING_ACCEPTANCE;
        return true;
    }

    /** 待接收 -> 已接收。 */
    public synchronized boolean accept() {
        if (status != Status.PENDING_ACCEPTANCE) {
            return false;
        }
        status = Status.ACCEPTED;
        return true;
    }

    /** 已接收 -> 执行中。 */
    public synchronized boolean start() {
        if (status != Status.ACCEPTED) {
            return false;
        }
        status = Status.IN_PROGRESS;
        return true;
    }

    /** 执行中 -> 已完成，并保存处理结果和完成时间。 */
    public synchronized boolean complete(String result) {
        if (status != Status.IN_PROGRESS || result == null || result.isBlank()) {
            return false;
        }
        this.result = result.trim();
        this.finishTime = LocalDateTime.now();
        this.status = Status.COMPLETED;
        return true;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

        

    public String getTaskId() {
        return taskId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public String getHandler() {
        return handler;
    }

    public String getStatus() {
        return status.getLabel();
    }

    public Status getStatusEnum() {
        return status;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public String getResult() {
        return result;
    }
}
