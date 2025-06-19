package expensive.display.schedules.rw.impl;


import expensive.display.schedules.rw.Schedule;
import expensive.display.schedules.rw.TimeType;

public class SecretMerchantSchedule
        extends Schedule {
    @Override
    public String getName() {
        return "Тайный торговец";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.FOUR, TimeType.FIVE, TimeType.EIGHT, TimeType.ELEVEN, TimeType.FOURTEEN, TimeType.SEVENTEEN, TimeType.TWENTY, TimeType.TWENTY_THREE};
    }
}
