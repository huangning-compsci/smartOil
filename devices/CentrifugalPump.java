package devices;

public class CentrifugalPump extends Equipment {
    private double ratedFlow;
    private double ratedHead;
    private double inletPressure;
    private double outletPressure;
    private static int index;
    private static final String NAME="CP";
    private static final DeviceArray<CentrifugalPump> DEVICES=
            new DeviceArray<>(new CentrifugalPump[10]);
    
    

    public CentrifugalPump(){
        super(
            NAME+'_'+(++index),
            "CentrifugalPump",
            "",
            "",
            "",
            "ratedFlow,ratedHead,inletPressure,outletpressure"
        );
        this.ratedFlow=50.0;
        this.ratedHead=120.0;
    }

    public CentrifugalPump(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ){
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);
            }
            
    public static DeviceArray<CentrifugalPump> getDevices(){
        return DEVICES;
    }

    public double getRatedFlow(){
        return ratedFlow;
    }

    public void setRatedFlow(double ratedFlow){
        this.ratedFlow=ratedFlow;
    }

    public double getRatedHead(){
        return ratedHead;
    }

    public void setRatedHead(double ratedHead){
        this.ratedHead=ratedHead;
    }

    public double getInletPressure(){
        return inletPressure;
    }

    public void setInletPressure(double inletPressure){
        this.inletPressure=inletPressure;
    }

    public double getOutletPressure(){
        return outletPressure;
    }

    public void setOutletPressure(double outletPressure){
        this.outletPressure=outletPressure;
    }

    @Override //抽象方法
    public void collectData(){
        inletPressure=(((int)Math.random()*21)+10)/100.0;
        double k;
        if(Math.random()<0.8){
            k=((int)Math.random()*21+90)/100.0;
            
        }else{
            k=((int)Math.random()*30+50)/100.0;
        }
        outletPressure=inletPressure+k*1.176798;
    }

    public boolean checkAlarm(){
        if(outletPressure<inletPressure+0.8*1.176798){
            return false;
        }return true;
    }
}
