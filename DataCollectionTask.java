import devices.Equipment;
import interfaces.Collectable;
import java.util.concurrent.BlockingQueue;
import java.util.Map;

/**
 * M5 考点：创建数据采集任务实现 Runnable 接口（开发A任务）。
 * 职责：作为生产者，模拟单台设备的采集，并将数据放入共享缓冲区。
 */
public class DataCollectionTask implements Runnable {
    private final Equipment device;
    private final BlockingQueue<Equipment> queue;
    private final Map<String, String> statusMap; // 对应任务：共享设备状态 Map

    public DataCollectionTask(Equipment device, BlockingQueue<Equipment> queue, Map<String, String> statusMap) {
        this.device = device;
        this.queue = queue;
        this.statusMap = statusMap;
    }

    @Override
    public void run() {
        try {
            if (device.isRunning() && device instanceof Collectable c) {
                c.collectData();
                // 线程安全地更新 Map
                statusMap.put(device.getId(), "最新采集成功: " + java.time.LocalTime.now());
            }
            
            // 线程通信：放入阻塞队列（如果队列满，自动阻塞等待消费者）
            queue.put(device); 
            
            // 模拟采集间隔
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // 正确终止线程的方式：设置中断标志
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // M5 考点：编写多线程场景下的异常处理
            System.err.println("[采集线程异常] 设备 " + device.getId() + " 采集中断: " + e.getMessage());
        }
    }
}