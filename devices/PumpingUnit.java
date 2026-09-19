package devices;

public  class  PumpingUnit extends  Equipment{
    
    private double ratedPower;
    private double stroke;
    private double strokeRate;
    private double currentLoad;
    private double ratedLoad;
    private static final String NAME="PU";
    private static int index;
    private static final DeviceArray<PumpingUnit> DEVICES =
        new DeviceArray<>(new PumpingUnit[10]);
    
    
    public PumpingUnit(){
        super(
            NAME+'_'+(++index),
            "PumpingUnit",
            "",
            "",
            "",
            "stroke,strokeRate,currentLoad,ratedLoad,ratedPower"
        );
        this.ratedPower=75.0;
        this.ratedLoad=160.0;
    }

    public PumpingUnit(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ){
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);


            
    }
    
    public static DeviceArray<PumpingUnit> getDevices(){
        return DEVICES;
    }

    public double getRatedPower(){
        return ratedPower;
    }

    public void setRatedPower(double ratedPower){
        this.ratedPower=ratedPower;
    }

    public double getStroke(){
        return stroke;
    }

    public void setStroke(double stroke){
        this.stroke=stroke;
    }

    public double getStrokeRate(){
        return strokeRate;
    }

    public void setStrokeRate(double strokeRate){
        this.strokeRate=strokeRate;
    }

    public double getCurrentLoad(){
        return currentLoad;
    }

    public void setCurrentLoad(double currentLoad){
        this.currentLoad=currentLoad;
    }

    public double getRatedLoad(){
        return ratedLoad;
    }

    public void setRatedLoad(double ratedLoad){
        this.ratedLoad=ratedLoad;
    }

    @Override 
    public void collectData(){
        
    } 
    public boolean checkAlarm(){
        return true;
    }    
}
