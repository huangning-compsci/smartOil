package interfaces;

import alarm.AlarmLog;
import alarm.AlarmRecord;
import exceptions.AlarmException;

/**
 * 报警检测能力（FR-ALM-01）。
 * 监测设备判断测量值是否越限，生产设备判断工况是否危险。
 * 约定：正常时安静返回；异常时抛出 AlarmException 并携带等级。
 */
public interface Alarmable {

    /**
     * 执行一次报警检测。
     * @throws AlarmException 检测到异常工况时抛出，等级见 AlarmException.Level
     */
    boolean checkAlarm() throws AlarmException;

    /**
     * 巡检一组设备，返回触发的报警数量（FR-ALM-03 报警查询的数据来源）
     */
    static int patrol(Alarmable... devices) {
        int alarmCount = 0;
        for (Alarmable d : devices) {
            if (d == null) {
                continue;
            }
            try {
                d.checkAlarm();
            } catch (AlarmException e) {
                alarmCount++;
                // FR-ALM-01：检测到异常，自动生成一条报警记录入库
                AlarmRecord rec = AlarmLog.record(e);
                System.out.println(e.getMessage() + "（已生成报警记录 " + rec.getAlarmId() + "）");
                if (e.getLevel() == AlarmException.Level.CRITICAL) {
                    // 一级报警（紧急）：可在这里联动停机或生成运维任务（FR-MNT-01）
                    System.out.println(">>> 一级报警（紧急），建议立即停机检修 " + e.getDeviceId());
                }
            }
        }
        return alarmCount;
    }
}
