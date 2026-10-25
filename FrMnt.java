import devices.Equipment;
import exceptions.DeviceNotFoundException;
import exceptions.InvalidDeviceIdException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import Task.AlarmRecord;
import Task.MaintenanceTask;

public class FrMnt {
    private static final List<MaintenanceTask> TASKS = new CopyOnWriteArrayList<>();
    private static final java.time.format.DateTimeFormatter MAINTENANCE_TIME_FORMAT =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /** 创建人工工单或报警工单；同一报警只会关联一张工单。 */
    public static synchronized MaintenanceTask createTask(
            String deviceId, String alarmId, String reason) {
        if (alarmId != null && !alarmId.isBlank()) {
            for (MaintenanceTask task : TASKS) {
                if (alarmId.trim().equalsIgnoreCase(task.getAlarmId())) {
                    return task;
                }
            }
        }
        MaintenanceTask task = new MaintenanceTask(deviceId, alarmId, reason);
        TASKS.add(task);
        return task;
    }

    public static MaintenanceTask createFromAlarm(AlarmRecord alarm) {
        if (alarm == null) {
            throw new IllegalArgumentException("报警记录不能为空");
        }
        return createTask(alarm.getDeviceId(), alarm.getAlarmId(),
                "[" + alarm.getLevel() + "] " + alarm.getDescription());
    }

    public static List<MaintenanceTask> getAll() {
        return List.copyOf(TASKS);
    }

    public static MaintenanceTask findById(String taskId) {
        if (taskId == null) {
            return null;
        }
        for (MaintenanceTask task : TASKS) {
            if (task.getTaskId().equalsIgnoreCase(taskId.trim())) {
                return task;
            }
        }
        return null;
    }

    public static List<MaintenanceTask> queryByStatus(MaintenanceTask.Status status) {
        if (status == null) {
            return getAll();
        }
        return TASKS.stream()
                .filter(task -> task.getStatusEnum() == status)
                .toList();
    }

    public static boolean assignTask(String taskId, String handler) {
        MaintenanceTask task = findById(taskId);
        return task != null && task.assign(handler);
    }

    public static boolean acceptTask(String taskId) {
        MaintenanceTask task = findById(taskId);
        return task != null && task.accept();
    }

    public static boolean startTask(String taskId) {
        MaintenanceTask task = findById(taskId);
        Equipment device = task == null ? null : findDevice(task.getDeviceId());
        if (device == null || !task.start()) {
            return false;
        }
        device.setStatus("维修中");
        return true;
    }

    public static boolean completeTask(String taskId, String result) {
        MaintenanceTask task = findById(taskId);
        Equipment device = task == null ? null : findDevice(task.getDeviceId());
        if (device == null || !task.complete(result)) {
            return false;
        }
        device.setStatus("尚未启动");
        return true;
    }

    private static Equipment findDevice(String deviceId) {
        try {
            return FrDev.findById(deviceId);
        } catch (InvalidDeviceIdException | DeviceNotFoundException e) {
            return null;
        }
    }

    public static MaintenanceTask.Status chooseMaintenanceStatus(Scanner sc) {
        System.out.println("1.待分派 2.待接收 3.已接收 4.执行中 5.已完成");
        return switch (sc.nextLine().trim()) {
            case "1" -> MaintenanceTask.Status.PENDING_ASSIGNMENT;
            case "2" -> MaintenanceTask.Status.PENDING_ACCEPTANCE;
            case "3" -> MaintenanceTask.Status.ACCEPTED;
            case "4" -> MaintenanceTask.Status.IN_PROGRESS;
            case "5" -> MaintenanceTask.Status.COMPLETED;
            default -> {
                System.out.println("请输入正确的数字！");
                yield null;
            }
        };
    }

    public static void printMaintenanceTasks(java.util.List<MaintenanceTask> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("没有符合条件的运维工单");
            return;
        }
        for (MaintenanceTask task : tasks) {
            System.out.println("-".repeat(40));
            System.out.println("工单编号：" + task.getTaskId());
            System.out.println("设备编号：" + task.getDeviceId());
            System.out.println("关联报警：" + (task.getAlarmId() == null ? "人工报修" : task.getAlarmId()));
            System.out.println("报修原因：" + task.getReason());
            System.out.println("当前状态：" + task.getStatus());
            System.out.println("维修人员：" + (task.getHandler() == null ? "未分派" : task.getHandler()));
            System.out.println("创建时间：" + task.getCreateTime().format(MAINTENANCE_TIME_FORMAT));
            if (task.getFinishTime() != null) {
                System.out.println("完成时间：" + task.getFinishTime().format(MAINTENANCE_TIME_FORMAT));
                System.out.println("处理结果：" + task.getResult());
            }
        }
        System.out.println("共 " + tasks.size() + " 张工单");
    }

    public static String maintenanceFailure(MaintenanceTask task, String rule) {
        return task == null ? "操作失败：未找到工单" :
                "操作失败：" + rule + "（当前状态：" + task.getStatus() + "）";
    }
}
