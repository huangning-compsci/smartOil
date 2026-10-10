package devices.FluidTransportDevices;

import devices.DeviceArray;

import exceptions.AlarmException;
import exceptions.DeviceNotRunningException;
import interfaces.Alarmable;
import interfaces.Collectable;

/**
 * 离心泵：实现 Collectable + Alarmable 两个接口。
 *
 * 修复说明（"报不了警"的根因）：
 *   1) 原类没有 implements Alarmable，巡检时被 instanceof 过滤掉；
 *   2) 原 checkAlarm() 用 return false 表示异常，但报警链路只认
 *      "抛出 AlarmException"，返回值没有任何人接收；
 *   3) 原 collectData() 里 (int)Math.random() 恒等于 0（(int) 先作用于
 *      Math.random()），进口压力永远是 0.1，随机数根本没生效。
 *
 * FR-ALM-01 规则：出口压力 < 额定扬程对应的最低压力 -> 报警。
 */
public class CentrifugalPump extends FluidTransportEquipment
        implements Collectable, Alarmable {

    /** 扬程-压力换算系数（保留原设计的 1.176798）。 */
    private static final double HEAD_COEF = 1.176798;
    /** 额定扬程对应的最低扬程比例（保留原设计的 0.8）。 */
    private static final double MIN_HEAD_RATIO = 0.8;

    private double ratedFlow;
    private double ratedHead;
    private double inletPressure;
    private boolean hasInletPressureData;
    private boolean hasOutletPressureData;
    private java.time.LocalDateTime lastCollectedTime;
    private double outletPressure;
    private static int index;
    private static final String NAME = "CP";
    private static final DeviceArray<CentrifugalPump> DEVICES =
            new DeviceArray<>(new CentrifugalPump[10]);

    public CentrifugalPump() {
        super(
            NAME + '_' + (++index),
            "CentrifugalPump",
            "",
            "",
            "",
            "ratedFlow,ratedHead,inletPressure,outletpressure"
        );
        this.ratedFlow = 50.0;
        this.ratedHead = 120.0;
    }

    public CentrifugalPump(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ) {
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);
    }

    public static DeviceArray<CentrifugalPump> getDevices() {
        return DEVICES;
    }

    /**
     * FR-MON-01 模拟数据采集。
     * 修复原 bug：(int)Math.random() 恒为 0，应写成 (int)(Math.random()*范围+基数)。
     * 约 20% 概率出口压力跌落（扬程不足），用于演示报警。
     */
    @Override
    public void collectData() throws DeviceNotRunningException {
        if ("尚未启动".equals(getStatus())) {
            throw new DeviceNotRunningException(getId(), "泵压采集");
        }
        this.inletPressure = (int) (Math.random() * 21 + 10) / 100.0;   // 0.10~0.30 MPa
        double k;
        if (Math.random() < 0.8) {
            k = (int) (Math.random() * 21 + 90) / 100.0;                // 0.90~1.10 正常扬程
        } else {
            k = (int) (Math.random() * 30 + 50) / 100.0;                // 0.50~0.79 扬程不足
        }
        this.outletPressure = inletPressure + k * HEAD_COEF;
        this.hasInletPressureData = true;
        this.hasOutletPressureData = true;
        this.lastCollectedTime = java.time.LocalDateTime.now();

    }

    @Override
    public boolean hasCurrentData() {
        return hasInletPressureData && hasOutletPressureData;
    }

    @Override
    public java.time.LocalDateTime getLastCollectedTime() {
        return lastCollectedTime;
    }

    /** 单设备实时详情：检查已有数据，展示异常原因，不生成报警记录。 */
    @Override
    public String collectSummary() {
        String data =
                (hasInletPressureData ? String.format(java.util.Locale.ROOT, "进口压力：%.3f MPa", inletPressure) : "进口压力：尚无数据")
                + System.lineSeparator() +
                (hasOutletPressureData ? String.format(java.util.Locale.ROOT, "出口压力：%.3f MPa", outletPressure) : "出口压力：尚无数据");
        String time = "采集时间：尚未采集";
        if (hasInletPressureData || hasOutletPressureData) {
            time = lastCollectedTime == null ? "数据来源：手动设置（无采集时间）"
                    : "最近采集时间：" + lastCollectedTime.format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        String detection = "检测结果：未检测（数据尚不完整）";
        if (hasInletPressureData && hasOutletPressureData) {
            try {
                checkAlarm();
                detection = "检测结果：正常（当前数据未触发报警规则）";
            } catch (AlarmException e) {
                detection = "检测结果：异常\n报警等级：" + e.getLevel().getLabel()
                        + "\n异常原因：" + e.getDescription();
            }
        }
        return String.format(java.util.Locale.ROOT,
                "离心泵实时详情%n"
                + "【基本信息】%n"
                + "设备编号：%s%n设备类型：%s%n所属井场：%s%n"
                + "设备型号：%s%n安装日期：%s%n运行状态：%s%n"
                + "【当前数据】%n%s%n%s%n"
                + "【设备参数与报警规则】%n"
                + "额定流量：%.2f%n额定扬程：%.2f%n二级报警：出口压力 < %.3f MPa%n"
                + "【基于当前数据的检测结果】%n%s",
                getId(), getType(), getWellsite(), getModel(), getInstallDate(),
                getStatus(), data, time, ratedFlow, ratedHead, inletPressure + MIN_HEAD_RATIO * HEAD_COEF, detection);
    }

    /** FR-ALM-01 异常检测：出口压力低于额定扬程对应的最低压力 -> 报警（疑似汽蚀/抽空）。 */
    @Override
    public boolean checkAlarm() throws AlarmException {
        double minOutlet = inletPressure + MIN_HEAD_RATIO * HEAD_COEF;
        if (outletPressure < minOutlet) {
            throw new AlarmException(getId(), AlarmException.Level.WARNING,
                    String.format("出口压力 %.3f MPa 低于额定扬程对应的最低压力 %.3f MPa，疑似汽蚀或抽空",
                            outletPressure, minOutlet));
        }
        return true;
    }

    public double getRatedFlow() { return ratedFlow; }
    public void setRatedFlow(double ratedFlow) { this.ratedFlow = ratedFlow; }
    public double getRatedHead() { return ratedHead; }
    public void setRatedHead(double ratedHead) { this.ratedHead = ratedHead; }
    public double getInletPressure() { return inletPressure; }
    public void setInletPressure(double inletPressure) {
        this.inletPressure = inletPressure;
        this.hasInletPressureData = true;
        this.lastCollectedTime = null;
    }
    public double getOutletPressure() { return outletPressure; }
    public void setOutletPressure(double outletPressure) {
        this.outletPressure = outletPressure;
        this.hasOutletPressureData = true;
        this.lastCollectedTime = null;
    }
}
