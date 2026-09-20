package devices.FluidTransportDevices;

import devices.Equipment;

public abstract class FluidTransportEquipment extends Equipment {
    protected FluidTransportEquipment(
            String e_ID,
            String e_type,
            String e_model,
            String e_wellsite,
            String e_Install_date,
            String e_parameter) {
        super(e_ID, e_type, e_model, e_wellsite, e_Install_date, e_parameter);
    }
}
