package exceptions;

/**
 * 对未启动的设备执行采集、停机等操作时抛出（FR-MON-01）。
 * 比如：抽油机还是"尚未启动"状态，就调用 collectData() 采数据。
 */
public class DeviceNotRunningException extends OilfieldException {

    public DeviceNotRunningException(String deviceId, String operation) {
        super("设备 " + deviceId + " 尚未启动，无法执行操作：" + operation);
    }
}
