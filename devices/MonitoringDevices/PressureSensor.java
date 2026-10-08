package devices.MonitoringDevices;

import devices.DeviceArray;

public class PressureSensor extends MonitoringEquipment{
    private double pressureRange;
    private String accuracy;
    private double currentPressure;
    private double upperLimit;
    private double lowerLimit;
    private static final String NAME="PS";
    private static int index;
    private static final DeviceArray<PressureSensor> DEVICES =
        new DeviceArray<>(new PressureSensor[10]);

    public PressureSensor(){
        super(
            NAME+'_'+(++index),
            "PressureSensor",
            "",
            "",
            "",
            "pressureRange,accuracy,currentPressure,upperLimit,lowerLimit"
        );
        this.pressureRange=4.0;
        this.accuracy="0.5级";
    }

    public PressureSensor(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ){
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);

    }
    public static DeviceArray<PressureSensor> getDevices(){
        return DEVICES;
    }

    public double getPressureRange(){
        return pressureRange;
    }

    public void setPressureRange(double pressureRange){
        this.pressureRange=pressureRange;
    }

    public String getAccuracy(){
        return accuracy;
    }

    public void setAccuracy(String accuracy){
        this.accuracy=accuracy;
    }

    public double getCurrentPressure(){
        return currentPressure;
    }

    public void setCurrentPressure(double currentPressure){
        this.currentPressure=currentPressure;
    }

    public double getUpperLimit(){
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit){
        this.upperLimit=upperLimit;
    }

    public double getLowerLimit(){
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit){
        this.lowerLimit=lowerLimit;
    }
    @Override 
    public boolean collectData(){
        return false;
    } 
    public boolean checkAlarm(){
        return true;
    }
}
