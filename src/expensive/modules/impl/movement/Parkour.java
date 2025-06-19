package expensive.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventUpdate;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.util.misc.player.MoveUtils;

@FunctionRegister(name = "Parkour", type = Category.Movement)
public class Parkour extends Function {

    @Subscribe
    private void onUpdate(EventUpdate e) {

        if (MoveUtils.isBlockUnder(0.001f) && mc.player.isOnGround()) {
            mc.player.jump();
        }
    }

}
