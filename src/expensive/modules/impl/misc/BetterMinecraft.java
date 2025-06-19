package expensive.modules.impl.misc;

import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.BooleanSetting;

@FunctionRegister(name = "BetterMinecraft", type = Category.Misc)
public class BetterMinecraft extends Function {

    public final BooleanSetting smoothCamera = new BooleanSetting("Плавная камера", true);
    //public final BooleanSetting smoothTab = new BooleanSetting("Плавный таб", true); // пот
    public final BooleanSetting betterTab = new BooleanSetting("Улучшенный таб", true);

    public BetterMinecraft() {
        addSettings(smoothCamera, betterTab);
    }
}
