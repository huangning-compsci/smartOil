package devices.MonitoringDevices;

import devices.DeviceArray;

public class TemperatureSensor extends MonitoringEquipment{
    private double tempRange;
    private String accuracy;
    private double currentTemp;
    private double upperLimit;
    private static final String NAME="TS";
    private static int index;
    private static final DeviceArray<TemperatureSensor> DEVICES =
        new DeviceArray<>(new TemperatureSensor[10]);

    public TemperatureSensor(){
        super(
            NAME+'_'+(++index),
            "TemperatureSensor",
            "",
            "",
            "",
            "tempRange,accuracy,currentTemp,upperLimit"
        );
        this.tempRange=100.0;
        this.accuracy="A级";
    }

    public TemperatureSensor(
            String e_wellsite,
            String e_Install_date,
            String e_model
            ){
        this();
        setModel(e_model);
        setWellsite(e_wellsite);
        setInstallDate(e_Install_date);

    }
    public static DeviceArray<TemperatureSensor> getDevices(){
        return DEVICES;
    }

    public double getTempRange(){
        return tempRange;
    }

    public void setTempRange(double tempRange){
        this.tempRange=tempRange;
    }

    public String getAccuracy(){
        return accuracy;
    }

    public void setAccuracy(String accuracy){
        this.accuracy=accuracy;
    }

    public double getCurrentTemp(){
        return currentTemp;
    }

    public void setCurrentTemp(double currentTemp){
        this.currentTemp=currentTemp;
    }

    public double getUpperLimit(){
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit){
        this.upperLimit=upperLimit;
    }

    @Override 
    public void collectData(){
        
    } 
    public boolean checkAlarm(){
        return true;
    }    
}
