package tk.thundaklap.enchantism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class Utils {
    private static final Map<Enchantment, String> readableName = new HashMap<Enchantment, String>();
    private static final Map<Material, List<Enchantment>> cachedEnchantmentLists = new HashMap<Material, List<Enchantment>>();

    @SuppressWarnings("unchecked")
    private static final List<Enchantment> EMPTY_LIST = java.util.Collections.EMPTY_LIST;

    static {
        cachedEnchantmentLists.put(Material.BOOK, Arrays.asList(Enchantment.values()));

        readableName.put(Enchantment.ARROW_DAMAGE, "Power");
        readableName.put(Enchantment.ARROW_FIRE, "Flame");
        readableName.put(Enchantment.ARROW_INFINITE, "Infinity");
        readableName.put(Enchantment.ARROW_KNOCKBACK, "Punch");
        readableName.put(Enchantment.DAMAGE_ALL, "Sharpness");
        readableName.put(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
        readableName.put(Enchantment.DAMAGE_UNDEAD, "Smite");
        readableName.put(Enchantment.DIG_SPEED, "Efficiency");
        readableName.put(Enchantment.DURABILITY, "Unbreaking");
        readableName.put(Enchantment.FIRE_ASPECT, "Fire Aspect");
        readableName.put(Enchantment.KNOCKBACK, "Knockback");
        readableName.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
        readableName.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
        readableName.put(Enchantment.OXYGEN, "Respiration");
        readableName.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
        readableName.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
        readableName.put(Enchantment.PROTECTION_FALL, "Feather Falling");
        readableName.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
        readableName.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
        readableName.put(Enchantment.SILK_TOUCH, "Silk Touch");
        readableName.put(Enchantment.THORNS, "Thorns");
        readableName.put(Enchantment.WATER_WORKER, "Aqua Affinity");
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

    public static String readableNameForEnchantment(Enchantment enchant) {
        if (readableName.containsKey(enchant)) {
            return readableName.get(enchant);
        }

        return StringUtils.capitalize(enchant.getName());
    }

    public static String intToRomanNumerals(int i) {
        switch (i) {
        case 1:
            return "I";
        case 2:
            return "II";
        case 3:
            return "III";
        case 4:
            return "IV";
        case 5:
            return "V";
        case 6:
            return "VI";
        case 7:
            return "VII";
        case 8:
            return "VIII";
        case 9:
            return "IX";
        case 10:
            return "X";
        default:
            return Integer.toString(i);
        }
    }

}
