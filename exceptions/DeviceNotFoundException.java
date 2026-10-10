package exceptions;

/**
 * 按 ID 查询不到设备时抛出（FR-DEV-02 / FR-DEV-04）。
 * 与 InvalidDeviceIdException 的区别：
 * 那个是"ID 格式本身就错了"，这个是"格式正确，但库里没有这台设备"。
 */
public class DeviceNotFoundException extends OilfieldException {

    private final String deviceId;

    public DeviceNotFoundException(String deviceId) {
        super("未找到设备：" + deviceId);
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
