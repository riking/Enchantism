package tk.thundaklap.enchantism;

import org.bukkit.plugin.java.JavaPlugin;

import tk.thundaklap.enchantism.pagefactory.DefaultPageFactory;
import tk.thundaklap.enchantism.pagefactory.PageFactory;

public class Enchantism extends JavaPlugin {

    public EnchantismConfiguration configuration;
    private static Enchantism instance;
    public PageFactory pagefactory;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configuration = new EnchantismConfiguration();
        getServer().getPluginManager().registerEvents(new EnchantismListener(), this);

        pagefactory = new DefaultPageFactory();
    }

    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public static Enchantism getInstance() {
        return instance;
    }

    public static PageFactory getPageFactory() {
        return instance.pagefactory;
    }

    public static void setPageFactory(PageFactory replacement) {
        instance.pagefactory = replacement;
    }
}
