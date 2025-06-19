package expensive.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventUpdate;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;

@FunctionRegister(name = "NoJumpDelay", type = Category.Player)
public class NoJumpDelay extends Function {
    @Subscribe
    public void onUpdate(EventUpdate e) {
        mc.player.jumpTicks = 0;
    }
}
