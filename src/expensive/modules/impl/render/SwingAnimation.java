package expensive.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.impl.combat.KillAura;
import expensive.modules.api.impl.BooleanSetting;
import expensive.modules.api.impl.ModeSetting;
import expensive.modules.api.impl.SliderSetting;
import net.minecraft.util.math.vector.Vector3f;


@FunctionRegister(name = "SwingAnimation", type = Category.Render)
public class SwingAnimation extends Function {

    public ModeSetting animationMode = new ModeSetting("Мод", "1", "1", "2", "3", "4");
    public SliderSetting swingPower = new SliderSetting("Сила", 5.0f, 1.0f, 10.0f, 0.05f);
    public SliderSetting swingSpeed = new SliderSetting("Скорость", 10.0f, 3.0f, 10.0f, 1.0f);
    public SliderSetting scale = new SliderSetting("Размер", 1.0f, 0.5f, 1.5f, 0.05f);
    public final BooleanSetting onlyAura = new BooleanSetting("Только с киллаурой", true);
    public KillAura killAura;

    public SwingAnimation(KillAura killAura) {
        this.killAura = killAura;
        addSettings(animationMode, swingPower, swingSpeed, scale, onlyAura);
    }

    public void animationProcess(MatrixStack stack, float swingProgress, Runnable runnable) {
        float anim = (float) Math.sin(swingProgress * (Math.PI / 2) * 2);

        if (onlyAura.get() && killAura.getTarget() == null) {
            runnable.run();
            return;
        }

        switch (animationMode.getIndex()) {
            case 0:
                stack.scale(scale.get(), scale.get(), scale.get());
                stack.translate(0.4f, 0.1f, -0.5);
                stack.rotate(Vector3f.YP.rotationDegrees(90));
                stack.rotate(Vector3f.ZP.rotationDegrees(-60));
                stack.rotate(Vector3f.XP.rotationDegrees(-90
                        - (swingPower.get() * 10) * anim));
                break;
            case 1:
                stack.scale(scale.get(), scale.get(), scale.get());
                stack.translate(0.0, 0, -0.5f);
                stack.rotate(Vector3f.YP.rotationDegrees(15 * anim));

                stack.rotate(Vector3f.ZP.rotationDegrees(-60 * anim));
                stack.rotate(Vector3f.XP.rotationDegrees((-90 - (swingPower.get())) * anim));
                break;
            case 2:
                stack.scale(scale.get(), scale.get(), scale.get());
                stack.translate(0.4f, 0, -0.5f);
                stack.rotate(Vector3f.YP.rotationDegrees(90));
                stack.rotate(Vector3f.ZP.rotationDegrees(-30));
                stack.rotate(Vector3f.XP.rotationDegrees(-90
                        - (swingPower.get() * 10) * anim));
                break;
            default:
                stack.scale(scale.get(), scale.get(), scale.get());
                runnable.run();
                break;
        }
    }

}
