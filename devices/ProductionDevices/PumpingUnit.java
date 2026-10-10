package devices.ProductionDevices;

import java.util.Scanner;

import devices.DeviceArray;

import exceptions.AlarmException;
import exceptions.DeviceNotRunningException;
import interfaces.Alarmable;
import interfaces.Collectable;
import interfaces.Maintainable;

/**
 * 抽油机：实现 Collectable + Alarmable + Maintainable 三个接口。
 *
 * 修复说明（"报不了警"的根因之一）：
 *   1) 原类没有 implements Alarmable，FrUi.allAlarmables() 用 instanceof 过滤时
 *      会直接把它排除，巡检永远扫不到抽油机；
 *   2) 原 checkAlarm() 是空壳，永远 return true，从不抛 AlarmException；
 *   3) 原 collectData() 是空实现，currentLoad 永远是 0。
 *
 * FR-ALM-01 规则：当前载荷 > 额定载荷的 120% -> 一级报警（紧急）；
 *                 当前载荷 > 额定载荷        -> 二级报警（重要）。
 * Maintainable：支撑 FR-ALM-04 可选功能"报警确认后自动生成运维任务"。
 */
public class PumpingUnit extends ProductionEquipment
        implements Collectable, Alarmable, Maintainable {

    private double ratedPower;
    private double stroke;
    private double strokeRate;
    private double currentLoad;
    private boolean hasLoadData;
    private java.time.LocalDateTime lastCollectedTime;
    private double ratedLoad;
    private boolean underMaintenance;   // 是否维修中（Maintainable）
    private static int mntSeq;          // 运维工单编号自增序号
    private static final String NAME = "PU";
    private static int index;
    private static final DeviceArray<PumpingUnit> DEVICES =
        new DeviceArray<>(new PumpingUnit[10]);

    public PumpingUnit() {
        super(
            NAME + '_' + (++index),
            "PumpingUnit",
            "",
            "",
            "",
            "stroke,strokeRate,currentLoad,ratedLoad,ratedPower"
        );
        this.ratedPower = 75.0;
        this.ratedLoad = 160.0;
    }

    public PumpingUnit(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ) {
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);
    }

    public static DeviceArray<PumpingUnit> getDevices() {
        return DEVICES;
    }

    /**
     * FR-MON-01 模拟数据采集：载荷在额定值的 60%~110% 之间波动，
     * 约 10% 概率超载到 120% 以上（便于演示报警）。
     * 未启动时抛 DeviceNotRunningException。
     */
    @Override
    public void collectData() throws DeviceNotRunningException {
        if ("尚未启动".equals(getStatus())) {
            throw new DeviceNotRunningException(getId(), "载荷采集");
        }
        double ratio = 0.6 + 0.5 * Math.random();          // 0.6~1.1 正常区间
        if (Math.random() < 0.1) {
            ratio = 1.2 + 0.2 * Math.random();             // 偶发超载 1.2~1.4
        }
        this.currentLoad = ratedLoad * ratio;
        this.hasLoadData = true;
        this.lastCollectedTime = java.time.LocalDateTime.now();

    }

    @Override
    public boolean hasCurrentData() {
        return hasLoadData;
    }

    @Override
    public java.time.LocalDateTime getLastCollectedTime() {
        return lastCollectedTime;
    }

    /** 单设备实时详情：检查已有数据，展示异常原因，不生成报警记录。 */
    @Override
    public String collectSummary() {
        String data = "当前载荷：尚无数据";
        String time = "采集时间：尚未采集";
        String detection = "检测结果：未检测（尚无载荷数据）";
        if (hasLoadData) {
            data = String.format(java.util.Locale.ROOT,
                    "当前载荷：%.1f kN", currentLoad);
            time = lastCollectedTime == null ? "数据来源：手动设置（无采集时间）"
                    : "最近采集时间：" + lastCollectedTime.format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            try {
                checkAlarm();
                detection = "检测结果：正常（当前载荷未超过额定载荷）";
            } catch (AlarmException e) {
                detection = "检测结果：异常\n报警等级：" + e.getLevel().getLabel()
                        + "\n异常原因：" + e.getDescription();
            }
        }
        return String.format(java.util.Locale.ROOT,
                "抽油机实时详情%n"
                + "【基本信息】%n"
                + "设备编号：%s%n设备类型：%s%n所属井场：%s%n"
                + "设备型号：%s%n安装日期：%s%n运行状态：%s%n维修状态：%s%n"
                + "【当前数据】%n%s%n%s%n"
                + "【设备参数与报警规则】%n"
                + "额定功率：%.1f kW%n冲程：%.2f%n冲次：%.2f%n"
                + "额定载荷：%.1f kN%n"
                + "二级报警：%.1f kN < 载荷 <= %.1f kN%n"
                + "一级报警：载荷 > %.1f kN%n"
                + "【基于当前数据的检测结果】%n%s",
                getId(), getType(), getWellsite(), getModel(), getInstallDate(),
                getStatus(), underMaintenance ? "维修中" : "未处于维修中",
                data, time, ratedPower, stroke, strokeRate, ratedLoad,
                ratedLoad, ratedLoad * 1.2, ratedLoad * 1.2, detection);
    }

    /** FR-ALM-01 异常检测：超载 120% 严重报警，超载 100% 警告。 */
    @Override
    public boolean checkAlarm() throws AlarmException {
        int num=Character.getNumericValue(getStatus().charAt(0));
        runTimeRemain-=rt.getTime()*num;
        if(runTimeRemain<60){
            System.out.println("请立即停止"+getId()+",否则机器将会过热");
        }

        if (currentLoad > ratedLoad * 1.2) {
            throw new AlarmException(getId(), AlarmException.Level.CRITICAL,
                    String.format("载荷 %.1f kN 超过额定载荷的 120%%（额定 %.1f kN），有断杆风险",
                            currentLoad, ratedLoad));
        }
        if (currentLoad > ratedLoad) {
            throw new AlarmException(getId(), AlarmException.Level.WARNING,
                    String.format("载荷 %.1f kN 超过额定载荷 %.1f kN", currentLoad, ratedLoad));
        }
        return true;
    }

    /** FR-MNT-01 / FR-ALM-04：为设备创建维修工单，返回工单编号。 */
    @Override
    public String requestMaintenance(String reason) {
        this.underMaintenance = true;
        String orderId = "MNT_" + (++mntSeq);
        System.out.println("已自动生成运维工单 " + orderId + "：设备 " + getId()
                + "，报修原因：" + reason);
        return orderId;
    }

    @Override
    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void run(Scanner sc){
        System.out.println("请选择档位:1.慢速/2.正常/3.全速(输入对应数字)");
        switch (sc.next()) {
            case "1":
                Switch("1档");
                rt.start();
                break;
            case "2":
                Switch("2档");
                rt.start();
                break;
            case "3":
                Switch("3档");
                rt.start();
                break;
            default:
                System.out.println("请输入正确的数字!已默认设置为正常工作档位");
                Switch("2档");
                rt.start();
                break;
        }
    }

    public void stopRun(){
        rt.stop();
        setStatus("尚未启动");
        System.out.println("成功关闭"+getId());
    }

    //未加入实际应用,与时间判断有关
    public boolean Switch(String status){
        if(status=="1档"){
            int num=Character.getNumericValue(getStatus().charAt(0));
            runTimeRemain-=rt.getTime()*num;
            setStatus(status);
            System.out.println(getId()+"已调至慢速工作");

            return true;
        }else if(status=="2档"){
            int num=Character.getNumericValue(getStatus().charAt(0));
            runTimeRemain-=rt.getTime()*num;
            setStatus(status);
            System.out.println(getId()+"已调至正常速度工作");
            return true;
        }else if(status=="3档"){
            int num=Character.getNumericValue(getStatus().charAt(0));
            runTimeRemain-=rt.getTime()*num;
            setStatus(status);
            System.out.println(getId()+"已调至全速工作");
            return true;
        }else{
            System.out.println("出现错误,不存在的档位");
            return false;
        }
    }

    
    
    
    
    public double getRatedPower() { return ratedPower; }
    public void setRatedPower(double ratedPower) { this.ratedPower = ratedPower; }
    public double getStroke() { return stroke; }
    public void setStroke(double stroke) { this.stroke = stroke; }
    public double getStrokeRate() { return strokeRate; }
    public void setStrokeRate(double strokeRate) { this.strokeRate = strokeRate; }
    public double getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(double currentLoad) {
        this.currentLoad = currentLoad;
        this.hasLoadData = true;
        this.lastCollectedTime = null;
    }
    public double getRatedLoad() { return ratedLoad; }
    public void setRatedLoad(double ratedLoad) { this.ratedLoad = ratedLoad; }
}
