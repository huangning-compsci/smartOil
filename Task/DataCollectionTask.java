package Task;
import devices.Equipment;
import exceptions.AlarmException;
import interfaces.Alarmable;
import interfaces.Collectable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/** 单台设备的一轮采集任务；重复执行和间隔由 MonitorEngine 调度。 */
public class DataCollectionTask implements Runnable {
    private final Equipment device;
    private final BlockingQueue<AlarmException> alarmQueue;
    private final Map<String, String> statusMap;

    public DataCollectionTask(Equipment device, BlockingQueue<AlarmException> alarmQueue,
            Map<String, String> statusMap) {
        this.device = device;
        this.alarmQueue = alarmQueue;
        this.statusMap = statusMap;
    }

    @Override
    public void run() {
        try {
            if (!device.isRunning() || !(device instanceof Collectable c)) {
                statusMap.put(device.getId(), "跳过采集：设备未投运或不支持采集");
                return;
            }
            c.collectData();
            statusMap.put(device.getId(), "最新采集成功: " + java.time.LocalTime.now());
            if (device instanceof Alarmable a) {
                try {
                    a.checkAlarm();
                } catch (AlarmException alarm) {
                    // 在本轮数据被覆盖之前生成报警结果；队列满时阻塞，限制积压。
                    alarmQueue.put(alarm);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // 周期任务必须捕获本轮异常，否则后续调度会被取消。
            statusMap.put(device.getId(), "采集失败: " + e.getMessage());
            System.err.println("[采集线程异常] 设备 " + device.getId() + ": " + e.getMessage());
        }
    }
}
