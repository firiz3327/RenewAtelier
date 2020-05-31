package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.inventory.AnvilManager;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;

/**
 * @author firiz
 */
public class InventoryListener implements Listener {

    private final InventoryManager inventoryManager;

    public InventoryListener(AtelierPlugin atelierPlugin) {
        this.inventoryManager = atelierPlugin.getInventoryManager();
    }

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
        final InventoryView view = e.getView();
        if (view.getTopInventory() instanceof AnvilInventory) {
            AnvilManager.open(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void prepareAnvil(final PrepareAnvilEvent e) {
        AnvilManager.prepare(e);
    }

    @EventHandler
    private void prepareCraft(final PrepareItemCraftEvent e) {
        if (e.getRecipe() != null
                && e.getRecipe().getResult().getType() == Material.AIR
                && e.getInventory().getResult() != null) {
            e.getInventory().setResult(ReplaceVanillaItems.changeVanillaLore(e.getInventory().getResult()));
        }
    }

}
