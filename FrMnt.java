import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FrMnt {
    // M5 考点：使用阻塞队列作为工单缓冲区，天然支持线程安全的入队/出队
    private static final BlockingQueue<String> TASK_QUEUE = new LinkedBlockingQueue<>();

    /** 报警发生时，由后台监控引擎自动调用（入队） */
    public static void addTask(String taskInfo) {
        TASK_QUEUE.offer(taskInfo); // offer 非阻塞入队，保证生产者线程不卡死
    }

    /** 供 UI 层查看当前积压的运维任务（预览） */
    public static void showTasks() {
        if (TASK_QUEUE.isEmpty()) {
            System.out.println("当前运维任务队列为空。");
            return;
        }
        System.out.println("当前排队待处理的运维工单共 " + TASK_QUEUE.size() + " 个：");
        for (String task : TASK_QUEUE) {
            System.out.println("  - " + task);
        }
    }
    
    /** 维修工人接单处理（出队） */
    public static void processTask() {
        String task = TASK_QUEUE.poll(); // poll 非阻塞出队
        if (task == null) {
            System.out.println("当前暂无待处理运维任务。");
        } else {
            System.out.println("✅ 维修工已接单并移除任务：" + task);
        }
    }
}