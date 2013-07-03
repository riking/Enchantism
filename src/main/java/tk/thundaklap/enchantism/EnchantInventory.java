package tk.thundaklap.enchantism;

import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import static tk.thundaklap.enchantism.Constants.*;

public final class EnchantInventory {

    public Player player;
    public BukkitTask updateTask;
    private EnchantPage[] pages;
    private int pageCount = 0;
    private int currentPage = 0;
    private Inventory inventory;
    private boolean showUnenchant = false;
    private boolean unenchantEnabled;

    public EnchantInventory(Player player) {
        unenchantEnabled = Enchantism.getInstance().configuration.enableUnenchantButton;
        this.player = player;
        this.inventory = Bukkit.createInventory(player, SIZE_INVENTORY, "Enchant");
        inventory.setMaxStackSize(1);
        slotChange();
        this.player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void updatePlayerInv() {
        boolean isMultiPage = pageCount != 0;
        inventory.setContents((ItemStack[]) ArrayUtils.addAll(topRows(isMultiPage && pageCount != currentPage, isMultiPage && currentPage != 0, showUnenchant && unenchantEnabled), pages[currentPage].getInventory()));
        new DelayUpdateInventory(player).runTask(Enchantism.getInstance());
    }

    public void slotChange() {
        ItemStack change = inventory.getItem(SLOT_CURRENT_ITEM);
        List<Enchantment> applicableEnchantments = Utils.getEnchantments(change);

        currentPage = 0;

        if (applicableEnchantments.isEmpty()) {
            pageCount = 0;
            pages = new EnchantPage[1];
            pages[0] = new EnchantPage();
            pages[0].setEmpty();

            // allow unenchanting of books
            showUnenchant = change == null ? false : change.getType() == Material.ENCHANTED_BOOK;

        } else {
            int numberOfEnchants = applicableEnchantments.size();
            pageCount = (numberOfEnchants - 1) / ENCHANTMENTS_PER_PAGE;
            pages = new EnchantPage[pageCount + 1];

            for (int i = 0; i < pages.length; i++) {
                pages[i] = new EnchantPage();
            }

            int currentlyAddingPage = 0;

            for (Enchantment enchant : applicableEnchantments) {
                // Method returns false if the page is full.
                if (!pages[currentlyAddingPage].addEnchantment(enchant)) {
                    pages[++currentlyAddingPage].addEnchantment(enchant);
                }
            }

            pages[currentlyAddingPage].fill();
            showUnenchant = true;
        }
        updatePlayerInv();
    }

    public void inventoryClicked(InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        InventoryView view = event.getView();
        assert SIZE_INVENTORY == view.getTopInventory().getSize();

        // Default to cancel, uncancel if we want vanilla behavior
        event.setResult(Result.DENY);

        updateTask = Bukkit.getScheduler().runTask(Enchantism.getInstance(), new SlotChangeTask(this, inventory.getItem(SLOT_CURRENT_ITEM)));

        // Let people shift-click in tools
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            if (rawSlot >= SIZE_INVENTORY) {
                // Swappy swap swap!
                ItemStack old = view.getItem(SLOT_CURRENT_ITEM);
                ItemStack newI = view.getItem(rawSlot);
                ItemStack toPlace = newI;
                ItemStack replace = null;

                // Only allow in 1 item
                if (newI.getAmount() > 1) {
                    replace = newI;
                    toPlace = newI.clone();

                    replace.setAmount(newI.getAmount() - 1);
                    toPlace.setAmount(1);
                }
                view.setItem(SLOT_CURRENT_ITEM, toPlace);

                if (replace != null) {
                    view.setItem(rawSlot, replace);
                    view.getBottomInventory().addItem(old);
                } else {
                    view.setItem(rawSlot, old);
                }
                return;
            }
        }

        // Predefined slot behavior
        if (rawSlot == SLOT_PREV_PAGE) {
            if (currentPage != 0 && pageCount > 0) {
                currentPage--;
                updatePlayerInv();
                player.playSound(player.getLocation(), Sound.CLICK, 2F, 1F);
            }
            return;

        } else if (rawSlot == SLOT_NEXT_PAGE) {
            if (currentPage != pageCount) {
                currentPage++;
                updatePlayerInv();
                player.playSound(player.getLocation(), Sound.CLICK, 2F, 1F);
            }
            return;

        } else if (rawSlot == SLOT_CURRENT_ITEM) {
            event.setResult(Result.DEFAULT);
            return;

        } else if (rawSlot == SLOT_UNENCHANT) {
            if (showUnenchant && unenchantEnabled && event.getClick() == ClickType.LEFT) {
                ItemStack item = inventory.getItem(SLOT_CURRENT_ITEM);

                if (item != null && !item.getType().equals(Material.AIR)) {
                    if (item.getType() == Material.ENCHANTED_BOOK) {
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

                        for (Enchantment en : meta.getStoredEnchants().keySet()) {
                            meta.removeStoredEnchant(en);
                        }
                        for (Enchantment en : meta.getEnchants().keySet()) {
                            meta.removeEnchant(en);
                        }
                        item.setItemMeta(meta);

                        item.setType(Material.BOOK);
                    } else {
                        ItemMeta meta = item.getItemMeta();

                        for (Enchantment en : meta.getEnchants().keySet()) {
                            meta.removeEnchant(en);
                        }

                        item.setItemMeta(meta);
                    }
                    player.playSound(player.getLocation(), Sound.GLASS, 2F, 1F);
                    slotChange();
                }
            }
            return;

        } else if (rawSlot >= SIZE_HEADER && rawSlot < SIZE_INVENTORY) {
            EnchantLevelCost enchant = pages[currentPage].enchantAtSlot(rawSlot - SIZE_HEADER);

            if (enchant == null) {
                return;
            }

            if (player.getLevel() < enchant.cost) {
                player.sendMessage(ChatColor.RED + "You don\'t have enough XP to enchant the item with that enchantment!");
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2F, 1F);
                return;
            }

            player.setLevel(player.getLevel() - enchant.cost);
            player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 2F, 1F);

            ItemStack item = inventory.getItem(SLOT_CURRENT_ITEM);

            if (item.getType() == Material.BOOK) {
                item.setType(Material.ENCHANTED_BOOK);

                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                meta.addStoredEnchant(enchant.enchant, enchant.level, true);

                item.setItemMeta(meta);
                return;
            }

            try {
                item.addUnsafeEnchantment(enchant.enchant, enchant.level);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "[Enchantism] Unexpected error. See console for details.");
                Enchantism.getInstance().getLogger().severe(e.getMessage());
            }
            inventory.setItem(SLOT_CURRENT_ITEM, item);
        } else if (rawSlot >= SIZE_INVENTORY) {
            // Uncancel, unless on blacklist
            if (event.getAction() != InventoryAction.COLLECT_TO_CURSOR && event.getAction() != InventoryAction.CLONE_STACK && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setResult(Result.DEFAULT);
            }
        }
    }

    public void inventoryDragged(InventoryDragEvent event) {
        if (event.getRawSlots().contains(SLOT_CURRENT_ITEM)) {
            Bukkit.getScheduler().runTask(Enchantism.getInstance(), new SlotChangeTask(this, inventory.getItem(Constants.SLOT_CURRENT_ITEM)));
        }
    }

    private ItemStack[] topRows(boolean showNextPage, boolean showPrevPage, boolean showUnenchantButton) {
        ItemStack[] is = Constants.getTopRowTemplate();

        is[SLOT_CURRENT_ITEM] = inventory.getItem(SLOT_CURRENT_ITEM);
        if (showPrevPage) {
            is[SLOT_PREV_PAGE] = ITEM_PREV_PAGE;
        }
        if (showUnenchantButton) {
            is[SLOT_UNENCHANT] = ITEM_UNENCHANT;
        }
        if (showNextPage) {
            is[SLOT_NEXT_PAGE] = ITEM_NEXT_PAGE;
        }
        return is;
    }
}
