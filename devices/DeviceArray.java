package devices;

import exceptions.DeviceArrayFullException;

/**
 * 改造后的设备容器：
 * - store 满员时抛 DeviceArrayFullException（原返回 false）
 * - get 越界时抛 IndexOutOfBoundsException（原返回 null，易被忽略）
 * - remove 语义不变，但补充注释说明
 */
public class DeviceArray<T extends Equipment> {

    private int count;
    private final T[] devices;

    public DeviceArray(T[] devices) {
        this.devices = devices;
        this.count = 0;
    }

    public void store(T device) throws DeviceArrayFullException {
        if (device == null) {
            throw new IllegalArgumentException("不能存入空设备");
        }
        if (count >= devices.length) {
            throw new DeviceArrayFullException(devices.length);
        }
        devices[count++] = device;
    }

    public T get(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException(
                "下标 " + index + " 越界，当前设备数量 " + count);
        }
        return devices[index];
    }

    /** 查询用 get：越界返回 null，供 findById 内部区分"编号超出范围"。 */
    public T getOrNull(int index) {
        if (index < 0 || index >= count) {
            return null;
        }
        return devices[index];
    }

    public boolean remove(int index) {
        if (index < 0 || index >= count || devices[index] == null) {
            return false;
        }
        devices[index] = null; // TODO: 后期加入位置复用
        return true;
    }

    public int size() {
        return count;
    }
}