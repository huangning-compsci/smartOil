package exceptions;

/**
 * 数据采集失败异常（FR-MON-01）。
 * 用于"设备已启动但采集过程本身出错"的场景，例如：
 * 采集值超出物理量程、传感器读数校验失败、数据格式非法等。
 * 与 DeviceNotRunningException 区分：那个是"没启动不能采"，
 * 这个是"启动了但采回来的数据有问题"。
 */
public class DataCollectionException extends OilfieldException {

    private final String deviceId;
    private final double rawValue;

    public DataCollectionException(String deviceId, double rawValue, String reason) {
        super("设备 " + deviceId + " 采集数据异常：" + reason + "（原始读数 " + rawValue + "）");
        this.deviceId = deviceId;
        this.rawValue = rawValue;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public double getRawValue() {
        return rawValue;
    }
}
