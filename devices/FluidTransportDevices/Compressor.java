package devices.FluidTransportDevices;

import devices.DeviceArray;

import exceptions.AlarmException;
import exceptions.DeviceNotRunningException;
import interfaces.Alarmable;
import interfaces.Collectable;

/**
 * 压缩机：实现 Collectable + Alarmable 两个接口。
 *
 * 修复说明（"报不了警"的根因）：
 *   1) 原类没有 implements Alarmable，巡检时被 instanceof 过滤掉；
 *   2) 原 checkAlarm() 用 return false 表示异常，但报警链路只认
 *      "抛出 AlarmException"，返回值没有任何人接收；
 *   3) 原 collectData() 里 (int)Math.random() 恒等于 0，排气压力永远是 6.0。
 *
 * FR-ALM-01 规则：排气压力超出 6.5~8.0 MPa 安全范围 -> 严重报警；
 *                 排气流量低于 2.5 -> 警告。
 */
public class Compressor extends FluidTransportEquipment
        implements Collectable, Alarmable {

    private double exhaustPressure;
    private boolean hasExhaustPressureData;
    private boolean hasExhaustFlowData;
    private java.time.LocalDateTime lastCollectedTime;
    private double exhaustFlow;
    private double power;
    private static final String NAME = "CM";
    private static int index;
    private static final DeviceArray<Compressor> DEVICES =
        new DeviceArray<>(new Compressor[10]);

    public Compressor() {
        super(
            NAME + '_' + (++index),
            "Compressor",
            "",
            "",
            "",
            "exhaustPressure,exhaustFlow,power"
        );
        this.power = 37.0;
    }

    public Compressor(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ) {
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);
    }

    public static DeviceArray<Compressor> getDevices() {
        return DEVICES;
    }

    /**
     * FR-MON-01 模拟数据采集。
     * 修复原 bug：(int)Math.random() 恒为 0，应写成 (int)(Math.random()*范围+基数)。
     * 排气压力约 25% 概率越出 6.5~8.0 安全范围，用于演示报警。
     */
    @Override
    public void collectData() throws DeviceNotRunningException {
        if ("尚未启动".equals(getStatus())) {
            throw new DeviceNotRunningException(getId(), "排气参数采集");
        }
        if (Math.random() < 0.75) {
            this.exhaustPressure = (int) (Math.random() * 150 + 650) / 100.0;  // 6.50~8.00 正常
        } else {
            this.exhaustPressure = (int) (Math.random() * 160 + 600) / 100.0;  // 6.00~7.60 可能越限
        }
        this.exhaustFlow = (int) (Math.random() * 120 + 220) / 100.0;          // 2.20~3.40
        this.hasExhaustPressureData = true;
        this.hasExhaustFlowData = true;
        this.lastCollectedTime = java.time.LocalDateTime.now();

    }

    @Override
    public boolean hasCurrentData() {
        return hasExhaustPressureData && hasExhaustFlowData;
    }

    @Override
    public java.time.LocalDateTime getLastCollectedTime() {
        return lastCollectedTime;
    }

    /** 单设备实时详情：检查已有数据，展示异常原因，不生成报警记录。 */
    @Override
    public String collectSummary() {
        String data =
                (hasExhaustPressureData ? String.format(java.util.Locale.ROOT, "排气压力：%.2f MPa", exhaustPressure) : "排气压力：尚无数据")
                + System.lineSeparator() +
                (hasExhaustFlowData ? String.format(java.util.Locale.ROOT, "排气流量：%.2f", exhaustFlow) : "排气流量：尚无数据");
        String time = "采集时间：尚未采集";
        if (hasExhaustPressureData || hasExhaustFlowData) {
            time = lastCollectedTime == null ? "数据来源：手动设置（无采集时间）"
                    : "最近采集时间：" + lastCollectedTime.format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        String detection = "检测结果：未检测（数据尚不完整）";
        if (hasExhaustPressureData && hasExhaustFlowData) {
            try {
                checkAlarm();
                detection = "检测结果：正常（当前数据未触发报警规则）";
            } catch (AlarmException e) {
                detection = "检测结果：异常\n报警等级：" + e.getLevel().getLabel()
                        + "\n异常原因：" + e.getDescription();
            }
        }
        return String.format(java.util.Locale.ROOT,
                "压缩机实时详情%n"
                + "【基本信息】%n"
                + "设备编号：%s%n设备类型：%s%n所属井场：%s%n"
                + "设备型号：%s%n安装日期：%s%n运行状态：%s%n"
                + "【当前数据】%n%s%n%s%n"
                + "【设备参数与报警规则】%n"
                + "功率：%.1f kW%n一级报警：排气压力 < 6.5 MPa 或 > 8.0 MPa%n二级报警：排气流量 < 2.5%n"
                + "【基于当前数据的检测结果】%n%s",
                getId(), getType(), getWellsite(), getModel(), getInstallDate(),
                getStatus(), data, time, power, detection);
    }

    /** FR-ALM-01 异常检测：排气压力越限严重报警，排气流量过低警告。 */
    @Override
    public boolean checkAlarm() throws AlarmException {
        if (exhaustPressure < 6.5 || exhaustPressure > 8.0) {
            throw new AlarmException(getId(), AlarmException.Level.CRITICAL,
                    String.format("排气压力 %.2f MPa 超出安全范围 6.5~8.0 MPa", exhaustPressure));
        }
        if (exhaustFlow < 2.5) {
            throw new AlarmException(getId(), AlarmException.Level.WARNING,
                    String.format("排气流量 %.2f 低于下限 2.5", exhaustFlow));
        }
        return true;
    }

    public double getExhaustPressure() { return exhaustPressure; }
    public void setExhaustPressure(double exhaustPressure) {
        this.exhaustPressure = exhaustPressure;
        this.hasExhaustPressureData = true;
        this.lastCollectedTime = null;
    }
    public double getExhaustFlow() { return exhaustFlow; }
    public void setExhaustFlow(double exhaustFlow) {
        this.exhaustFlow = exhaustFlow;
        this.hasExhaustFlowData = true;
        this.lastCollectedTime = null;
    }
    public double getPower() { return power; }
    public void setPower(double power) { this.power = power; }
}
