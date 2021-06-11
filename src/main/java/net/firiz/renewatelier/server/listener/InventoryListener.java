package net.firiz.renewatelier.server.listener;

import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * @author firiz
 */
public class InventoryListener implements Listener {

    private static final InventoryManager inventoryManager = InventoryManager.INSTANCE;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invClick(final InventoryClickEvent e) {
        inventoryManager.onClick(e.getView(), e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invDrag(final InventoryDragEvent e) {
        inventoryManager.onDrag(e.getView(), e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invClose(final InventoryCloseEvent e) {
        inventoryManager.onClose(e.getView(), e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invOpen(final InventoryOpenEvent e) {
        inventoryManager.onOpen(e.getView(), e);
    }

    @EventHandler
    private void prepareCraft(final PrepareItemCraftEvent e) {
        if (e.getRecipe() != null
                && e.getRecipe().getResult().getType() == Material.AIR
                && e.getInventory().getResult() != null) {
            e.getInventory().setResult(ReplaceVanillaItems.changeVanillaLore(e.getInventory().getResult()));
        }
    }

    @EventHandler
    private void prepareSmithing(final PrepareSmithingEvent e) {
        final ItemStack result = e.getResult();
        if (result != null && result.hasItemMeta()) {
            final ItemMeta meta = result.getItemMeta();
            if (meta.hasLore()) {
                final List<Component> lore = meta.lore();
                assert lore != null;
                if (lore.size() == 1 && Text.plainStartsWith(lore.get(0), "防御力: ")) {
                    e.setResult(ReplaceVanillaItems.changeVanillaLore(result));
                    System.out.println(result);
                }
            }
        }
    }

}
