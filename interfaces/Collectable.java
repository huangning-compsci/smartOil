package interfaces;

import exceptions.DeviceNotRunningException;

/**
 * 数据采集能力（FR-MON-01）。
 *
 * 注意：方法签名与 Equipment 的抽象方法保持一致（void 返回值），
 * 这样实现类用一个方法同时满足抽象类和接口。
 *
 * 包含评分细则要求的三类成员：
 *   抽象方法   collectData()
 *   default 方法 collectSummary()   —— 子类可重写，不重写就用默认实现
 *   static 方法 collectAll()        —— 批量采集，展示接口多态
 */
public interface Collectable {

    /**
     * 采集一次数据并写入设备自身的当前值字段。
     * @throws DeviceNotRunningException 设备未启动时抛出
     */
    void collectData() throws DeviceNotRunningException;

    /**
     * default 方法：采集后的摘要信息。
     * 所有实现类免费获得该行为；需要个性化摘要的类可重写。
     */
    default String collectSummary() {
        return "数据采集已完成";
    }

    /** 最近一次成功采集的时间；未采集或手动数据返回 null。 */
    default java.time.LocalDateTime getLastCollectedTime() {
        return null;
    }

    /** 是否有完整的当前采集数据。 */
    default boolean hasCurrentData() {
        return getLastCollectedTime() != null;
    }

    /** 查看已有数据台账，不执行采集。 */
    static void showAll(Collectable... devices) {
        printTable(false, devices);
    }

    /** 批量采集并以台账形式展示，不调用单设备详情摘要。 */
    static void collectAll(Collectable... devices) {
        printTable(true, devices);
    }

    private static void printTable(boolean collect, Collectable... devices) {
        String[] headers = {"设备编号", "设备类型", "井场", "运行状态", "关键采集数据", "采集时间", collect ? "采集结果" : "数据状态"};
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        for (Collectable d : devices) {
            if (d == null) {
                continue;
            }
            String data = "—";
            String time = "—";
            String result = "成功";
            try {
                if (collect) {
                    d.collectData();
                }
                if (!collect && !d.hasCurrentData()) {
                    result = "未采集或数据不完整";
                } else {
                    data = briefData(d);
                    java.time.LocalDateTime collectedTime = d.getLastCollectedTime();
                    time = collectedTime == null ? "未记录"
                            : collectedTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (!collect) {
                        result = collectedTime == null ? "手动数据" : "已有数据";
                    }
                }
            } catch (DeviceNotRunningException e) {
                result = "跳过：" + e.getMessage();
            }
            if (d instanceof devices.Equipment e) {
                rows.add(new String[]{e.getId(), e.getType(), e.getWellsite(), e.getStatus(), data, time, result});
            } else {
                rows.add(new String[]{"—", d.getClass().getSimpleName(), "—", "—", data, time, result});
            }
        }
        if (rows.isEmpty()) {
            System.out.println("暂无设备");
            return;
        }
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = displayWidth(headers[i]);
            for (String[] row : rows) {
                row[i] = String.valueOf(row[i]);
                widths[i] = Math.max(widths[i], displayWidth(row[i]));
            }
        }
        StringBuilder table = new StringBuilder(tableRow(headers, widths));
        for (String[] row : rows) {
            table.append(System.lineSeparator()).append(tableRow(row, widths));
        }
        System.out.println(table);
    }

    private static String briefData(Collectable device) {
        if (device instanceof devices.ProductionDevices.PumpingUnit p) {
            return String.format(java.util.Locale.ROOT, "载荷 %.1f kN", p.getCurrentLoad());
        }
        if (device instanceof devices.FluidTransportDevices.CentrifugalPump p) {
            return String.format(java.util.Locale.ROOT, "进口 %.3f MPa / 出口 %.3f MPa", p.getInletPressure(), p.getOutletPressure());
        }
        if (device instanceof devices.FluidTransportDevices.Compressor c) {
            return String.format(java.util.Locale.ROOT, "排气压力 %.2f MPa / 流量 %.2f", c.getExhaustPressure(), c.getExhaustFlow());
        }
        if (device instanceof devices.MonitoringDevices.PressureSensor p) {
            return String.format(java.util.Locale.ROOT, "压力 %.3f MPa", p.getCurrentPressure());
        }
        if (device instanceof devices.MonitoringDevices.TemperatureSensor t) {
            return String.format(java.util.Locale.ROOT, "温度 %.1f ℃", t.getCurrentTemp());
        }
        if (device instanceof devices.MonitoringDevices.FlowMeter f) {
            return String.format(java.util.Locale.ROOT, "瞬时流量 %.2f m³/h", f.getCurrentFlow());
        }
        return "采集完成";
    }

    private static String tableRow(String[] values, int[] widths) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            row.append(values[i]).append(" ".repeat(widths[i] + 4 - displayWidth(values[i])));
        }
        return row.toString().stripTrailing();
    }

    private static int displayWidth(String text) {
        return text.codePoints().map(c ->
                Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN
                || (c >= 0x3000 && c <= 0x303F)
                || (c >= 0xFF01 && c <= 0xFF60)
                || (c >= 0xFFE0 && c <= 0xFFE6) ? 2 : 1).sum();
    }
}