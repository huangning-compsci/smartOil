import java.util.Scanner;
import devices.FluidTransportDevices.CentrifugalPump;
import devices.FluidTransportDevices.Compressor;
import devices.DeviceArray;
import devices.MonitoringDevices.FlowMeter;
import devices.MonitoringDevices.PressureSensor;
import devices.ProductionDevices.PumpingUnit;
import devices.MonitoringDevices.TemperatureSensor;
import devices.Equipment;




public class FrDev{
    
    //添加的代码应该写成equipment.add(),然后每个子类的add都不一样
    //加入id查询系统，将参数改成id
   

    static <T extends Equipment> boolean add(
        DeviceArray<T> target,
        T device){
        
            return target.store(device);
        
    }

    static String showInfo(Equipment... devices){
        String[] headers={"设备id","安装日期","目前状态","设备型号","所处井场"};
        String[][] rows=new String[devices.length][];
        int[] widths=new int[headers.length];
        for(int i=0;i<headers.length;i++){
            widths[i]=displayWidth(headers[i]);
        }
        int rowCount=0;
        for(Equipment device:devices){
            if(device==null){
                continue;
            }
            String[] values={device.getId(),device.getInstallDate(),device.getStatus(),
                device.getModel(),device.getWellsite()};
            for(int i=0;i<values.length;i++){
                values[i]=String.valueOf(values[i]);
                widths[i]=Math.max(widths[i],displayWidth(values[i]));
            }
            rows[rowCount++]=values;
        }
        if(rowCount==0){
            return "";
        }
        StringBuilder headerRow=new StringBuilder();
        for(int i=0;i<headers.length;i++){
            headerRow.append(headers[i]).append(" ".repeat(widths[i]+4-displayWidth(headers[i])));
        }
        StringBuilder table=new StringBuilder(headerRow.toString().stripTrailing());
        for(int r=0;r<rowCount;r++){
            StringBuilder valueRow=new StringBuilder();
            for(int i=0;i<headers.length;i++){
                valueRow.append(rows[r][i]).append(" ".repeat(widths[i]+4-displayWidth(rows[r][i])));
            }
            table.append(System.lineSeparator()).append(valueRow.toString().stripTrailing());
        }
        return table.toString();
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
    
    
