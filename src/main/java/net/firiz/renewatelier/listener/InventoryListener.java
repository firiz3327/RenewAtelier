package net.firiz.renewatelier.listener;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.inventory.AnvilManager;
import net.firiz.renewatelier.inventory.manager.CustomInventory;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * @author firiz
 */
public class InventoryListener implements Listener {

    private final Map<UUID, ItemStack> clickTemp = new Object2ObjectOpenHashMap<>();
    private final InventoryManager inventoryManager;

    public InventoryListener(AtelierPlugin atelierPlugin) {
        this.inventoryManager = atelierPlugin.getInventoryManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invClick(final InventoryClickEvent e) {
        final UUID uuid = e.getWhoClicked().getUniqueId();

        if (clickTemp.containsKey(uuid)) {
            final ItemStack rCursor = clickTemp.remove(uuid);
            if (rCursor.isSimilar(e.getCursor())) {
                e.setCancelled(true);
                return;
            }
        }

        final CustomInventory customInventory = inventoryManager.getInventory(e.getView());
        if (customInventory != null) {
            customInventory.onClick(e);
        }

//        if (ConfirmInventory.isConfirmInventory(view)) {
//            ConfirmInventory.click(e);
//        } else if (DeliveryInventory.isDeliveryInventory(view)) {
//            DeliveryInventory.click(e);
//        } else if (RecipeSelect.isKettleRecipe(view)) {
//            RecipeSelect.click(e);
//        } else if (ItemSelect.isItemSelect(view)) {
//            ItemSelect.click(e);
//        } else if (CatalystSelect.isCatalystSelect(view)) {
//            CatalystSelect.click(e);
//        } else if (AlchemyKettle.isAlchemyKettle(view)) {
//            AlchemyKettle.click(e);
//        } else if (ShopInventory.isShopInventory(view)) {
//            ShopInventory.click(e);
//        } else if (view.getTopInventory() instanceof AnvilInventory) {
//            AnvilManager.click(e);
//        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invDrag(final InventoryDragEvent e) {
        final CustomInventory customInventory = inventoryManager.getInventory(e.getView());
        if (customInventory != null) {
            customInventory.onDrag(e);
        }

//        final InventoryView view = e.getView();
//        if (ConfirmInventory.isConfirmInventory(view)) {
//            ConfirmInventory.drag(e);
//        } else if (DeliveryInventory.isDeliveryInventory(view)) {
//            DeliveryInventory.drag(e);
//        } else if (RecipeSelect.isKettleRecipe(view)) {
//            RecipeSelect.drag(e);
//        } else if (ItemSelect.isItemSelect(view)) {
//            ItemSelect.drag(e);
//        } else if (CatalystSelect.isCatalystSelect(view)) {
//            CatalystSelect.drag(e);
//        } else if (AlchemyKettle.isAlchemyKettle(view)) {
//            AlchemyKettle.drag(e);
//        } else if (view.getTopInventory() instanceof AnvilInventory) {
//            AnvilManager.drag(e);
//        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invClose(final InventoryCloseEvent e) {
        final CustomInventory customInventory = inventoryManager.getInventory(e.getView());
        if (customInventory != null) {
            customInventory.onClose(e);
        }

//        final InventoryView view = e.getView();
//        if (ConfirmInventory.isConfirmInventory(view)) {
//            ConfirmInventory.close(e);
//        } else if (DeliveryInventory.isDeliveryInventory(view)) {
//            DeliveryInventory.close(e);
//        } else if (ItemSelect.isItemSelect(view)) {
//            ItemSelect.close(e);
//        } else if (CatalystSelect.isCatalystSelect(view)) {
//            CatalystSelect.close(e);
//        } else if (AlchemyKettle.isAlchemyKettle(view)) {
//            AlchemyKettle.close(e);
//        } else if (view.getTopInventory() instanceof AnvilInventory) {
//            AnvilManager.close(e);
//        }
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
