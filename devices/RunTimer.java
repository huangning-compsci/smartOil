package devices;

//本想写成生产力机器类的方法,但是想了想这好像不是多态该做的事,于是抽离出来成为独立类
public class RunTimer {
    private long startTime;
    private long totalTime;
    private boolean running;

    public boolean start(){
        if(!running){
            startTime=System.nanoTime();
            running=true;
            return true;
        }else{
            System.out.println("请勿重复开启计时");
            return false;
        }
    }

    public boolean stop(){
        if(running){
            totalTime+=System.nanoTime()-startTime;
            running=false;
            return true;
        }else{
            System.out.println("无法关闭,计时器不在计时");
            return false;
        }
    }

    public long getTime(){
        long time=totalTime;
        if(running){
            time+=System.nanoTime()-startTime;
            startTime=System.nanoTime();
            System.out.println("已重置开启时间");
        }
        return time/1_000_000_000;
    }
}
