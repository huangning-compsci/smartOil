package devices;

public abstract class Equipment {
    private final String e_ID;
    private final String e_type;
    private String e_model;
    private String e_wellsite;
    private String e_Install_date;
    private final String e_parameter;
    private String e_Status; 
      
    public Equipment(){
        this("", "", "", "", "", "");
    }

    protected Equipment(
        String e_ID,
        String e_type,
        String e_model,
        String e_wellsite,
        String e_Install_date,
        String e_parameter
       ){
        this.e_ID=e_ID;
        this.e_Install_date=e_Install_date;
        this.e_Status="尚未启动";
        this.e_model=e_model;
        this.e_parameter=e_parameter;
        this.e_type=e_type;
        this.e_wellsite=e_wellsite;

    }

    public String getId(){
        return e_ID;
    }

    public String getType(){
        return e_type;
    }

    public String getParameter(){
        return e_parameter;
    }

    public String getInstallDate(){
        return e_Install_date;
    }

    public String getStatus(){
        return e_Status;
    }

    public String getModel(){
        return e_model;
    }

    public String getWellsite(){
        return e_wellsite;
    }
    public void setWellsite(String wellsite){
        this.e_wellsite=wellsite;
    }
    public void setInstallDate(String installdate){
        this.e_Install_date=installdate;
    }
    public void setModel(String model){
        this.e_model=model;
    }
    public void setStatus(String status){
        this.e_Status=status;
    }

    public abstract void collectData();
    public abstract boolean checkAlarm();

    

}
