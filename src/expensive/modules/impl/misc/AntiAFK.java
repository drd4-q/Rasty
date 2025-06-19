package expensive.modules.impl.misc;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventUpdate;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;

import java.util.concurrent.ThreadLocalRandom;

@FunctionRegister(name = "AntiAFK", type = Category.Player)
public class AntiAFK extends Function {

    @Subscribe
    private void onUpdate(EventUpdate e) {
        if (mc.player.ticksExisted % 200 != 0) return;

        if (mc.player.isOnGround()) mc.player.jump();
        mc.player.rotationYaw += ThreadLocalRandom.current().nextFloat(-10, 10);
    }
}
