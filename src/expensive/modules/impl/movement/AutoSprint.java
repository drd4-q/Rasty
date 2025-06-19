package expensive.modules.impl.movement;

import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.BooleanSetting;

@FunctionRegister(name = "AutoSprint", type = Category.Movement)
public class AutoSprint extends Function {
    public BooleanSetting saveSprint = new BooleanSetting("Сохранять спринт", true);
    public AutoSprint() {
        addSettings(saveSprint);
    }
}
