import java.util.Scanner;

import devices.FluidTransportDevices.CentrifugalPump;
import devices.FluidTransportDevices.Compressor;
import devices.DeviceArray;
import devices.Equipment;
import devices.MonitoringDevices.FlowMeter;
import devices.MonitoringDevices.PressureSensor;
import devices.ProductionDevices.PumpingUnit;
import devices.MonitoringDevices.TemperatureSensor;
import exceptions.DeviceArrayFullException;
import exceptions.DeviceNotFoundException;
import exceptions.InvalidDeviceIdException;

/**
 * 设备管理业务层（异常化改造版）
 * 原则：业务层只 throw，错误提示由 UI 层（FrUi）catch 后统一打印。
 */
public class FrDev {

    /** 新增设备：数组满时抛 DeviceArrayFullException。 */
    static <T extends Equipment> void add(DeviceArray<T> target, T device)
            throws DeviceArrayFullException {
        target.store(device);
    }

    /** 一键新增 20 台预设设备，容量不足时停止并抛出异常。 */
    static void add20Devices() throws DeviceArrayFullException {
        add(PumpingUnit.getDevices(), new PumpingUnit("01", "2026-01-10", "CYJ10-3-53HB"));
        add(PumpingUnit.getDevices(), new PumpingUnit("02", "2026-01-12", "CYJ12-4.8-73HB"));
        add(PumpingUnit.getDevices(), new PumpingUnit("03", "2026-01-15", "CYJ14-5-89HB"));
        add(PumpingUnit.getDevices(), new PumpingUnit("04", "2026-01-18", "CYJ16-6-109HB"));

        add(CentrifugalPump.getDevices(), new CentrifugalPump("01", "2026-02-01", "IS50-32-125"));
        add(CentrifugalPump.getDevices(), new CentrifugalPump("02", "2026-02-03", "IS65-50-160"));
        add(CentrifugalPump.getDevices(), new CentrifugalPump("03", "2026-02-05", "IS80-65-200"));
        add(CentrifugalPump.getDevices(), new CentrifugalPump("04", "2026-02-07", "IS100-80-250"));

        add(PressureSensor.getDevices(), new PressureSensor("01", "2026-03-01", "PT-100"));
        add(PressureSensor.getDevices(), new PressureSensor("02", "2026-03-03", "PT-200"));
        add(PressureSensor.getDevices(), new PressureSensor("03", "2026-03-05", "PT-300"));

        add(TemperatureSensor.getDevices(), new TemperatureSensor("01", "2026-03-10", "WZP-230"));
        add(TemperatureSensor.getDevices(), new TemperatureSensor("02", "2026-03-12", "WZP-231"));
        add(TemperatureSensor.getDevices(), new TemperatureSensor("03", "2026-03-15", "WZP-240"));

        add(FlowMeter.getDevices(), new FlowMeter("01", "2026-04-01", "LWGY-25"));
        add(FlowMeter.getDevices(), new FlowMeter("02", "2026-04-03", "LWGY-50"));
        add(FlowMeter.getDevices(), new FlowMeter("03", "2026-04-05", "LWGY-80"));

        add(Compressor.getDevices(), new Compressor("01", "2026-04-10", "LG-3.6/8"));
        add(Compressor.getDevices(), new Compressor("02", "2026-04-12", "LG-6/8"));
        add(Compressor.getDevices(), new Compressor("03", "2026-04-15", "LG-10/8"));
    }

    /**
     * 生成设备信息表格（支持一次传多台设备）。
     * 注意：返回 String，调用方负责打印 —— System.out.println(FrDev.showInfo(device));
     */
    static String showInfo(Equipment... devices) {
        String[] headers = {"设备id", "安装日期", "目前状态", "设备型号", "所处井场"};
        String[][] rows = new String[devices.length][];
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = displayWidth(headers[i]);
        }
        int rowCount = 0;
        for (Equipment device : devices) {
            if (device == null) {
                continue;
            }
            String[] values = {device.getId(), device.getInstallDate(), device.getStatus(),
                    device.getModel(), device.getWellsite()};
            for (int i = 0; i < values.length; i++) {
                values[i] = String.valueOf(values[i]);
                widths[i] = Math.max(widths[i], displayWidth(values[i]));
            }
            rows[rowCount++] = values;
        }
        if (rowCount == 0) {
            return "";
        }
        StringBuilder headerRow = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            headerRow.append(headers[i]).append(" ".repeat(widths[i] + 4 - displayWidth(headers[i])));
        }
        StringBuilder table = new StringBuilder(headerRow.toString().stripTrailing());
        for (int r = 0; r < rowCount; r++) {
            StringBuilder valueRow = new StringBuilder();
            for (int i = 0; i < headers.length; i++) {
                valueRow.append(rows[r][i]).append(" ".repeat(widths[i] + 4 - displayWidth(rows[r][i])));
            }
            table.append(System.lineSeparator()).append(valueRow.toString().stripTrailing());
        }
        return table.toString();
    }

    // 等宽终端中，中文汉字和常用全角字符占两格，其余字符占一格。
    private static int displayWidth(String text) {
        return text.codePoints().map(c ->
                Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN
                || (c >= 0x3000 && c <= 0x303F)
                || (c >= 0xFF01 && c <= 0xFF60)
                || (c >= 0xFFE0 && c <= 0xFFE6) ? 2 : 1).sum();
    }

    /**
     * id 查询功能（异常化版本）。
     * 原来 return null 无法区分失败原因，现在：
     *   ID 为空/格式错/前缀不认识 -> InvalidDeviceIdException
     *   格式正确但库里没有       -> DeviceNotFoundException
     */
    static Equipment findById(String id)
            throws InvalidDeviceIdException, DeviceNotFoundException {

        if (id == null || id.isBlank()) {
            throw new InvalidDeviceIdException(String.valueOf(id), "ID 不能为空");
        }

        String[] parts = id.trim().split("_", -1);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new InvalidDeviceIdException(id, "必须形如 前缀_编号");
        }

        final int number;
        try {
            number = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new InvalidDeviceIdException(id, "编号必须是正整数");
        }
        if (number <= 0) {
            throw new InvalidDeviceIdException(id, "编号必须大于 0");
        }

        DeviceArray<? extends Equipment> pool = switch (parts[0]) {
            case "PU" -> PumpingUnit.getDevices();
            case "CP" -> CentrifugalPump.getDevices();
            case "PS" -> PressureSensor.getDevices();
            case "TS" -> TemperatureSensor.getDevices();
            case "FM" -> FlowMeter.getDevices();
            case "CM" -> Compressor.getDevices();
            default -> throw new InvalidDeviceIdException(id,
                    "未知设备前缀 " + parts[0] + "（可选 PU/CP/PS/TS/FM/CM）");
        };

        // 查询用 getOrNull：越界返回 null 而不是抛越界异常
        Equipment device = pool.getOrNull(number - 1);
        if (device == null) {
            throw new DeviceNotFoundException(id);
        }
        return device;
    }

    //删除设备
    static boolean deleteById(String id, Scanner sc)
            throws InvalidDeviceIdException, DeviceNotFoundException {

        Equipment device = findById(id);   // 查不到直接抛异常，走不到下面

        if ("投运中".equals(device.getStatus())) {
            System.out.println("设备[" + id + "]当前状态为:" + device.getStatus()
                    + "处于运行状态中，不能删除");
            return false;
        }
        // 显示设备详细信息
        System.out.println("找到设备：");
        System.out.println(showInfo(device));

        // 确认删除
        System.out.print("确定要删除该设备吗？(y/n)：");
        String choice = sc.nextLine().trim();

        if (!choice.equalsIgnoreCase("y")) {
            System.out.println("已取消删除！");
            return false;
        }

        // id 已通过 findById 校验，这里拆分一定安全
        String[] parts = id.trim().split("_");
        int index = Integer.parseInt(parts[1]) - 1;

        return switch (parts[0]) {
            case "PU" -> PumpingUnit.getDevices().remove(index);
            case "CP" -> CentrifugalPump.getDevices().remove(index);
            case "PS" -> PressureSensor.getDevices().remove(index);
            case "TS" -> TemperatureSensor.getDevices().remove(index);
            case "FM" -> FlowMeter.getDevices().remove(index);
            case "CM" -> Compressor.getDevices().remove(index);
            default -> false;   // 理论上到不了（findById 已拦下非法前缀）
        };
    }

    /** 设备信息修改与补充：逐项修改，空字符串表示不修改。 */
    static boolean modifyById(String id, String newWellsite, String newInstallDate,
            String newModel, String newStatus)
            throws InvalidDeviceIdException, DeviceNotFoundException {

        Equipment device = findById(id);   // 查不到直接抛异常

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

    static Equipment[] findByType(String type) {
        return devices.DeviceFilter.filter(allDevices(), devices.DeviceFilter.byType(type));
    }

    static Equipment[] findByStatus(String status) {
        return devices.DeviceFilter.filter(allDevices(), e -> e.getStatus().equals(status));
    }

    /** 模糊匹配编号、型号或井场；关键字不能为空。 */
    static Equipment[] fuzzyFind(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("关键字不能为空");
        }
        String key = keyword.trim().toLowerCase(java.util.Locale.ROOT);
        return devices.DeviceFilter.filter(allDevices(), e ->
                containsIgnoreCase(e.getId(), key)
                || containsIgnoreCase(e.getModel(), key)
                || containsIgnoreCase(e.getWellsite(), key));
    }

    private static boolean containsIgnoreCase(String value, String key) {
        return value != null && value.toLowerCase(java.util.Locale.ROOT).contains(key);
    }

    public static Equipment[] allDevices() {
        java.util.List<Equipment> list = new java.util.ArrayList<>();
        devices.DeviceArray<?>[] pools = {
                PumpingUnit.getDevices(), CentrifugalPump.getDevices(),
                PressureSensor.getDevices(), TemperatureSensor.getDevices(),
                FlowMeter.getDevices(), Compressor.getDevices()
        };
        for (devices.DeviceArray<?> pool : pools) {
            for (int i = 0; i < pool.size(); i++) {
                Equipment e = pool.getOrNull(i);
                if (e != null) {
                    list.add(e);
                }
            }
        }
        
        return list.toArray(new Equipment[0]);
        
    }
}
