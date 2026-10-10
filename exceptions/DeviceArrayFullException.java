package exceptions;

/** 设备数组已满、无法继续录入时抛出（FR-DEV-01）。 */
public class DeviceArrayFullException extends OilfieldException {

    public DeviceArrayFullException(int capacity) {
        super("设备库已满（容量 " + capacity + "），请先删除无用设备或扩容");
    }
}