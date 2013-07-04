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

    private Map<Integer, EnchantLevelCost> levelsForSlot;

    public EnchantPage() {
        inventory = Constants.getPageTemplate();
        levelsForSlot = new HashMap<Integer, EnchantLevelCost>();
    }

    public EnchantPage(ItemStack[] inv, Map<Integer, EnchantLevelCost> positions) {
        inventory = inv;
        levelsForSlot = positions;
    }

    public void setEmpty() {
        inventory = Constants.INV_EMPTY_PAGE;
    }

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

    /**
     * Get the itemstacks to display in the inventory.
     *
     * @return items to show
     */
    public ItemStack[] getInventory() {
        return inventory;
    }

    /**
     * Get the EnchantLevelCost based on an index into {@link #getInventory()}
     * .
     *
     * @param slot slot desired
     * @return the EnchantLevelCost at that slot
     */
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

        String name = Utils.readableNameForEnchantment(enchant);

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
            meta.setDisplayName(ChatColor.YELLOW + name + " " + Utils.intToRomanNumerals(enchant.level));

            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.ITALIC + "Cost: " + cost + "XP");
            meta.setLore(lore);

            is.setItemMeta(meta);
        }

        return is;

    }
}
