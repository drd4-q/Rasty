package expensive.modules.api.impl;

import expensive.modules.api.Setting;

import java.util.function.Supplier;

public class BindSetting extends Setting<Integer> {
    public BindSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }

    @Override
    public BindSetting setVisible(Supplier<Boolean> bool) {
        return (BindSetting) super.setVisible(bool);
    }
}
