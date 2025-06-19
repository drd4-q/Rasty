package expensive.modules.api.impl;


import expensive.modules.api.Setting;

import java.util.function.Supplier;

public class ModeSetting extends Setting<String> {

    public String[] strings;

    public ModeSetting(String name, String defaultVal, String... strings) {
        super(name, defaultVal);
        this.strings = strings;
    }

    public int getIndex() {
        int index = 0;
        for (String val : strings) {
            if (val.equalsIgnoreCase(get())) {
                return index;
            }
            index++;
        }
        return 0;
    }

    public boolean is(String s) {
        return get().equalsIgnoreCase(s);
    }
    @Override
    public ModeSetting setVisible(Supplier<Boolean> bool) {
        return (ModeSetting) super.setVisible(bool);
    }

}