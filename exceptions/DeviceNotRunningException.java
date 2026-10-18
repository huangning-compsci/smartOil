package exceptions;

/**
 * 对非投运状态的设备执行采集等操作时抛出（FR-MON-01）。
 */
public class DeviceNotRunningException extends OilfieldException {

    public DeviceNotRunningException(String deviceId, String operation) {
        super("设备 " + deviceId + " 不处于投运中状态，无法执行操作：" + operation);
    }
}
