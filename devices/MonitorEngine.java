package devices;
import exceptions.AlarmException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import FrMnt;
import Task.DataCollectionTask;

public class MonitorEngine {
    // 保存本轮报警结果，而不是下一轮可能被改写的设备对象。
    private final BlockingQueue<AlarmException> alarmQueue = new LinkedBlockingQueue<>(100);
    private final ScheduledExecutorService collectPool = Executors.newScheduledThreadPool(10);
    private final Map<String, String> deviceStatusMap = new ConcurrentHashMap<>();
    private final AtomicInteger alarmCounter = new AtomicInteger();
    private final Object alarmDisplayLock = new Object();
    private volatile boolean running;
    private volatile boolean realtimeAlarmDisplayEnabled = true;
    private Thread consumerThread;

    /** 一个引擎只启动一次；每台设备完成一轮后，间隔一秒再次采集。 */
    public synchronized void start(Equipment[] devices) {
        if (consumerThread != null || collectPool.isShutdown()) {
            throw new IllegalStateException("监控引擎不能重复启动，请创建新实例");
        }
        Objects.requireNonNull(devices, "设备列表不能为空");
        Equipment[] snapshot = devices.clone();
        for (Equipment device : snapshot) {
            Objects.requireNonNull(device, "设备列表不能包含空设备");
        }
        running = true;
        consumerThread = new Thread(this::consumeAlarms, "Alarm-Consumer");
        consumerThread.start();
        for (Equipment device : snapshot) {
            collectPool.scheduleWithFixedDelay(
                    new DataCollectionTask(device, alarmQueue, deviceStatusMap),
                    0, 1, TimeUnit.SECONDS);
        }
        System.out.println("[系统] 并发监控引擎已启动，每台设备每轮采集间隔 1 秒");
    }

    public boolean isRunning() {
        return running;
    }

    /** 只停止控制台实时报警输出，采集、报警入库和运维任务继续运行。 */
    public void stopRealtimeAlarmDisplay() {
        synchronized (alarmDisplayLock) {
            realtimeAlarmDisplayEnabled = false;
        }
    }

    private void consumeAlarms() {
        // 停止调度后，仍等待在途采集结束，并处理完已经入队的报警。
        while (running || !collectPool.isTerminated() || !alarmQueue.isEmpty()) {
            try {
                AlarmException event = alarmQueue.poll(100, TimeUnit.MILLISECONDS);
                if (event == null) {
                    continue;
                }
                Task.AlarmRecord record = Task.AlarmLog.record(event);
                synchronized (alarmDisplayLock) {
                    if (realtimeAlarmDisplayEnabled) {
                        System.out.printf("[实时报警] %s | 设备 %s | %s%n",
                                record.getAlarmTime(), event.getDeviceId(), event.getDescription());
                    }
                }
                FrMnt.createFromAlarm(record);
                alarmCounter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /** 停止后不再采集；只有所有线程结束，才报告最终统计。 */
    public synchronized void stop() {
        running = false;
        collectPool.shutdown();
        boolean interrupted = false;
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        try {
            while (!collectPool.isTerminated()
                    || (consumerThread != null && consumerThread.isAlive())) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    collectPool.shutdownNow();
                    if (consumerThread != null) {
                        consumerThread.interrupt();
                    }
                    throw new IllegalStateException("监控引擎停止超时，已请求强制中断，部分报警可能未处理");
                }
                try {
                    if (!collectPool.isTerminated()) {
                        collectPool.awaitTermination(remaining, TimeUnit.NANOSECONDS);
                    } else {
                        TimeUnit.NANOSECONDS.timedJoin(consumerThread, remaining);
                    }
                } catch (InterruptedException e) {
                    // 先完成资源清理，再恢复调用线程的中断状态。
                    interrupted = true;
                }
            }
            System.out.println("[系统] 并发监控引擎已停止。本次总计处理报警: "
                    + alarmCounter.get() + " 条");
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
