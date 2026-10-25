import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Task.AlarmLog;
import Task.AlarmRecord;
import Task.MaintenanceTask;
import devices.Equipment;
import exceptions.AlarmException;
import exceptions.DeviceNotFoundException;
import exceptions.InvalidDeviceIdException;


public class FrRpt {

    /** 汇总当前内存中的设备、报警和运维数据，供 UI 显示。 */
    public static String generateReport() {
        Equipment[] devices = FrDev.allDevices();
        List<AlarmRecord> alarms = List.copyOf(AlarmLog.getAll());
        List<MaintenanceTask> tasks = FrMnt.getAll();
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(40)).append("\n智慧油气田综合报表\n生成时间：")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n统计范围：当前系统全部记录\n");

        report.append("\n【设备统计】\n设备总数：").append(devices.length).append(" 台\n");
        Map<String, Integer> types = new TreeMap<>();
        Map<String, Integer> states = new TreeMap<>();
        for (Equipment device : devices) {
            types.merge(device.getType(), 1, Integer::sum);
            states.merge(device.getStatus(), 1, Integer::sum);
        }
        types.forEach((type, count) -> report.append("类型 ").append(type).append("：").append(count).append(" 台\n"));
        states.forEach((state, count) -> report.append("状态 ").append(state).append("：").append(count).append(" 台\n"));
        report.append(String.format("设备可用率（投运中/总数）：%.2f%%%n",
                devices.length == 0 ? 0.0 : states.getOrDefault("投运中", 0) * 100.0 / devices.length));

        report.append("\n【报警统计】\n报警总数：").append(alarms.size()).append(" 条\n");
        long handled = 0;
        for (AlarmRecord.Status status : AlarmRecord.Status.values()) {
            long count = alarms.stream().filter(alarm -> alarm.getStatusEnum() == status).count();
            report.append(status.getLabel()).append("：").append(count).append(" 条\n");
            if (status == AlarmRecord.Status.HANDLED) handled = count;
        }
        for (AlarmException.Level level : AlarmException.Level.values()) {
            long count = alarms.stream().filter(alarm -> alarm.getLevelEnum() == level).count();
            report.append(level.getLabel()).append("报警：").append(count).append(" 条\n");
        }
        report.append(String.format("报警处理率（已处理/总数）：%.2f%%%n",
                alarms.isEmpty() ? 0.0 : handled * 100.0 / alarms.size()));

        report.append("\n【运维统计】\n工单总数：").append(tasks.size()).append(" 张\n");
        long completed = 0;
        for (MaintenanceTask.Status status : MaintenanceTask.Status.values()) {
            long count = tasks.stream().filter(task -> task.getStatusEnum() == status).count();
            report.append(status.getLabel()).append("：").append(count).append(" 张\n");
            if (status == MaintenanceTask.Status.COMPLETED) completed = count;
        }
        report.append(String.format("任务完成率：%.2f%%%n", tasks.isEmpty() ? 0.0 : completed * 100.0 / tasks.size()));
        report.append(String.format("平均处理时长（已完成工单创建至完成）：%.2f 秒%n", FrMnt.getAverageProcessingTime()));
        report.append("=".repeat(40));
        return report.toString();
    }

    public static void showResults(Scanner sc, Equipment[] results) {
    if (results.length == 0) System.out.println("没有符合条件的设备");
    else {
        System.out.println("共找到 " + results.length + " 台设备：");
        System.out.println(FrDev.showInfo(results));
    }
    FrUi.waitForEnter(sc);
    }


    public  static void queryDevices(Scanner sc) {
        System.out.println("1.按编号查询\n2.按类型查询\n3.按状态查询\n4.模糊查询（id/型号/井场）");
        String choice = sc.nextLine().trim();
        try {
            Equipment[] result;
            switch (choice) {
                case "1":
                    System.out.println("请输入设备id,例如 PU_1:");
                    result = new Equipment[]{FrDev.findById(sc.nextLine().trim())};
                    break;
                case "2":
                    System.out.println("请输入设备类型（PU/CP/PS/TS/FM/CM、英文类型名或中文名）：");
                    result = FrDev.findByType(sc.nextLine().trim());
                    break;
                case "3":
                    result = FrDev.findByStatus(FrDev.chooseDeviceStatus(sc, false));
                    break;
                case "4":
                    System.out.println("请输入关键字（id/型号/井场）：");
                    result = FrDev.fuzzyFind(sc.nextLine());
                    break;
                default:
                    System.out.println("请输入正确的数字！");
                    return;
            }
            showResults(sc, result);
        } catch (InvalidDeviceIdException | DeviceNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            FrUi.waitForEnter(sc);
        }
    }

    public static void sortDevices(Scanner sc) {
        System.out.println("1.按设备编号排序\n2.按运行状态排序\n3.按安装日期排序");
        java.util.Comparator<Equipment> comparator = switch (sc.nextLine().trim()) {
            case "1" -> devices.DeviceSort.BY_ID;
            case "2" -> devices.DeviceSort.BY_STATUS;
            case "3" -> devices.DeviceSort.BY_INSTALL_DATE;
            default -> null;
        };
        if (comparator == null) {
            System.out.println("请输入正确的数字！");
            return;
        }
        System.out.println("1.升序  2.降序");
        String direction = sc.nextLine().trim();
        while (!"1".equals(direction) && !"2".equals(direction)) {
            System.out.println("请输入 1 或 2！");
            direction = sc.nextLine().trim();
        }
        showResults(sc, devices.DeviceSort.sorted(FrDev.allDevices(), comparator, "1".equals(direction)));
    }

    public static void filterDevices(Scanner sc) {
        System.out.println("1.筛选运行中设备\n2.筛选故障设备\n3.按设备类型筛选");
        String choice = sc.nextLine().trim();
        Equipment[] result;
        switch (choice) {
            case "1": result = FrDev.findByStatus("投运中"); break;
            case "2": result = FrDev.findByStatus("故障"); break;
            case "3":
                System.out.println("请输入设备类型：");
                result = FrDev.findByType(sc.nextLine().trim());
                break;
            default:
                System.out.println("请输入正确的数字！");
                return;
        }
        showResults(sc, result);
    }

}
