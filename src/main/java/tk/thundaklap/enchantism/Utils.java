package tk.thundaklap.enchantism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class Utils {
    private static final Map<Material, List<Enchantment>> cachedEnchantmentLists = new HashMap<Material, List<Enchantment>>();

    @SuppressWarnings("unchecked")
    private static final List<Enchantment> EMPTY_LIST = java.util.Collections.EMPTY_LIST;

    static {
        // cachedEnchantmentLists
        cachedEnchantmentLists.put(Material.BOOK, Arrays.asList(Enchantment.values()));

    }

    /**
     * Get all of the Enchantments that should be displayed for this item
     * (ignoring levels). Assume that the returned list is immutable.
     *
     * @param item
     * @return an immutable list of valid enchantments
     */
    public static List<Enchantment> getEnchantments(ItemStack item) {
        if (item == null) {
            return EMPTY_LIST;
        }
        Material type = item.getType();
        if (type == Material.AIR) {
            return EMPTY_LIST;
        }

        if (cachedEnchantmentLists.containsKey(type)) {
            return cachedEnchantmentLists.get(type);
        }

        List<Enchantment> ret = new ArrayList<Enchantment>(7);
        for (Enchantment enc : Enchantment.values()) {
            if (enc.canEnchantItem(item)) {
                ret.add(enc);
            }
        }

        if (ret.size() == 0) {
            cachedEnchantmentLists.put(type, EMPTY_LIST);
            return EMPTY_LIST;
        }

        cachedEnchantmentLists.put(type, ret);
        return ret;
    }
}
