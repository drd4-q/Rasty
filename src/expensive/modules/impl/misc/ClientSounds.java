package expensive.modules.impl.misc;

import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.FunctionRegister;
import expensive.modules.api.impl.ModeSetting;
import expensive.modules.api.impl.SliderSetting;

@FunctionRegister(name = "ClientSounds", type = Category.Misc)
public class ClientSounds extends Function {

    public ModeSetting mode = new ModeSetting("Тип", "Обычный", "Обычный", "Пузырьки");
    public SliderSetting volume = new SliderSetting("Громкость", 70.0f, 0.0f, 100.0f, 1.0f);

    public ClientSounds() {
        addSettings(mode, volume);
    }


    public String getFileName(boolean state) {
        switch (mode.get()) {
            case "Обычный" -> {
                return state ? "enable" : "disable".toString();
            }
            case "Пузырьки" -> {
                return state ? "enableBubbles" : "disableBubbles";
            }
        }
        return "";
    }
}
