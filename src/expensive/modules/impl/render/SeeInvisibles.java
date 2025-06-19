package expensive.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventUpdate;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import net.minecraft.entity.player.PlayerEntity;

@FunctionRegister(name = "SeeInvisibles", type = Category.Render)
public class SeeInvisibles extends Function {


    @Subscribe
    private void onUpdate(EventUpdate e) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player && player.isInvisible()) {
                player.setInvisible(false);
            }
        }
    }

}
