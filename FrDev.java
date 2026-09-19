import java.util.Scanner;
import devices.CentrifugalPump;
import devices.Compressor;
import devices.DeviceArray;
import devices.FlowMeter;
import devices.PressureSensor;
import devices.PumpingUnit;
import devices.TemperatureSensor;
import devices.Equipment;




public class FrDev{
    
    //添加的代码应该写成equipment.add(),然后每个子类的add都不一样
    //加入id查询系统，将参数改成id
   

    static <T extends Equipment> boolean add(
        DeviceArray<T> target,
        T device){
        
            return target.store(device);
        
    }

    static boolean showInfo(Equipment device){
        if(device==null){
            return false;
        }
        String[] headers={"设备id","安装日期","目前状态","设备型号","所处井场"};
        String[] values={device.getId(),device.getInstallDate(),device.getStatus(),
            device.getModel(),device.getWellsite()};
        StringBuilder headerRow=new StringBuilder();
        StringBuilder valueRow=new StringBuilder();
        for(int i=0;i<headers.length;i++){
            String value=String.valueOf(values[i]);
            int headerWidth=displayWidth(headers[i]);
            int valueWidth=displayWidth(value);
            int width=Math.max(headerWidth,valueWidth)+4;
            headerRow.append(headers[i]).append(" ".repeat(width-headerWidth));
            valueRow.append(value).append(" ".repeat(width-valueWidth));
        }
        System.out.println(headerRow.toString().stripTrailing());
        System.out.println(valueRow.toString().stripTrailing());
        return true;
        }

    // 等宽终端中，中文汉字和常用全角字符占两格，其余字符占一格。
    private static int displayWidth(String text){
        return text.codePoints().map(c ->
            Character.UnicodeScript.of(c)==Character.UnicodeScript.HAN
            || (c>=0x3000 && c<=0x303F)
            || (c>=0xFF01 && c<=0xFF60)
            || (c>=0xFFE0 && c<=0xFFE6) ? 2 : 1).sum();
    }
    //id查询功能
    static Equipment findById(String id){
        if (id==null){
            return null;
        }

        String[] parts=id.trim().split("_",-1);
        if (parts.length !=2){
            return null;
        }
        int number;
        try{
            number=Integer.parseInt(parts[1]);
        }catch (NumberFormatException e){
            return null;
        }

        if(number<=0){
            return null;
        }
        int index=number-1;

        switch (parts[0]) {
            case "PU":
                return PumpingUnit.getDevices().get(index);
            case "CP":
                return CentrifugalPump.getDevices().get(index);
            case "PS":
                return PressureSensor.getDevices().get(index);
            case "TS":
                return TemperatureSensor.getDevices().get(index);
            case "FM":
                return FlowMeter.getDevices().get(index);
            case "CM":
                return Compressor.getDevices().get(index);
            default:
                return null;
        }

        
    }

    //删除程序如下
    static boolean deleteById(String id,Scanner sc) {

        Equipment device = findById(id);

        // 查询不到设备
        if (device == null) {
            System.out.println("未找到设备：" + id);
            return false;
        }

        // 显示设备详细信息
        System.out.println("找到设备：");
        showInfo(device);

        // 确认删除
        System.out.print("确定要删除该设备吗？(y/n)：");

        
        String choice = sc.next();

        if (!choice.equalsIgnoreCase("y")) {
            System.out.println("已取消删除！");
            return false;
        }

        String[] parts = id.trim().split("_");
        int number = Integer.parseInt(parts[1]);
        int index = number - 1;

        boolean result = false;

        switch (parts[0]) {

            case "PU":
                result = PumpingUnit.getDevices().remove(index);
                break;

            case "CP":
                result = CentrifugalPump.getDevices().remove(index);
                break;

            case "PS":
                result = PressureSensor.getDevices().remove(index);
                break;

            case "TS":
                result = TemperatureSensor.getDevices().remove(index);
                break;

            case "FM":
                result = FlowMeter.getDevices().remove(index);
                break;

            case "CM":
                result = Compressor.getDevices().remove(index);
                break;

            default:
                System.out.println("设备ID格式错误！");
                return false;
        }

        

        return result;
    }
    //请黄宁同志思考，是否会出现数组越界

    //设备信息修改与补充代码如下
    static boolean modifyById(String id, String newWellsite, String newInstallDate,
            String newModel, String newStatus) {

    Equipment device = findById(id);

    // 查询不到设备
    if (device == null) {
        return false;
    }

    // 逐项修改，空字符串表示不修改
    if (!newWellsite.isEmpty()) {
        device.setWellsite(newWellsite);
    }

    if (!newInstallDate.isEmpty()) {
        device.setInstallDate(newInstallDate);
    }

    if (!newModel.isEmpty()) {
        device.setModel(newModel);
    }

    if (!newStatus.isEmpty()) {
        device.setStatus(newStatus);
    }

    return true;
}
}
    
    
