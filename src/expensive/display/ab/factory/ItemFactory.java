package expensive.display.ab.factory;

import expensive.display.ab.model.IItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

import java.util.Map;

public interface ItemFactory {
    IItem createNewItem(Item item, int price, int quantity, int damage, Map<Enchantment, Integer> enchantments);
}
