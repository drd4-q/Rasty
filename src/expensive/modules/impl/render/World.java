package expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;

import expensive.events.EventPacket;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.ModeSetting;
import lombok.Getter;
import net.minecraft.network.play.server.SUpdateTimePacket;

@Getter
@FunctionRegister(name = "World", type = Category.Render)
public class World extends Function {

    public ModeSetting time = new ModeSetting("Time", "Day", "Day", "Night");

    public World() {
        addSettings(time);
    }
    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SUpdateTimePacket p) {
            if (time.get().equalsIgnoreCase("Day"))
                p.worldTime = 1000L;
            else
                p.worldTime = 13000L;
        }
    }
}
