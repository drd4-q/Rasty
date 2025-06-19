package expensive.display.schedules.rw.impl;

import expensive.display.schedules.rw.Schedule;
import expensive.display.schedules.rw.TimeType;

public class MascotSchedule
        extends Schedule {
    @Override
    public String getName() {
        return "Талисман";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.NINETEEN_HALF};
    }
}
