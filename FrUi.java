import devices.CentrifugalPump;
import devices.Compressor;
import devices.FlowMeter;
import devices.PressureSensor;
import devices.PumpingUnit;
import devices.TemperatureSensor;
import devices.Equipment;
import java.util.Scanner;

public class FrUi{
    public static void main(String[] args){
        System.out.println("=".repeat(30));
        System.out.println("SmartOil 油气井管理系统 v1.0");
        
        Scanner sc=new Scanner(System.in);
        boolean CONTINUE=true;
        
        while (CONTINUE){
            boolean continue1_=true;
            boolean continue2_=true;
            boolean continue1_1=true;
            boolean continue1_3=true;
            boolean continue1_4=true;
            boolean continueD_=true;
            System.out.println("\t    首页");
            System.out.println("=".repeat(30));

            System.out.println("1.设备管理");
            System.out.println("2.数据采集与监控");
            System.out.println("3.报警管理");
            System.out.println("4.运维任务管理");
            System.out.println("5.数据统计与报表");
            System.out.println("0.退出系统");
            System.out.println("请输入您的选择：");
            switch (sc.next()) {
                case "1":
                continue1_=true;
                while(continue1_){
                
                
                System.out.println("\t   设备管理");
                System.out.println("=".repeat(30));
                
                System.out.println("1.新增设备");
                System.out.println("2.查询设备");
                System.out.println("3.修改或补充设备信息");
                System.out.println("4.删除设备");
                System.out.println("5.退出");
                System.out.println("6.返回上一级菜单");

                System.out.println("=".repeat(30));

                System.out.println("请输入您的选择：");
                
                switch (sc.next()) {
                    
                    case "1":
                        //加入容量不足的判断
                    continue1_1=true;
                    while (continue1_1){
                        
                        sc.nextLine();//吃掉换行符，学以致用
                        System.out.println("请输入：设备类型");
                        System.out.println("1.抽油机(PU)");
                        System.out.println("2.离心泵(CP)");
                        System.out.println("3.压力传感器(PS)");
                        System.out.println("4.温度传感器(TS)");
                        System.out.println("5.流量计(FM)");
                        System.out.println("6.压缩机(CM)");
                        System.out.println("-".repeat(30));
                        switch (sc.next().toLowerCase(java.util.Locale.ROOT)) {
                            case "1":
                            case "pu":
                            case "pumpingunit":
                            case "抽油机器":
                            case "抽油几":
                            case "抽油机":{
                                sc.nextLine();
                                String wellsite;
                                do {
                                    System.out.println("请输入所属井场：");
                                    wellsite = sc.nextLine().trim();
                                } while (wellsite.isEmpty());

                                String installDate;
                                do {
                                    System.out.println("请输入安装时间：");
                                    installDate = sc.nextLine().trim();
                                } while (installDate.isEmpty());

                                String model;
                                do {
                                    System.out.println("请输入型号：");
                                    model = sc.nextLine().trim();
                                } while (model.isEmpty());

                                PumpingUnit device = new PumpingUnit(wellsite, installDate, model);
                                boolean Success=FrDev.add(PumpingUnit.getDevices(),device);
                                System.out.println();
                                System.out.println (Success ? "添加成功" : "提交失败：数组已满");
                                continue1_1=false;
                                break;}
                            
                            case "2":
                            case "cp":
                            case "centrifugalpump":
                            case "离心水泵":
                            case "离心磅":
                            case "离心泵":{
                                sc.nextLine();
                                String wellsite;
                                do {
                                    System.out.println("请输入所属井场：");
                                    wellsite = sc.nextLine().trim();
                                } while (wellsite.isEmpty());

                                String installDate;
                                do {
                                    System.out.println("请输入安装时间：");
                                    installDate = sc.nextLine().trim();
                                } while (installDate.isEmpty());

                                String model;
                                do {
                                    System.out.println("请输入型号：");
                                    model = sc.nextLine().trim();
                                } while (model.isEmpty());

                                CentrifugalPump device = new CentrifugalPump(wellsite, installDate, model);
                                boolean Success=FrDev.add(CentrifugalPump.getDevices(),device);
                                System.out.println();
                                System.out.println(Success?"添加成功":"添加失败：数组已满");
                                continue1_1=false;
                                break;
                            }
                            case "3":
                            case "ps":
                            case "pressuresensor":
                            case "压力感应器":
                            case "压力传感":
                            case "压力传感器":{
                                sc.nextLine();
                                String wellsite;
                                do {
                                    System.out.println("请输入所属井场：");
                                    wellsite = sc.nextLine().trim();
                                } while (wellsite.isEmpty());

                                String installDate;
                                do {
                                    System.out.println("请输入安装时间：");
                                    installDate = sc.nextLine().trim();
                                } while (installDate.isEmpty());

                                String model;
                                do {
                                    System.out.println("请输入型号：");
                                    model = sc.nextLine().trim();
                                } while (model.isEmpty());

                                PressureSensor device = new PressureSensor(wellsite, installDate, model);
                                boolean Success=FrDev.add(PressureSensor.getDevices(),device);
                                System.out.println();
                                System.out.println(Success?"添加成功":"添加失败：数组已满");
                                continue1_1=false;
                                break;
                            }
                            case "4":
                            case "ts":
                            case "temperaturesensor":
                            case "温度感应器":
                            case "温度传感":
                            case "温度传感器":{
                                sc.nextLine();
                                String wellsite;
                                do {
                                    System.out.println("请输入所属井场：");
                                    wellsite = sc.nextLine().trim();
                                } while (wellsite.isEmpty());

                                String installDate;
                                do {
                                    System.out.println("请输入安装时间：");
                                    installDate = sc.nextLine().trim();
                                } while (installDate.isEmpty());

                                String model;
                                do {
                                    System.out.println("请输入型号：");
                                    model = sc.nextLine().trim();
                                } while (model.isEmpty());

                                TemperatureSensor device = new TemperatureSensor(wellsite, installDate, model);
                                boolean Success=FrDev.add(TemperatureSensor.getDevices(),device);
                                System.out.println();
                                System.out.println(Success?"添加成功":"添加失败：数组已满");
                                continue1_1=false;
                                break;
                            }
                            case "5":
                            case "fm":
                            case "flowmeter":
                            case "流量表":
                            case "流量记":
                            case "流量计":{
                                sc.nextLine();
                                String wellsite;
                                do {
                                    System.out.println("请输入所属井场：");
                                    wellsite = sc.nextLine().trim();
                                } while (wellsite.isEmpty());

                                String installDate;
                                do {
                                    System.out.println("请输入安装时间：");
                                    installDate = sc.nextLine().trim();
                                } while (installDate.isEmpty());

                                String model;
                                do {
                                    System.out.println("请输入型号：");
                                    model = sc.nextLine().trim();
                                } while (model.isEmpty());

                                FlowMeter device = new FlowMeter(wellsite, installDate, model);
                                boolean Success=FrDev.add(FlowMeter.getDevices(),device);
                                System.out.println();
                                System.out.println(Success?"添加成功":"添加失败：数组已满");
                                continue1_1=false;
                                break;
                            }
                            case "6":
                            case "cm":
                            case "compressor":
                            case "压缩机":{
                                sc.nextLine();
                                String wellsite;
                                do {
                                    System.out.println("请输入所属井场：");
                                    wellsite = sc.nextLine().trim();
                                } while (wellsite.isEmpty());

                                String installDate;
                                do {
                                    System.out.println("请输入安装时间：");
                                    installDate = sc.nextLine().trim();
                                } while (installDate.isEmpty());

                                String model;
                                do {
                                    System.out.println("请输入型号：");
                                    model = sc.nextLine().trim();
                                } while (model.isEmpty());

                                Compressor device = new Compressor(wellsite, installDate, model);
                                boolean Success=FrDev.add(Compressor.getDevices(),device);
                                System.out.println();
                                System.out.println(Success?"添加成功":"添加失败：数组已满");
                                continue1_1=false;
                                break;
                            }
                            default:
                                System.out.println("暂不支持该设备，是否回到首页重新选择");
            
                                System.out.println("1.重新输入");
                                System.out.println("2.退出");
                                System.out.println("3.返回上一级菜单");
                                
                                    switch (sc.next()) {
                                        case "1":
                                            
                                            break;
                                        case "2":
                                            continue1_1=false;
                                            continue1_=false;
                                            CONTINUE=false;
                                            break;
                                        case "3":
                                            continue1_1=false;
                                            continue1_=false;
                                            break;
                                        default:
                                        System.out.println("请输入正确的数字！");    
                                        break;
                                    
                                    
                                    }
                                }
                            }
                        
                            
                            break;
                        case "2":
                            sc.nextLine();
                            System.out.println("请输入设备id,例如 PU_1:");
                            String id=sc.nextLine();
                            
                            Equipment device =FrDev.findById(id);
                            if(device==null){
                                System.out.println("未找到相关设备");
                            }else{
                            System.out.println("-".repeat(30)+"设备信息"+"-".repeat(30));
                            FrDev.showInfo(device);  //改成编号为参数,泛型
                            System.out.println("-".repeat(68));
                            }
                            break;

                        case "3":
                        //设备信息修改与补充
                        continue1_3=true;
                        while (continue1_3) {
                            sc.nextLine();  // 吃掉上一轮残留的换行符
                            System.out.println("请输入设备id，例如 PU_1:");
                            String modifyId = sc.nextLine().trim();

                            Equipment modifyDevice = FrDev.findById(modifyId);
                            boolean success = false;
                            if (modifyDevice == null) {
                                System.out.println("未找到设备：" + modifyId);
                            } else {
                                System.out.println("找到设备，当前信息如下：");
                                FrDev.showInfo(modifyDevice);
                                System.out.println("\t    信息修改");
                                System.out.print("所属井场（当前：" + modifyDevice.getWellsite() + "，回车跳过）：");
                                String newWellsite = sc.nextLine().trim();
                                System.out.print("安装日期（当前：" + modifyDevice.getInstallDate() + "，回车跳过）：");
                                String newInstallDate = sc.nextLine().trim();
                                System.out.print("设备型号（当前：" + modifyDevice.getModel() + "，回车跳过）：");
                                String newModel = sc.nextLine().trim();
                                System.out.print("目前状态（当前：" + modifyDevice.getStatus() + "，回车跳过）：");
                                String newStatus = sc.nextLine().trim();

                                success = FrDev.modifyById(modifyId, newWellsite, newInstallDate,
                                        newModel, newStatus);
                            }

                            System.out.println();
                            System.out.println(success ? "设备 " + modifyId + " 修改成功！" : "设备修改失败！");

                        if (!success) {
                            System.out.println("*".repeat(30));
                            System.out.println("是否重新输入？");
                            System.out.println("1.重新输入");
                            System.out.println("2.退出系统");
                            System.out.println("3.返回上一级");
                            System.out.println("=".repeat(30));
                            System.out.println("请输入您的选择：");
                        switch (sc.next()) {
                        case "1":
                            break;
                        case "2":
                            continue1_3 = false;
                            continue1_ = false;
                            CONTINUE = false;
                            break;
                        case "3":
                            continue1_3 = false;
                            break;
                        default:
                            System.out.println("请输入正确的数字！");
                            break;
                        }
                        } else {
                            System.out.println("修改后的信息如下：");
                            FrDev.showInfo(modifyDevice);
                            continue1_3 = false;  // 修改成功，退出这个子循环
                        }
                   }
                        break;
                        
                        case "4":
                            continue1_4=true;
                            while (continue1_4){
                                
                                System.out.println("请输入设备id");
                                String Id=sc.next();
                
                                boolean Success=FrDev.deleteById(Id, sc);
                                System.out.println();
                                System.out.print(Success?"设备 " + Id + " 删除成功！":"设备删除失败！");
                                if(!Success){
                                    System.out.println("是否再次输入");
                                    System.out.println("1.再次输入");
                                    System.out.println("2.退出系统");
                                    System.out.println("3.返回上一级");
                                    System.out.println("=".repeat(30));
                                    System.out.println("请输入您的选择");
                                    switch (sc.next()) {
                                        case "1":
                                            
                                            break;
                                        case "2":
                                            continue1_4=false;
                                            continue1_=false;
                                            CONTINUE=false;
                                            break;
                                        case "3":
                                            continue1_4=false;
                                            break;
                                        default:
                                            System.out.println("请输入正确的数字");
                                            break;
                                    }
                                }else{
                                    System.out.println();
                                    continue1_4=false;
                                }
                            }
                            break;
                        
                        case "5":
                            continue1_=false;
                            CONTINUE=false;
                            break;
                        case "6":
                            continue1_=false;
                            break;
                        default:
                            System.out.println("请输入正确的数字！");
                    }
            }
                    break;
                //此处case属于最大的switch
                case "2":
                    //数据采集与监控    这段由Cr编写
                    continue2_=true;
                    while (continue2_){ 
                        boolean continue2_1=true;
                        boolean continue2_2=true;
                        boolean continue2_3=true;
                        boolean continue2_4=true;
                        sc.nextLine();//吃掉换行符，学以致用
                        System.out.println("请输入你要执行的操作");
                        System.out.println("1.数据采集");
                        System.out.println("2.实时状态");
                        System.out.println("3.单设备状态查看");
                        System.out.println("4.并发设备状态采集");
                        System.out.println("-".repeat(30));



                        switch(sc.next()){
                    
                            case  "1":{
                                continue2_1=true;
                                while(continue2_1){
                                    System.out.println("\t   数据采集");
                                    System.out.println("=".repeat(30));
            
                                    System.out.println("1.单机数据采集");
                                    System.out.println("2.批量数据采集");
                                    System.out.println("3.退出");
                                switch(sc.next()){
                                    case "1":
                                      //实现
        
                                    System.out.println("按任意键返回");
                                    sc.nextLine();
                                    break;
                                    case "2":
                                     //实现
        
                                    
                                    System.out.println("按任意键返回");
                                    sc.nextLine();
                                    break;
                                    case "3":
                                    continue2_1=false;   
                                    break;
                                    default:
                                    System.out.println("非法输入，跳转回上一页面");
                                    break;   
                                }
                            
        
                                }
                                
                               break;
                            }
        
                            case "2":{
                                System.out.println("\t   实时状态");
                                System.out.println("=".repeat(30));
                                //
                                
                                System.out.println("按任意键返回");  
                                sc.nextLine();                            
                            }
                            break;
                            case "3":{
                                System.out.println("\t   实时状态");
                                System.out.println("=".repeat(30));
                                //
                                
                                System.out.println("按任意键返回");
                                sc.nextLine();
                            }
                            break;
                            case "4":{
                                System.out.println("\t   实时状态");
                                System.out.println("=".repeat(30));
                                //
                                
                                System.out.println("按任意键返回");
                                sc.nextLine();
                            }
                            break;
                            }
                    }
                      
                 break;
                case "3":
                    //报警管理
                    break;
                case "4":
                    //运维任务管理
                    break;
                case "5":
                    //数据统计与报表
                    break;
                case "0":
                    //退出系统
                    CONTINUE=false;
                    break;
                default:
                    System.out.println("输入的数字有误，是否重新输入");
                    

                    System.out.println("1.重新输入");
                    System.out.println("2.退出");
                    continueD_ =true;
                    while(continueD_){
                        switch (sc.next()) {
                            case "1":
                                continueD_=false;
                                break;
                            case "2":
                                continueD_=false;
                                CONTINUE=false;
                                break;
                            default:
                                System.out.println("请输入正确的数字！");
                                break;
                        }
                    }
                    break;
            }

        }
        sc.close();
    }
}
