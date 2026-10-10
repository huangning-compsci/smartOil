package exceptions;

/**
 * 录入重复设备时抛出（FR-DEV-01）。
 * 同一设备池中不允许出现相同 ID 的设备。
 */
public class DuplicateDeviceException extends OilfieldException {

    private final String deviceId;

    public DuplicateDeviceException(String deviceId) {
        super("设备 " + deviceId + " 已存在，禁止重复录入");
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
