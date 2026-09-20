package devices.ProductionDevices;

import java.util.Scanner;

import devices.DeviceArray;


public  class  PumpingUnit extends  ProductionEquipment {
    
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

    @Override 
    public void collectData(){
        if(getStatus().equals("尚未启动")){
            System.out.println("设备未启动");
            //还没写完,全部改成boolean
        }
        
    }

    public boolean checkAlarm(){
        int num=Character.getNumericValue(getStatus().charAt(0));
        runTimeRemain-=rt.getTime()*num;
        if(runTimeRemain<60){
            System.out.println("请立即停止"+getId()+",否则机器将会过热");
            return true;
        }
        return true;
    }

    public void run(Scanner sc){
        System.out.println("请选择档位:1.慢速/2.正常/3.全速(输入对应数字)");
        switch (sc.next()) {
            case "1":
                Switch("1档");
                rt.start();
                break;
            case "2":
                Switch("2档");
                rt.start();
                break;
            case "3":
                Switch("3档");
                rt.start();
                break;
            default:
                System.out.println("请输入正确的数字!已默认设置为正常工作档位");
                Switch("2档");
                rt.start();
                break;
        }
    }

    public void stopRun(){
        rt.stop();
        setStatus("尚未启动");
        System.out.println("成功关闭"+getId());
    }

    //未加入实际应用,与时间判断有关
    public boolean Switch(String status){
        if(status=="1档"){
            int num=Character.getNumericValue(getStatus().charAt(0));
            runTimeRemain-=rt.getTime()*num;
            setStatus(status);
            System.out.println(getId()+"已调至慢速工作");

            return true;
        }else if(status=="2档"){
            int num=Character.getNumericValue(getStatus().charAt(0));
            runTimeRemain-=rt.getTime()*num;
            setStatus(status);
            System.out.println(getId()+"已调至正常速度工作");
            return true;
        }else if(status=="3档"){
            int num=Character.getNumericValue(getStatus().charAt(0));
            runTimeRemain-=rt.getTime()*num;
            setStatus(status);
            System.out.println(getId()+"已调至全速工作");
            return true;
        }else{
            System.out.println("出现错误,不存在的档位");
            return false;
        }
    }

    
    
    
    
    //以下是getter,setter方法
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

    
}
