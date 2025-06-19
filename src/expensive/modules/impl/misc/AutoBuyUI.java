package expensive.modules.impl.misc;

import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.BindSetting;

@FunctionRegister(name = "AutoBuyUI", type = Category.Misc)
public class AutoBuyUI extends Function {

    public BindSetting setting = new BindSetting("Кнопка открытия", -1);

    public AutoBuyUI() {
        addSettings(setting);
    }
}
