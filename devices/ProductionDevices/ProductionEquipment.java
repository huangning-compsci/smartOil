package devices.ProductionDevices;

import devices.Equipment;
import devices.RunTimer;

public abstract class ProductionEquipment extends Equipment {
    protected int runTimeRemain=600;
    protected RunTimer rt=new RunTimer();
    protected ProductionEquipment(
            String e_ID,
            String e_type,
            String e_model,
            String e_wellsite,
            String e_Install_date,
            String e_parameter) {
        super(e_ID, e_type, e_model, e_wellsite, e_Install_date, e_parameter);
    }
    

    
    abstract boolean Switch(String status);//提供更换档位的功能

}
