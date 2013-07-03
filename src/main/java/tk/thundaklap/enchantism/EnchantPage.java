package tk.thundaklap.enchantism;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static tk.thundaklap.enchantism.Constants.*;

public class EnchantPage {
    private ItemStack[] inventory;
    private int enchantIndex = 0;

    private Map<Integer, EnchantLevelCost> levelsForSlot = new HashMap<Integer, EnchantLevelCost>();
    private static final Map<Enchantment, String> readableName = new HashMap<Enchantment, String>();

    static {
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

    public EnchantPage() {
        inventory = Constants.getPageTemplate();
    }

    public void setEmpty() {
        inventory = Constants.INV_EMPTY_PAGE;
    }

    /**
     * Add the enchantment to this page. If the page is full, false will be
     * returned.
     *
     * @param enchant enchantment to add
     * @return if the enchantment was added
     */
    public boolean addEnchantment(Enchantment enchant) {
        // Enchant index too high, this page is full.
        if (enchantIndex >= 36) {
            return false;
        }

        System.arraycopy(getBooksForEnchant(enchant), 0, inventory, enchantIndex, 4);

        for (int i = 0; i < 4; i++) {
            levelsForSlot.put(enchantIndex + i, new EnchantLevelCost(enchant, i + 1));
        }

        advanceEnchantIndex();

        return true;

    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public EnchantLevelCost enchantAtSlot(int slot) {
        return levelsForSlot.get(slot);
    }

    public void fill() {
        while (enchantIndex < 36) {
            for (int i = 0; i < 4; i++) {
                inventory[enchantIndex + i] = ITEM_UNAVAILABLE_ENCHANT;
            }
            advanceEnchantIndex();
        }
    }

    private int advanceEnchantIndex() {
        enchantIndex += enchantIndex % 9 == 0 ? 5 : 4;
        return enchantIndex;
    }

    private static ItemStack[] getBooksForEnchant(Enchantment enchant) {
        ItemStack[] is = new ItemStack[4];

        String name = readableNameForEnchantment(enchant);

        for (int i = 0; i < 4; i++) {
            is[i] = fancyBook(new EnchantLevelCost(enchant, i + 1), name);
        }

        return is;
    }

    private static ItemStack fancyBook(EnchantLevelCost enchant, String name) {
        ItemStack is;
        int cost = enchant.cost;

        if (cost <= -1) {
            return ITEM_UNAVAILABLE_ENCHANT;
        } else {
            is = ITEM_ENCH_BOOK.clone();

            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + name + " " + intToRomanNumerals(enchant.level));

            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.ITALIC + "Cost: " + cost + "XP");
            meta.setLore(lore);

            is.setItemMeta(meta);
        }

        return is;

    }

    private static String intToRomanNumerals(int i) {
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
            return "enchantment.level." + i;
        }
    }

    private static String readableNameForEnchantment(Enchantment enchant) {
        if (readableName.containsKey(enchant)) {
            return readableName.get(enchant);
        }

        return "Undefined";
    }
}
