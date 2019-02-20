/*
 * InventoryListener.java
 * 
 * Copyright (c) 2018 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package jp.gr.java_conf.zakuramomiji.renewatelier.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.ConfirmInventory;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.DeliveryInventory;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle.AlchemyKettle;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle.CatalystSelect;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle.ItemSelect;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle.RecipeSelect;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.bag.AlchemyBagItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class InventoryListener implements Listener {

    private final Map<UUID, ItemStack> click_temp = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invClick(final InventoryClickEvent e) {
        final UUID uuid = e.getWhoClicked().getUniqueId();
        final Inventory inv = e.getInventory();
        final InventoryView view = e.getView();

        if (click_temp.containsKey(uuid)) {
            final ItemStack rcursor = click_temp.remove(uuid);
            if (rcursor.isSimilar(e.getCursor())) {
                e.setCancelled(true);
                return;
            }
        }

        if (ConfirmInventory.isConfirmInventory(view)) {
            e.setCancelled(e.getSlotType() == SlotType.CONTAINER);
            ConfirmInventory.click(e);
        } else if (DeliveryInventory.isDeliveryInventory(view)) {
            ConfirmInventory.click(e);
        } else if (RecipeSelect.isKettleRecipe(view)) {
            RecipeSelect.click(e);
        } else if (ItemSelect.isItemSelect(view)) {
            ItemSelect.click(e);
        } else if (CatalystSelect.isCatalystSelect(view)) {
            CatalystSelect.click(e);
        } else if (AlchemyKettle.isAlchemyKettle(view)) {
            AlchemyKettle.click(e);
        } else if (AlchemyBagItem.isBagInventory(view)) {
            AlchemyBagItem.click(e);
        } else if (inv.getType() == InventoryType.CRAFTING && e.getClick() == ClickType.RIGHT) { // player inv
            final AlchemyBagItem bag = AlchemyBagItem.getBag(e.getCurrentItem());
            if (bag != null) {
                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                    e.setCancelled(true);
                    AlchemyBagItem.openInventory((Player) e.getWhoClicked(), e.getCurrentItem(), e.getSlot());
                }/* else { // アイテム追加
                    final AlchemyMaterial material = AlchemyMaterial.getMaterial(e.getCursor());
                    if (material != null && bag.getType() == material) {
                        e.setCancelled(true);
                        click_temp.put(uuid, e.getCursor());
                        
                        final DoubleData<ItemStack, ItemStack> bagItem = AlchemyBagItem.addItem(e.getCurrentItem(), bag, e.getCursor());
                        e.setCurrentItem(bagItem.getLeft());
                    }
                }
                 */
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invDrag(final InventoryDragEvent e) {
        final InventoryView view = e.getView();
        if (ConfirmInventory.isConfirmInventory(view)) {
            ConfirmInventory.drag(e);
        } else if (RecipeSelect.isKettleRecipe(view)) {
            RecipeSelect.drag(e);
        } else if (ItemSelect.isItemSelect(view)) {
            ItemSelect.drag(e);
        } else if (CatalystSelect.isCatalystSelect(view)) {
            CatalystSelect.drag(e);
        } else if (AlchemyKettle.isAlchemyKettle(view)) {
            AlchemyKettle.drag(e);
        } else if (AlchemyBagItem.isBagInventory(view)) {
            AlchemyBagItem.drag(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invClose(final InventoryCloseEvent e) {
        final InventoryView view = e.getView();
        if (ConfirmInventory.isConfirmInventory(view)) {
            ConfirmInventory.close(e);
        } else if (ItemSelect.isItemSelect(view)) {
            ItemSelect.close(e);
        } else if (CatalystSelect.isCatalystSelect(view)) {
            CatalystSelect.close(e);
        } else if (AlchemyKettle.isAlchemyKettle(view)) {
            AlchemyKettle.close(e);
        } else if (AlchemyBagItem.isBagInventory(view)) {
            AlchemyBagItem.close(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void invOpen(final InventoryOpenEvent e) {
        final Player player = (Player) e.getPlayer();
    }
}