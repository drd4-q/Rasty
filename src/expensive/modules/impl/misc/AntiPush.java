package expensive.modules.impl.misc;

import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.BooleanSetting;
import expensive.modules.api.impl.ModeListSetting;
import lombok.Getter;

@Getter
@FunctionRegister(name = "AntiPush", type = Category.Player)
public class AntiPush extends Function {

    private final ModeListSetting modes = new ModeListSetting("Тип",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Вода", false),
            new BooleanSetting("Блоки", true));

    public AntiPush() {
        addSettings(modes);
    }

}
