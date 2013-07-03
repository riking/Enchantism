package tk.thundaklap.enchantism.pagefactory;

import java.util.List;

import org.bukkit.enchantments.Enchantment;

import tk.thundaklap.enchantism.EnchantPage;

public class DefaultPageFactory implements PageFactory {
    public static final int ENCHANTMENTS_PER_PAGE = 8;

    @Override
    public EnchantPage[] getEnchantPages(List<Enchantment> enchantments) {
        int numberOfEnchants = enchantments.size();
        EnchantPage[] pages = new EnchantPage[((numberOfEnchants - 1) / ENCHANTMENTS_PER_PAGE) + 1];

        for (int i = 0; i < pages.length; i++) {
            pages[i] = new EnchantPage();
        }

        int currentlyAddingPage = 0;

        for (Enchantment enchant : enchantments) {
            // Method returns false if the page is full.
            if (!pages[currentlyAddingPage].addEnchantment(enchant)) {
                pages[++currentlyAddingPage].addEnchantment(enchant);
            }
        }

        pages[currentlyAddingPage].fill();

        return pages;
    }
}
