package interfaces;

/**
 * 运维能力（FR-MNT）。
 * 可维修的设备实现此接口，报警模块发现严重异常时可调用
 * requestMaintenance() 自动生成维修任务（模块间解耦的关键）。
 */
public interface Maintainable {

    /**
     * 为设备创建一条维修工单。
     * @param reason 报修原因（通常来自 AlarmException 的信息）
     * @return 工单编号
     */
    String requestMaintenance(String reason);

    /** 设备是否正在维修中。 */
    boolean isUnderMaintenance();
}
