package expensive.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import expensive.events.EventKey;
import expensive.modules.api.Category;
import expensive.modules.api.Function;
import expensive.modules.api.impl.BindSetting;
import expensive.util.misc.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import expensive.main.command.friends.FriendStorage;
import expensive.modules.api.FunctionRegister;

@FunctionRegister(name = "ClickFriend", type = Category.Player)
public class ClickFriend extends Function {
    final BindSetting throwKey = new BindSetting("Кнопка", -98);
    public ClickFriend() {
        addSettings(throwKey);
    }
    @Subscribe
    public void onKey(EventKey e) {
        if (e.getKey() == throwKey.get() && mc.pointedEntity instanceof PlayerEntity) {

            if (mc.player == null || mc.pointedEntity == null) {
                return;
            }

            String playerName = mc.pointedEntity.getName().getString();

            if (!PlayerUtils.isNameValid(playerName)) {
                print("Невозможно добавить бота в друзья, увы, как бы вам не хотелось это сделать");
                return;
            }

            if (FriendStorage.isFriend(playerName)) {
                FriendStorage.remove(playerName);
                printStatus(playerName, true);
            } else {
                FriendStorage.add(playerName);
                printStatus(playerName, false);
            }
        }
    }

    void printStatus(String name, boolean remove) {
        if (remove) print(name + " удалён из друзей");
        else print(name + " добавлен в друзья");
    }
}
