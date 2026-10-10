package exceptions;

/**
 * 设备 ID 格式非法时抛出。
 * 与 DeviceNotFoundException 区分：
 * 这个异常表示"格式本身就错了"，根本不该去查数组；
 * 那个表示"格式正确，但库里没有这台设备"。
 * 合法格式示例：PU_1、PS_3（前缀_正整数）。
 */
public class InvalidDeviceIdException extends OilfieldException {

    public InvalidDeviceIdException(String id, String reason) {
        super("设备ID格式错误 [" + id + "]：" + reason + "（正确格式如 PU_1、PS_3）");
    }
}
