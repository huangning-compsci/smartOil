package devices;

public class DeviceArray<T extends Equipment> {
    private int count;
    private final T[] devices;
    

    DeviceArray(T[] devices){
        this.devices=devices;
        this.count=0;

    }

    public boolean store(T device){
        if(count>=devices.length){
            return false;
        }

        devices[count++]=device;
        return  true;
    }

    public T get(int index){
        if(index<0 ||index >=count){
            return  null;
        }
        return  devices[index];
    }

    public void fix(T device){
        //用于随机数
    }

    public boolean remove(int index) {
    if (index < 0 || index >= count || devices[index] == null) {
        return false;
    }

    devices[index] = null;//要加一些后期位置复用功能
    return true;
}
}
