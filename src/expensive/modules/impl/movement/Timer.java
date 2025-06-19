package expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventUpdate;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.SliderSetting;

@FunctionRegister(name = "Timer", type = Category.Movement)
public class Timer extends Function {

    private final SliderSetting speed = new SliderSetting("Скорость", 2f, 0.1f, 10f, 0.1f);

    public Timer() {
        addSettings(speed);
    }

    @Subscribe
    private void onUpdate(EventUpdate e) {
        mc.timer.timerSpeed = speed.get();
    }

    private void reset() {
        mc.timer.timerSpeed = 1;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
    }
}
