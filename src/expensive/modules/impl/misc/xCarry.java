package expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventPacket;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import net.minecraft.network.play.client.CCloseWindowPacket;
import expensive.modules.api.FunctionRegister;

@FunctionRegister(name = "xCarry", type = Category.Misc)
public class xCarry extends Function {

    @Subscribe
    public void onPacket(EventPacket e) {
        if (mc.player == null) return;

        if (e.getPacket() instanceof CCloseWindowPacket) {
            e.cancel();
        }
    }
}
