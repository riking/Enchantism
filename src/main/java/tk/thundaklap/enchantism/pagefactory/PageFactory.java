package tk.thundaklap.enchantism.pagefactory;

import java.util.List;

import org.bukkit.enchantments.Enchantment;

import tk.thundaklap.enchantism.EnchantPage;

/**
 * A PageFactory takes an ordered list of Enchantments and creates as many
 * {@link EnchantPage}s as needed to contain all of the Enchantments.
 * <p>
 * A PageFactory shall have no state, and will be swappable for another
 * PageFactory at any time, with only the formatting of the returned pages
 * changing.
 */
public interface PageFactory {
    public EnchantPage[] getEnchantPages(List<Enchantment> enchantments);
}
