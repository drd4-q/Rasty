package expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventUpdate;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.ModeSetting;

import java.util.concurrent.ThreadLocalRandom;

@FunctionRegister(name = "ClickSettings", type = Category.Player)
public class ClickSettings extends Function {
    public final ModeSetting mode = new ModeSetting("Режим спринта", "Пакетный", "Пакетный", "Легитный");
    public ClickSettings() {
        addSettings(mode);
    }
}
