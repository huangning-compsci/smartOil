package devices;

public class FlowMeter extends Equipment{
    private double flowRange;
    private String accuracy;
    private double currentFlow;
    private double totalFlow;
    private static final String NAME="FM";
    private static int index;
    private static final DeviceArray<FlowMeter> DEVICES =
        new DeviceArray<>(new FlowMeter[10]);

    public FlowMeter(){
        super(
            NAME+'_'+(++index),
            "FlowMeter",
            "",
            "",
            "",
            "flowRange,accuracy,currentFlow,totalFlow"
        );
        this.flowRange=10.0;
        this.accuracy="0.5级";
    }

    public FlowMeter(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ){
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);

    }
    public static DeviceArray<FlowMeter> getDevices(){
        return DEVICES;
    }

    public double getFlowRange(){
        return flowRange;
    }

    public void setFlowRange(double flowRange){
        this.flowRange=flowRange;
    }

    public String getAccuracy(){
        return accuracy;
    }

    public void setAccuracy(String accuracy){
        this.accuracy=accuracy;
    }

    public double getCurrentFlow(){
        return currentFlow;
    }

    public void setCurrentFlow(double currentFlow){
        this.currentFlow=currentFlow;
    }

    public double getTotalFlow(){
        return totalFlow;
    }

    public void setTotalFlow(double totalFlow){
        this.totalFlow=totalFlow;
    }

    @Override //抽象方法
    public void collectData(){
        currentFlow=((int)Math.random()*500+100)/100.0;
    }
    public boolean checkAlarm(){
        if(currentFlow<1.50||currentFlow>4.50){
            return false;
        }return true;
    }
}
