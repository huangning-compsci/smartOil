package devices.MonitoringDevices;

import devices.DeviceArray;

import exceptions.AlarmException;
import exceptions.DeviceNotRunningException;
import interfaces.Alarmable;
import interfaces.Collectable;

/**
 * 流量计：实现 Collectable + Alarmable 两个接口。
 * 修复了原 collectData() 的 bug：
 *   (int)Math.random()*500 中 (int) 先作用于 Math.random()（恒为 0），
 *   导致 currentFlow 永远是 1.00；改为 (int)(Math.random()*500+100)/100.0。
 */
public class FlowMeter extends MonitoringEquipment
        implements Collectable, Alarmable {

    private double flowRange;
    private String accuracy;
    private double currentFlow;
    private boolean hasCurrentFlowData;
    private boolean hasTotalFlowData;
    private java.time.LocalDateTime lastCollectedTime;
    private double totalFlow;
    private static final String NAME = "FM";
    private static int index;
    private static final DeviceArray<FlowMeter> DEVICES =
        new DeviceArray<>(new FlowMeter[10]);

    public FlowMeter() {
        super(
            NAME + '_' + (++index),
            "FlowMeter",
            "",
            "",
            "",
            "flowRange,accuracy,currentFlow,totalFlow"
        );
        this.flowRange = 10.0;
        this.accuracy = "0.5级";
    }

    public FlowMeter(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ) {
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);
    }

    public static DeviceArray<FlowMeter> getDevices() {
        return DEVICES;
    }

    /** FR-MON-01 模拟数据采集：瞬时流量 1.00~6.00，并累加总流量。 */
    @Override
    public void collectData() throws DeviceNotRunningException {
        if ("尚未启动".equals(getStatus())) {
            throw new DeviceNotRunningException(getId(), "流量采集");
        }
        this.currentFlow = (int) (Math.random() * 500 + 100) / 100.0;
        this.totalFlow += currentFlow;
        this.hasCurrentFlowData = true;
        this.hasTotalFlowData = true;
        this.lastCollectedTime = java.time.LocalDateTime.now();

    }

    @Override
    public boolean hasCurrentData() {
        return hasCurrentFlowData && hasTotalFlowData;
    }

    @Override
    public java.time.LocalDateTime getLastCollectedTime() {
        return lastCollectedTime;
    }

    /** 单设备实时详情：检查已有数据，展示异常原因，不生成报警记录。 */
    @Override
    public String collectSummary() {
        String data =
                (hasCurrentFlowData ? String.format(java.util.Locale.ROOT, "瞬时流量：%.2f m³/h", currentFlow) : "瞬时流量：尚无数据")
                + System.lineSeparator() +
                (hasTotalFlowData ? String.format(java.util.Locale.ROOT, "累计流量：%.2f m³", totalFlow) : "累计流量：尚无数据");
        String time = "采集时间：尚未采集";
        if (hasCurrentFlowData || hasTotalFlowData) {
            time = lastCollectedTime == null ? "数据来源：手动设置（无采集时间）"
                    : "最近采集时间：" + lastCollectedTime.format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        String detection = "检测结果：未检测（数据尚不完整）";
        if (hasCurrentFlowData && hasTotalFlowData) {
            try {
                checkAlarm();
                detection = "检测结果：正常（当前数据未触发报警规则）";
            } catch (AlarmException e) {
                detection = "检测结果：异常\n报警等级：" + e.getLevel().getLabel()
                        + "\n异常原因：" + e.getDescription();
            }
        }
        return String.format(java.util.Locale.ROOT,
                "流量计实时详情%n"
                + "【基本信息】%n"
                + "设备编号：%s%n设备类型：%s%n所属井场：%s%n"
                + "设备型号：%s%n安装日期：%s%n运行状态：%s%n"
                + "【当前数据】%n%s%n%s%n"
                + "【设备参数与报警规则】%n"
                + "流量量程：%.2f m³/h%n精度：%s%n二级报警：瞬时流量 < 1.50 m³/h%n一级报警：瞬时流量 > 4.50 m³/h%n"
                + "【基于当前数据的检测结果】%n%s",
                getId(), getType(), getWellsite(), getModel(), getInstallDate(),
                getStatus(), data, time, flowRange, accuracy, detection);
    }

    /** FR-ALM-01 异常检测：保留原设计的 1.50~4.50 正常区间。 */
    @Override
    public boolean checkAlarm() throws AlarmException {
        if (currentFlow < 1.50) {
            throw new AlarmException(getId(), AlarmException.Level.WARNING,
                    String.format("流量 %.2f m³/h 过低，疑似管路堵塞或停输", currentFlow));
        }
        if (currentFlow > 4.50) {
            throw new AlarmException(getId(), AlarmException.Level.CRITICAL,
                    String.format("流量 %.2f m³/h 过高，超过安全输送能力", currentFlow));
        }
        return true;
    }

    // getter/setter 与原文件相同
    public double getFlowRange() { return flowRange; }
    public void setFlowRange(double flowRange) { this.flowRange = flowRange; }
    public String getAccuracy() { return accuracy; }
    public void setAccuracy(String accuracy) { this.accuracy = accuracy; }
    public double getCurrentFlow() { return currentFlow; }
    public void setCurrentFlow(double currentFlow) {
        this.currentFlow = currentFlow;
        this.hasCurrentFlowData = true;
        this.lastCollectedTime = null;
    }
    public double getTotalFlow() { return totalFlow; }
    public void setTotalFlow(double totalFlow) {
        this.totalFlow = totalFlow;
        this.hasTotalFlowData = true;
        this.lastCollectedTime = null;
    }
}
