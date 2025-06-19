package expensive.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventPacket;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import net.minecraft.network.play.client.CConfirmTeleportPacket;

@FunctionRegister(name = "PortalGodMode", type = Category.Player)
public class PortalGodMode extends Function {

    @Subscribe
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof CConfirmTeleportPacket) {
            e.cancel();
        }
    }
}
