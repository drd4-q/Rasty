package expensive.display.schedules.rw.impl;

import expensive.display.schedules.rw.Schedule;
import expensive.display.schedules.rw.TimeType;

public class ScroogeSchedule
        extends Schedule {
    @Override
    public String getName() {
        return "Скрудж";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.FIFTEEN_HALF};
    }
}
