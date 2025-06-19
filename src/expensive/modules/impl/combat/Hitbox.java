package expensive.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import expensive.events.EventUpdate;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.BooleanSetting;
import expensive.modules.api.impl.SliderSetting;

@FunctionRegister(name = "HitBox", type = Category.Combat)
public class Hitbox extends Function {
    public final SliderSetting size = new SliderSetting("Размер", 0.2f, 0.f, 3.f, 0.05f);
    public final BooleanSetting visible = new BooleanSetting("Видимые", false);
    public Hitbox() {
        addSettings(size,visible);
    }
    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (!visible.get() || mc.player == null) {
            return;
        }

        float sizeMultiplier = this.size.get() * 2.5F;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!isNotValid(player)) {
                player.setBoundingBox(calculateBoundingBox(player, sizeMultiplier));
            }
        }
    }

    private boolean isNotValid(PlayerEntity player) {
        return player == mc.player || !player.isAlive();
    }

    private AxisAlignedBB calculateBoundingBox(Entity entity, float size) {
        double minX = entity.getPosX() - size;
        double minY = entity.getBoundingBox().minY;
        double minZ = entity.getPosZ() - size;
        double maxX = entity.getPosX() + size;
        double maxY = entity.getBoundingBox().maxY;
        double maxZ = entity.getPosZ() + size;

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
