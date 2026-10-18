import devices.Equipment;
import interfaces.Alarmable;
import exceptions.AlarmException;
import java.util.concurrent.*;
import java.util.Map;

public class MonitorEngine {
    // 1. 共享缓冲区：容量为 100 的阻塞队列
    private final BlockingQueue<Equipment> dataQueue = new LinkedBlockingQueue<>(100);
    
    // 2. 线程池
    private final ExecutorService collectPool;
    
    // 控制开关
    private volatile boolean running = false; 

    // 3. M5 考点：共享的报警计数器（使用 synchronized 保护）和线程安全的设备状态 Map
    private int alarmCounter = 0;
    private final Map<String, String> deviceStatusMap = new ConcurrentHashMap<>();

    public MonitorEngine() {
        // M5 考点：线程中的异常如何捕获和记录。
        // 通过自定义 ThreadFactory，为线程池中创建出的每一个线程设置未捕获异常处理器。
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler((thread, e) -> {
                System.err.println("[全局异常捕获] 线程 " + thread.getName() + " 发生未捕获异常: " + e.getMessage());
            });
            return t;
        };

        // 使用 ThreadPoolExecutor 注入线程工厂
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
        
        this.collectPool = pool;
    }

    // 启动监控引擎
    public void start(Equipment[] devices) {
        running = true;
        System.out.println("[系统] 正在启动并发监控引擎...");

        // --- 启动消费者线程（报警处理） ---
        Thread consumerThread = new Thread(() -> {
            while (running || !dataQueue.isEmpty()) {
                try {
                    // take() 是阻塞方法，队列为空时自动等待，不占 CPU
                    Equipment device = dataQueue.take(); 
                    if (device instanceof Alarmable a) {
                        try {
                            a.checkAlarm(); // 触发报警检测
                        } catch (AlarmException e) {
                            // 发生报警，存入线程安全的 AlarmLog
                            alarm.AlarmLog.record(e); 
                            
                            // M5 考点：使用 synchronized 保护共享的报警计数器
                            synchronized (this) {
                                alarmCounter++;
                                // 每 100 条打印一次，防止控制台输出太快卡死（并发 IO 优化）
                                if (alarmCounter % 100 == 0) {
                                    System.out.println("[监控引擎] 后台已静默捕获并处理 " + alarmCounter + " 条并发报警...");
                                }
                            }
                            
                            // M5 考点：联动运维模块（安全入队）
                            // 这里的 e 是 AlarmException，有 getDeviceId 和 getDescription 方法
                            FrMnt.addTask("设备 " + e.getDeviceId() + " 异常：" + e.getDescription());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Alarm-Consumer");
        consumerThread.start();

        // --- 启动生产者（数据采集） ---
        for (Equipment device : devices) {
            // M5 考点：将任务封装成实现 Runnable 的类提交给线程池
            collectPool.submit(new DataCollectionTask(device, dataQueue, deviceStatusMap));
        }
    }

    // 停止监控引擎
    public void stop() {
        running = false;
        collectPool.shutdownNow(); // 中断所有线程
        System.out.println("[系统] 并发监控引擎已停止。本次总计处理报警: " + alarmCounter + " 条");
    }
}