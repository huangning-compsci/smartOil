package devices.MonitoringDevices;

import devices.DeviceArray;

import exceptions.AlarmException;
import exceptions.DeviceNotRunningException;
import interfaces.Alarmable;
import interfaces.Collectable;

/**
 * 温度传感器：实现 Collectable + Alarmable 两个接口。
 * 补全了原来的空壳 collectData()/checkAlarm()。
 */
public class TemperatureSensor extends MonitoringEquipment
        implements Collectable, Alarmable {

    private double tempRange;
    private String accuracy;
    private double currentTemp;
    private boolean hasCurrentTempData;
    private java.time.LocalDateTime lastCollectedTime;
    private double upperLimit;
    private static final String NAME = "TS";
    private static int index;
    private static final DeviceArray<TemperatureSensor> DEVICES =
        new DeviceArray<>(new TemperatureSensor[10]);

    public TemperatureSensor() {
        super(
            NAME + '_' + (++index),
            "TemperatureSensor",
            "",
            "",
            "",
            "tempRange,accuracy,currentTemp,upperLimit"
        );
        this.tempRange = 100.0;
        this.accuracy = "A级";
        this.upperLimit = 80.0;   // 默认报警上限 ℃
    }

    public TemperatureSensor(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ) {
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);
    }

    public static DeviceArray<TemperatureSensor> getDevices() {
        return DEVICES;
    }

    /** FR-MON-01 模拟数据采集：常温附近随机波动，约 10% 概率超温。 */
    @Override
    public void collectData() throws DeviceNotRunningException {
        if ("尚未启动".equals(getStatus())) {
            throw new DeviceNotRunningException(getId(), "温度采集");
        }
        double base = 20 + tempRange * 0.5 * Math.random();   // 20~70 ℃ 正常区间
        double spike = Math.random() < 0.1 ? 25 : 0;          // 偶发超温尖峰
        this.currentTemp = base + spike;
        this.hasCurrentTempData = true;
        this.lastCollectedTime = java.time.LocalDateTime.now();

    }

    @Override
    public boolean hasCurrentData() {
        return hasCurrentTempData;
    }

    @Override
    public java.time.LocalDateTime getLastCollectedTime() {
        return lastCollectedTime;
    }

    /** 单设备实时详情：检查已有数据，展示异常原因，不生成报警记录。 */
    @Override
    public String collectSummary() {
        String data =
                (hasCurrentTempData ? String.format(java.util.Locale.ROOT, "当前温度：%.1f ℃", currentTemp) : "当前温度：尚无数据");
        String time = "采集时间：尚未采集";
        if (hasCurrentTempData) {
            time = lastCollectedTime == null ? "数据来源：手动设置（无采集时间）"
                    : "最近采集时间：" + lastCollectedTime.format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        String detection = "检测结果：未检测（数据尚不完整）";
        if (hasCurrentTempData) {
            try {
                checkAlarm();
                detection = "检测结果：正常（当前数据未触发报警规则）";
            } catch (AlarmException e) {
                detection = "检测结果：异常\n报警等级：" + e.getLevel().getLabel()
                        + "\n异常原因：" + e.getDescription();
            }
        }
        return String.format(java.util.Locale.ROOT,
                "温度传感器实时详情%n"
                + "【基本信息】%n"
                + "设备编号：%s%n设备类型：%s%n所属井场：%s%n"
                + "设备型号：%s%n安装日期：%s%n运行状态：%s%n"
                + "【当前数据】%n%s%n%s%n"
                + "【设备参数与报警规则】%n"
                + "温度量程：%.1f ℃%n精度：%s%n二级报警：%.1f ℃ < 温度 <= %.1f ℃%n一级报警：温度 > %.1f ℃%n"
                + "【基于当前数据的检测结果】%n%s",
                getId(), getType(), getWellsite(), getModel(), getInstallDate(),
                getStatus(), data, time, tempRange, accuracy, upperLimit * 0.9, upperLimit, upperLimit, detection);
    }

    /** FR-ALM-01 异常检测：超上限严重报警，接近上限（90%）提前警告。 */
    @Override
    public boolean checkAlarm() throws AlarmException {
        if (currentTemp > upperLimit) {
            throw new AlarmException(getId(), AlarmException.Level.CRITICAL,
                    String.format("温度 %.1f ℃ 超过上限 %.1f ℃", currentTemp, upperLimit));
        }
        if (currentTemp > upperLimit * 0.9) {
            throw new AlarmException(getId(), AlarmException.Level.WARNING,
                    String.format("温度 %.1f ℃ 接近上限 %.1f ℃，请注意观察", currentTemp, upperLimit));
        }
        return true;
    }

    // getter/setter 与原文件相同
    public double getTempRange() { return tempRange; }
    public void setTempRange(double tempRange) { this.tempRange = tempRange; }
    public String getAccuracy() { return accuracy; }
    public void setAccuracy(String accuracy) { this.accuracy = accuracy; }
    public double getCurrentTemp() { return currentTemp; }
    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
        this.hasCurrentTempData = true;
        this.lastCollectedTime = null;
    }
    public double getUpperLimit() { return upperLimit; }
    public void setUpperLimit(double upperLimit) { this.upperLimit = upperLimit; }
}
