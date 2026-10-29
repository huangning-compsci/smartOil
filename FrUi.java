import devices.FluidTransportDevices.CentrifugalPump;
import devices.FluidTransportDevices.Compressor;
import devices.MonitoringDevices.FlowMeter;
import devices.MonitoringDevices.PressureSensor;
import devices.ProductionDevices.PumpingUnit;
import devices.MonitoringDevices.TemperatureSensor;
import exceptions.AlarmException;
import exceptions.DeviceArrayFullException;
import exceptions.DeviceNotFoundException;
import exceptions.DeviceNotRunningException;
import exceptions.InvalidDeviceIdException;
import devices.Equipment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import Task.AlarmLog;
import Task.AlarmRecord;
import Task.MaintenanceTask;

public class FrUi {
    
    
    public static void waitForEnter(Scanner sc) {
        System.out.println("按回车返回");
        sc.nextLine();
    }

    static MonitorEngine toggleMonitoring(MonitorEngine current) {
        if (current != null && current.isRunning()) {
            current.stop();
            System.out.println("实时监控已关闭");
            return null;
        }
        Equipment[] currentDevices = FrDev.allDevices();
        if (currentDevices.length == 0) {
            System.out.println("当前没有设备，无法开启并发采集与实时监控");
            return null;
        }
        MonitorEngine next = new MonitorEngine();
        next.start(currentDevices);
        System.out.println("实时监控已开启；设备增删后请关闭并重新开启以刷新清单");
        return next;
    }

    
    private interface AddAction {  
        void accept(String wellsite, String installDate, String model) throws DeviceArrayFullException;
    }

    // 【修复缺陷1】所有输入增加取消退出机制，保留原有注释
    private static void addBatch(Scanner sc, String typeName, AddAction action) {
        int added = 0;
        String previousDate = null;
        while (true) {
            System.out.println("----- 录入第 " + (added + 1) + " 台" + typeName + " -----");
            String wellsite;
            do {
                System.out.println("请输入所属井场：(参考格式:01，输入0取消并返回)");
                wellsite = sc.nextLine().trim();
                if ("0".equals(wellsite)) {
                    System.out.println("已取消本次录入。");
                    return;
                }
                if (!wellsite.matches("\\d{2}")) System.out.println("格式错误！请按 xx 格式输入");
            } while (!wellsite.matches("\\d{2}"));
            String date;
            while (true) {
                System.out.println("请输入安装时间：(参考格式:xxxx-xx-xx"
                        + (previousDate == null ? "" : "，回车沿用 " + previousDate) + "，输入0取消)");
                date = sc.nextLine().trim();
                if ("0".equals(date)) {
                    System.out.println("已取消本次录入。");
                    return;
                }
                if (date.isEmpty() && previousDate != null) {
                    date = previousDate;
                    break;
                }
                if (date.matches("\\d{4}-\\d{2}-\\d{2}")) break;
                System.out.println("格式错误！请按 xxxx-xx-xx 格式输入");
            }
            String model;
            while (true) {
                System.out.println("请输入型号：(输入0取消)");
                model = sc.nextLine().trim();
                if ("0".equals(model)) {
                    System.out.println("已取消本次录入。");
                    return;
                }
                if (!model.isEmpty()) break;
            }
            try {
                action.accept(wellsite, date, model);
                previousDate = date;
                added++;
                System.out.println("添加成功（本次已添加 " + added + " 台" + typeName + "）");
            } catch (DeviceArrayFullException e) {
                System.out.println(e.getMessage() + "，无法继续添加");
                break;
            }
            System.out.println("是否继续添加" + typeName + "？(y/n)");
            String choice = sc.nextLine().trim();
            while (!"y".equalsIgnoreCase(choice) && !"n".equalsIgnoreCase(choice)) {
                System.out.println("请输入 y 或 n：");
                choice = sc.nextLine().trim();
            }
            if ("n".equalsIgnoreCase(choice)) break;
        }
        System.out.println("本次共添加 " + added + " 台" + typeName);
    }

    /** 运维任务交互层：只读取输入、调用 FrMnt 并展示结果。 */
    


    public static void main(String[] args){
        System.out.println("=".repeat(30));
        System.out.println("SmartOil 油气井管理系统 v1.0");

        Scanner sc=new Scanner(System.in);
        MonitorEngine monitorEngine = null;
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
                System.out.println("7.查看设备台账");
                System.out.println("8.排序查看");
                System.out.println("9.筛选查看");
                System.out.println("0.测试:一键新增20个设备(请勿重复使用)");

                System.out.println("=".repeat(30));

                System.out.println("请输入您的选择：");

                switch (sc.next()) {

                    case "1":
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
                                addBatch(sc, "抽油机", (wellsite, date, model) ->
                                        FrDev.add(PumpingUnit.getDevices(), new PumpingUnit(wellsite, date, model)));
                                continue1_1=false;
                                break;
                            }
                            case "2":
                            case "cp":
                            case "centrifugalpump":
                            case "离心水泵":
                            case "离心磅":
                            case "离心泵":{
                                sc.nextLine();
                                addBatch(sc, "离心泵", (wellsite, date, model) ->
                                        FrDev.add(CentrifugalPump.getDevices(), new CentrifugalPump(wellsite, date, model)));
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
                                addBatch(sc, "压力传感器", (wellsite, date, model) ->
                                        FrDev.add(PressureSensor.getDevices(), new PressureSensor(wellsite, date, model)));
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
                                addBatch(sc, "温度传感器", (wellsite, date, model) ->
                                        FrDev.add(TemperatureSensor.getDevices(), new TemperatureSensor(wellsite, date, model)));
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
                                addBatch(sc, "流量计", (wellsite, date, model) ->
                                        FrDev.add(FlowMeter.getDevices(), new FlowMeter(wellsite, date, model)));
                                continue1_1=false;
                                break;
                            }
                            case "6":
                            case "cm":
                            case "compressor":
                            case "压缩机":{
                                sc.nextLine();
                                addBatch(sc, "压缩机", (wellsite, date, model) ->
                                        FrDev.add(Compressor.getDevices(), new Compressor(wellsite, date, model)));
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
                            FrRpt.queryDevices(sc);
                            break;


                        case "3":
                        //设备信息修改与补充
                        continue1_3=true;
                        while (continue1_3) {
                            sc.nextLine();  // 吃掉上一轮残留的换行符
                            System.out.println("请输入设备id，例如 PU_1 (不区分大小写，输入0返回):");
                            String modifyId = sc.nextLine().trim();
                            if ("0".equals(modifyId)) {
                                continue1_3 = false;
                                break;
                            }

                            Equipment modifyDevice = null;
                            try{
                                modifyDevice=FrDev.findById(modifyId);
                            }catch(InvalidDeviceIdException e){
                                System.out.println("输入有误："+e.getMessage());
                            }catch(DeviceNotFoundException e){
                                System.out.println("未找到设备：" + modifyId);
                            }

                            boolean success = false;
                            if (modifyDevice != null) {
                                System.out.println("找到设备，当前信息如下：");
                                System.out.println(FrDev.showInfo(modifyDevice));
                                System.out.println("\t    信息修改");
                                System.out.print("所属井场（当前：" + modifyDevice.getWellsite() + "，回车跳过）：");
                                String newWellsite = sc.nextLine().trim();
                                System.out.print("安装日期（当前：" + modifyDevice.getInstallDate() + "，回车跳过）：");
                                String newInstallDate = sc.nextLine().trim();
                                System.out.print("设备型号（当前：" + modifyDevice.getModel() + "，回车跳过）：");
                                String newModel = sc.nextLine().trim();
                                String newStatus = FrDev.chooseDeviceStatus(sc, true);

                                try {
                                    success = FrDev.modifyById(modifyId, newWellsite, newInstallDate,
                                            newModel, newStatus);
                                } catch (InvalidDeviceIdException | DeviceNotFoundException e) {
                                    System.out.println(e.getMessage());
                                }
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
                                System.out.println(FrDev.showInfo(modifyDevice));
                                // 【修复缺陷4】增加询问
                                System.out.println("是否继续修改其他设备？(y/n)");
                                String cont = sc.nextLine().trim();
                                if (!"y".equalsIgnoreCase(cont)) {
                                    continue1_3 = false;
                                }
                            }
                        }
                        break;

                        case "4":
                            continue1_4=true;
                            while (continue1_4){

                                System.out.println("请输入设备id (输入0返回)");
                                String Id=sc.next();
                                sc.nextLine(); // 清理输入 ID 后的换行，等待用户确认删除
                                
                                if ("0".equals(Id)) {
                                    continue1_4 = false;
                                    break;
                                }

                                boolean Success=false;
                                try {
                                    Success=FrDev.deleteById(Id, sc);
                                } catch (InvalidDeviceIdException | DeviceNotFoundException e) {
                                    System.out.println(e.getMessage());
                                }
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
                                    // 【修复缺陷4】增加询问
                                    System.out.println("是否继续删除其他设备？(y/n)");
                                    String cont = sc.nextLine().trim();
                                    if (!"y".equalsIgnoreCase(cont)) {
                                        continue1_4 = false;
                                    }
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
                        case "7":
                            sc.nextLine();
                            System.out.println(FrDev.showInfo(FrDev.allDevices()));
                            System.out.println("按任意键返回");
                            sc.nextLine();
                            break;
                        case "8":
                            sc.nextLine();
                            FrRpt.sortDevices(sc);
                            break;
                        case "9":
                            sc.nextLine();
                            FrRpt.filterDevices(sc);
                            break;
                        case "0": {
                            sc.nextLine();
                            int before = FrDev.allDevices().length;
                            try {
                                FrDev.add20Devices();
                                System.out.println("成功新增20个设备！");
                            } catch (DeviceArrayFullException e) {
                                int added = FrDev.allDevices().length - before;
                                System.out.println("本次已新增" + added + "个设备，批量新增中止：" + e.getMessage());
                            }
                            System.out.println("按任意键返回");
                            sc.nextLine();
                            break;
                        }
                        default:
                            System.out.println("请输入正确的数字！");
                    }
            }
                    break;
                //此处case属于最大的switch
                case "2":{
                    
                    continue2_=true;
                    while (continue2_){
                        boolean continue2_1=true;
                
                        
                        System.out.println("\t   数据采集");
                        System.out.println("=".repeat(30));
                        System.out.println("1.手动单次采集");
                        System.out.println("2.查看最新采集数据");
                        System.out.println("3.单设备状态查看");
                        System.out.println("4." + (monitorEngine != null && monitorEngine.isRunning()
                                ? "关闭" : "开启") + "并发采集与实时监控");
                        System.out.println("5.一键启动设备");
                        System.out.println("6.一键关闭设备");
                        System.out.println("0.返回上一级");
                        
                        System.out.println("温馨提醒:采集数据之前要先确保设备已启动");
                        System.out.println("请输入你要执行的操作:");
                        System.out.println("-".repeat(30));


                        switch(sc.next()){
                            case  "0":{
                                continue2_=false;
                                break;
                            }
                            case  "1":{
                                continue2_1=true;
                                while(continue2_1){
                                    System.out.println("\t   数据采集");
                                    System.out.println("=".repeat(30));

                                    System.out.println("1.单机数据采集");
                                    System.out.println("2.批量数据采集");
                                    System.out.println("3.退出");
                                switch(sc.next()){
                                    case "1":{
                                        //单机数据采集：按 id 找到设备，多态调用它自己的 collectData()
                                        sc.nextLine();
                                        System.out.println("请输入设备id,例如 PS_1:");
                                        String collectId = sc.nextLine().trim();
                                        try {
                                            System.out.println(FrMon.collectById(collectId));
                                        } catch (DeviceNotRunningException e) {
                                            System.out.println(e.getMessage());
                                        } catch (InvalidDeviceIdException | DeviceNotFoundException e) {
                                            System.out.println(e.getMessage());
                                        }
                                        System.out.println("按任意键返回");
                                        sc.nextLine();
                                        break;
                                    }
                                    case "2":{
                                        //批量数据采集：接口静态方法统一调度，未启动的设备自动跳过
                                        FrMon.collectAll(FrDev.allDevices());
                                        sc.nextLine();
                                        System.out.println("按任意键返回");
                                        sc.nextLine();
                                        break;
                                    }
                                    case "3":{
                                        continue2_1=false;
                                        break;}
                                    default:{
                                        System.out.println("非法输入，跳转回上一页面");
                                        break;}
                                }


                                }

                               break;
                            }

                            case "2":{
                                sc.nextLine();
                                System.out.println("\t   实时状态台账");
                                System.out.println("=".repeat(30));
                                FrMon.showAll(FrDev.allDevices());
                                System.out.println("按任意键返回");
                                sc.nextLine();
                                break;
                            }

                            case "3":{
                                System.out.println("\t   单设备状态查看");
                                System.out.println("=".repeat(30));
                                System.out.println("请输入设备id,例如PU_1");
                                sc.nextLine(); // 消费菜单选项后的换行
                                String collectId =sc.nextLine().trim();
                                try {
                                    System.out.println(FrMon.showById(collectId));
                                } catch (InvalidDeviceIdException | DeviceNotFoundException e) {
                                    System.out.println(e.getMessage());
                                }
                                System.out.println("按任意键返回");
                                sc.nextLine();
                                break;

                               
                            }

                            case "4":{
                                boolean wasRunning = monitorEngine != null && monitorEngine.isRunning();
                                sc.nextLine();
                                monitorEngine = toggleMonitoring(monitorEngine);
                                if (!wasRunning && monitorEngine != null && monitorEngine.isRunning()) {
                                    System.out.println("按回车停止实时报警显示（后台并发采集和报警记录继续运行）");
                                    
                                    sc.nextLine();
                                    monitorEngine.stopRealtimeAlarmDisplay();
                                    System.out.println("实时报警显示已停止，后台监控仍在运行");
                                } else {
                                    waitForEnter(sc);
                                }
                                break;
                            }

                            case "5":{
                                sc.nextLine();
                                FrMon.turnOnAll(FrDev.allDevices());
                                System.out.println("已将所有处于'尚未启动'的设备设置为'投运中'");
                                System.out.println("按任意键返回");
                                sc.nextLine();
                                break;
                            }
                            
                            case "6":{
                                sc.nextLine(); 
                                FrMon.turnOffAll(FrDev.allDevices());
                                System.out.println("已将所有处于'投运中'的设备设置为'尚未启动'");
                                System.out.println("按任意键返回");
                                sc.nextLine();
                                break;
                            }

                            default:{
                                System.out.println("请输入正确的数字");
                                break;
                            }
                            
                        }
                        
                    }
                    break;
                }
                    
                
                case "3": {
    boolean continue3_=true;
    while (continue3_){
        System.out.println("\t   报警管理");
        System.out.println("=".repeat(30));
        System.out.println("1.全部设备报警巡检");
        System.out.println("2.单设备报警检测");
        System.out.println("3.报警记录查询");
        System.out.println("4.报警确认与处理");
        System.out.println("5.返回上一级菜单");
        System.out.println("请输入您的选择：");
        switch (sc.next()) {
            case "1": {
                // 先采集一轮最新数据再巡检：否则检测的是设备初始值（全 0），
                // PS/FM 会误报"低于下限"，TS 则永远"正常"
                Equipment[] patrolDevices = FrDev.allDevices();
                System.out.println("正在采集投运设备最新数据……");
                FrAlm.PatrolResult result = FrAlm.patrolWithResult(patrolDevices);
                System.out.println("巡检完成：参与 " + result.participating() + " 台，跳过 "
                        + result.skipped() + " 台，发现 " + result.alarms() + " 条报警");
                if (result.participating() == 0) {
                    boolean hasRunning = java.util.Arrays.stream(patrolDevices).anyMatch(Equipment::isRunning);
                    System.out.println(hasRunning ? "没有成功采集的设备，本轮未执行报警检测"
                            : "没有投运中的设备，本轮未执行报警检测");
                }
                sc.nextLine();
                waitForEnter(sc);
                break;
            }
            case "2": {
                sc.nextLine();
                System.out.println("请输入设备id,例如 PS_1:");
                String alarmId = sc.nextLine().trim();
                try {
                    System.out.println(FrAlm.checkAlarm(alarmId));
                } catch (AlarmException e) {
                    System.out.println(e.getMessage());
                } catch (DeviceNotRunningException e) {
                    System.out.println(e.getMessage());
                } catch (InvalidDeviceIdException | DeviceNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("按任意键继续");
                sc.next();sc.nextLine();
                break;
            }
            case "3": {
                // FR-ALM-03 报警查询：数据来源是巡检/检测时自动生成的报警记录
                System.out.println("\t   报警记录查询（结果按报警时间倒序）");
                System.out.println("1.查询全部");
                System.out.println("2.按报警状态筛选");
                System.out.println("3.按报警等级筛选");
                System.out.println("4.按设备编号查询");
                System.out.println("5.按时间范围查询");
                System.out.println("请输入您的选择：");
                switch (sc.next()) {
                    case "1":
                        AlarmLog.print(AlarmLog.query(null, null, null, null, null));
                        break;
                    case "2": {
                        AlarmRecord.Status st = FrAlm.chooseStatus(sc);
                        if (st != null) {
                            AlarmLog.print(AlarmLog.query(st, null, null, null, null));
                        }
                        break;
                    }
                    case "3": {
                        AlarmException.Level lv = FrAlm.chooseLevel(sc);
                        if (lv != null) {
                            AlarmLog.print(AlarmLog.query(null, lv, null, null, null));
                        }
                        break;
                    }
                    case "4": {
                        sc.nextLine();
                        System.out.println("请输入设备编号，例如 PS_1:");
                        String queryId = sc.nextLine().trim();
                        AlarmLog.print(AlarmLog.query(null, null, queryId, null, null));
                        break;
                    }
                    case "5": {
                        sc.nextLine();
                        System.out.println("请输入开始日期（yyyy-MM-dd，回车表示不限）：");
                        String fromStr = sc.nextLine().trim();
                        System.out.println("请输入结束日期（yyyy-MM-dd，回车表示不限）：");
                        String toStr = sc.nextLine().trim();
                        try {
                            LocalDateTime from = fromStr.isEmpty() ? null
                                    : LocalDate.parse(fromStr).atStartOfDay();
                            LocalDateTime to = toStr.isEmpty() ? null
                                    : LocalDate.parse(toStr).plusDays(1).atStartOfDay().minusSeconds(1);
                            AlarmLog.print(AlarmLog.query(null, null, null, from, to));
                        } catch (java.time.format.DateTimeParseException e) {
                            System.out.println("日期格式错误，请按 yyyy-MM-dd 输入");
                        }
                        break;
                    }
                    default:
                        System.out.println("请输入正确的数字！");
                        break;
                }
                waitForEnter(sc);
                break;
            }
            case "4": {
                // FR-ALM-04 报警确认与处理
                sc.nextLine();
                System.out.println("请输入报警编号，例如 ALM_1:");
                String handleId = sc.nextLine().trim();
                AlarmRecord rec = AlarmLog.findById(handleId);
                if (rec == null) {
                    System.out.println("未找到报警记录：" + handleId);
                } else {
                    System.out.println(rec.toDetailString());
                    System.out.println("-".repeat(30));
                    System.out.println("1.确认报警（未确认 -> 已确认）");
                    System.out.println("2.标记已处理（需先确认，填写处理说明）");
                    System.out.println("3.忽略误报（填写忽略原因）");
                    System.out.println("其他键.取消");
                    System.out.println("请输入您的选择：");
                    switch (sc.nextLine().trim()) {
                        case "1":
                            if (rec.confirm()) {
                                System.out.println("报警 " + handleId + " 已确认");
                                String maintenanceMessage = FrAlm.generateMaintenanceIfSupported(rec);
                                if (!maintenanceMessage.isEmpty()) {//找不到设备或设备不支持运维时返回对应文字
                                    System.out.println(maintenanceMessage);
                                }
                            } else {
                                System.out.println("操作失败：当前状态为「" + rec.getStatus()
                                        + "」，仅未确认报警可确认");
                            }
                            break;
                        case "2": {
                            System.out.println("请输入处理说明：");
                            String note = sc.nextLine().trim();
                            System.out.println(rec.handle(note)
                                    ? "报警 " + handleId + " 已标记为已处理"
                                    : "操作失败：需先确认报警，且处理说明不能为空（当前状态：" + rec.getStatus() + "）");
                            break;
                        }
                        case "3": {
                            System.out.println("请输入忽略原因：");
                            String reason = sc.nextLine().trim();
                            System.out.println(rec.ignore(reason)
                                    ? "报警 " + handleId + " 已忽略"
                                    : "操作失败：忽略原因不能为空，或该报警已终结（当前状态：" + rec.getStatus() + "）");
                            break;
                        }
                        default:
                            System.out.println("已取消");
                            break;
                    }
                }
                waitForEnter(sc);
                break;
            }
            case "5":
                continue3_=false;
                break;
            default:
                System.out.println("请输入正确的数字！");
                break;
        }
    }
    break;
}

            case "4": {
        sc.nextLine();
        boolean continue4_ = true;
        while (continue4_) {
            System.out.println("\t   运维任务管理");
            System.out.println("=".repeat(30));
            System.out.println("1.查看全部工单");
            System.out.println("2.按状态查询工单");
            System.out.println("3.人工创建工单");
            System.out.println("4.分派维修人员");
            System.out.println("5.接收工单");
            System.out.println("6.开始维修");
            System.out.println("7.完成工单");
            System.out.println("0.返回上一级");
            System.out.print("请输入您的选择：");

            switch (sc.nextLine().trim()) {
                case "1":
                    FrMnt.printMaintenanceTasks(FrMnt.getAll());
                    waitForEnter(sc);
                    break;
                case "2": {
                    MaintenanceTask.Status status = FrMnt.chooseMaintenanceStatus(sc);
                    if (status != null) {
                        FrMnt.printMaintenanceTasks(FrMnt.queryByStatus(status));
                    }
                    waitForEnter(sc);
                    break;
                }
                case "3": {
                    System.out.println("请输入设备编号：");
                    String deviceId = sc.nextLine().trim();
                    System.out.println("请输入报修原因：");
                    String reason = sc.nextLine().trim();
                    System.out.println("请选择报警等级");
                    AlarmException.Level level=FrAlm.chooseLevel(sc);
                    try {
                        FrDev.findById(deviceId);
                        MaintenanceTask task = FrMnt.createTask(deviceId, null, reason,level);
                        System.out.println("人工工单创建成功：" + task.getTaskId());
                    } catch (InvalidDeviceIdException | DeviceNotFoundException
                            | IllegalArgumentException e) {
                        System.out.println("创建失败：" + e.getMessage());
                    }
                    waitForEnter(sc);
                    break;
                }
                case "4": {
                    System.out.println("请输入工单编号：");
                    String taskId = sc.nextLine().trim();
                    System.out.println("请输入维修人员：");
                    String handler = sc.nextLine().trim();
                    MaintenanceTask task = FrMnt.findById(taskId);
                    System.out.println(FrMnt.assignTask(taskId, handler)
                            ? "工单已分派，等待维修人员接收"
                            : FrMnt.maintenanceFailure(task, "仅待分派工单可分派，维修人员不能为空"));
                    waitForEnter(sc);
                    break;
                }
                case "5": {
                    System.out.println("请输入工单编号：");
                    String taskId = sc.nextLine().trim();
                    MaintenanceTask task = FrMnt.findById(taskId);
                    System.out.println(FrMnt.acceptTask(taskId)
                            ? "工单已接收"
                            : FrMnt.maintenanceFailure(task, "仅待接收工单可接收"));
                    waitForEnter(sc);
                    break;
                }
                case "6": {
                    System.out.println("请输入工单编号：");
                    String taskId = sc.nextLine().trim();
                    MaintenanceTask task = FrMnt.findById(taskId);
                    System.out.println(FrMnt.startTask(taskId)
                            ? "维修已开始，设备状态已改为维修中"
                            : FrMnt.maintenanceFailure(task, "仅已接收且设备仍在库的工单可开始"));
                    waitForEnter(sc);
                    break;
                }
                case "7": {
                    System.out.println("请输入工单编号：");
                    String taskId = sc.nextLine().trim();
                    System.out.println("请输入处理结果：");
                    String result = sc.nextLine().trim();
                    MaintenanceTask task = FrMnt.findById(taskId);
                    System.out.println(FrMnt.completeTask(taskId, result)
                            ? "工单已完成，设备状态已改为尚未启动"
                            : FrMnt.maintenanceFailure(task, "仅执行中的工单可完成，处理结果不能为空"));
                    waitForEnter(sc);
                    break;
                }
                case "0":
                    continue4_ = false;
                    break;
                default:
                    System.out.println("请输入正确的数字！");
                    break;
            }
        }
                    break;
                }
                case "5":
            sc.nextLine();
            boolean continue5_=true;
            while(continue5_){
                System.out.println("\t   数据报表");
                System.out.println("=".repeat(30));
                System.out.println("1.设备统计");
                System.out.println("2.报警统计");
                System.out.println("3.运维统计");
                System.out.println("4.报表生成");
                System.out.println("0.返回上一级");
                System.out.println("请输入操作对应的数字:");
                switch (sc.next()) {
                    case "1":
                        System.out.println("\t   数据报表");
                        System.out.println("=".repeat(30));
                        System.out.println("1.按设备类型统计");
                        System.out.println("2.按设备状态统计");
                        System.out.println("3.设备可用率统计");
                        System.out.println("请输入数字:");
                        switch (sc.next()) {
                            case "1":
                                sc.nextLine();
                                System.out.println("请输入设备类型：");
                                Equipment[] result = FrDev.findByType(sc.nextLine().trim());
                                FrRpt.showResults(sc, result);
                                break;
                            case "2":
                                sc.nextLine();
                                FrRpt.filterDevices(sc);
                                break;
                            case "3":
                                sc.nextLine();
                                System.out.println("设备可用率为:"+
                                (double)FrDev.findByStatus("投运中").length/FrDev.allDevices().length);
                                waitForEnter(sc);
                                break;
                            default:
                                System.out.println("请输入正确的数字!");
                                waitForEnter(sc);
                                break;
                        }
                        break;
                    case "2":
                        //报警
                sc.nextLine();
                System.out.println("\t   报警统计");
                System.out.println("=".repeat(30));
                System.out.println("1.全部报警");
                System.out.println("2.按报警状态统计");
                System.out.println("3.按报警等级统计");
                System.out.println("4.按设备编号统计");
                System.out.println("5.按时间范围统计");
                System.out.println("6.报警处理率统计");
                System.out.println("请输入您的选择：");
                switch (sc.next()) {
                    case "1":
                        AlarmLog.print(AlarmLog.query(null, null, null, null, null));
                        break;
                    case "2": {
                        AlarmRecord.Status st = FrAlm.chooseStatus(sc);
                        if (st != null) {
                            AlarmLog.print(AlarmLog.query(st, null, null, null, null));
                        }
                        break;
                    }
                    case "3": {
                        AlarmException.Level lv = FrAlm.chooseLevel(sc);
                        if (lv != null) {
                            AlarmLog.print(AlarmLog.query(null, lv, null, null, null));
                        }
                        break;
                    }
                    case "4": {
                        sc.nextLine();
                        System.out.println("请输入设备编号，例如 PS_1:");
                        String queryId = sc.nextLine().trim();
                        AlarmLog.print(AlarmLog.query(null, null, queryId, null, null));
                        break;
                    }
                    case "5": {
                        sc.nextLine();
                        System.out.println("请输入开始日期（yyyy-MM-dd，回车表示不限）：");
                        String fromStr = sc.nextLine().trim();
                        System.out.println("请输入结束日期（yyyy-MM-dd，回车表示不限）：");
                        String toStr = sc.nextLine().trim();
                        try {
                            LocalDateTime from = fromStr.isEmpty() ? null
                                    : LocalDate.parse(fromStr).atStartOfDay();
                            LocalDateTime to = toStr.isEmpty() ? null
                                    : LocalDate.parse(toStr).plusDays(1).atStartOfDay().minusSeconds(1);
                            AlarmLog.print(AlarmLog.query(null, null, null, from, to));
                        } catch (java.time.format.DateTimeParseException e) {
                            System.out.println("日期格式错误，请按 yyyy-MM-dd 输入");
                        }
                        break;
                    }
                    case "6":
                        System.out.println("报警处理率为:"+AlarmLog.getRecordNumber());
                    default:
                        System.out.println("请输入正确的数字！");
                        break;
                }
                waitForEnter(sc);
                break;
                    case "3":
                         //运维
                        sc.nextLine();
                        System.out.println("\t   运维任务统计");
                        System.out.println("=".repeat(30));
                        System.out.println("1.按任务状态统计");
                        System.out.println("2.按任务类型统计");
                        System.out.println("3.任务完成率统计");
                        System.out.println("4.平均处理时长统计");
                        System.out.println("请输入您的选择：");
                        switch (sc.nextLine()) {
                            case "1":
                                sc.nextLine();
                                MaintenanceTask.Status status = FrMnt.chooseMaintenanceStatus(sc);
                                if (status != null) {
                                    FrMnt.printMaintenanceTasks(FrMnt.queryByStatus(status));
                                }
                                waitForEnter(sc);
                                break;
                            case "2":
                                sc.nextLine();
                                //按运维类型统计
                                AlarmException.Level level = FrAlm.chooseLevel(sc);
                                if (level != null) {
                                    FrMnt.printMaintenanceTasks(FrMnt.queryByLevel(level));
                                }
                                waitForEnter(sc);
                                break;
                            case "3":
                                sc.nextLine();
                                System.out.println("任务完成率为:"+FrMnt.getRate());
                                break;
                            case "4":
                                //平均处理时长统计
                                System.out.printf("平均处理时长为：%.2f 秒%n", FrMnt.getAverageProcessingTime());
                                waitForEnter(sc);
                                break;
                            default:
                                System.out.println("请输入正确的数字!");
                                break;
                        }
                       
                        break;
                    case "4":
                        sc.nextLine();
                        //报表生成
                        System.out.println(FrRpt.generateReport());
                        waitForEnter(sc);
                        break;
                    case "0":
                        continue5_ = false;
                        break;
                    default:
                        System.out.println("请输入正确的数字");
                        waitForEnter(sc);
                        break;
                }
            }
                    
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
        if (monitorEngine != null && monitorEngine.isRunning()) {
            monitorEngine.stop();
        }
        sc.close();
    }
}