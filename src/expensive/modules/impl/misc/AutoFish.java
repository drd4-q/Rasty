package expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventPacket;
import expensive.events.EventUpdate;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.util.math.main.StopWatch;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;

@FunctionRegister(name = "AutoFish", type = Category.Misc)
public class AutoFish extends Function {

    private final StopWatch delay = new StopWatch();
    private boolean isHooked = false;
    private boolean needToHook = false;

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (delay.isReached(600) && isHooked) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            isHooked = false;
            needToHook = true;
            delay.reset();
        }

        if (delay.isReached(300) && needToHook) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            needToHook = false;
            delay.reset();
        }
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SPlaySoundEffectPacket p) {
            if (p.getSound().getName().getPath().equals("entity.fishing_bobber.splash")) {
                isHooked = true;
                delay.reset();
            }
        }
    }
}
