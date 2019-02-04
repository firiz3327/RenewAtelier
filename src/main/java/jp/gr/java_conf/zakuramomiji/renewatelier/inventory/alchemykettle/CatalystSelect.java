/*
 * CatalystSelect.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle;

import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle.KettleItemManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.Catalyst;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Category;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.AlchemyRecipe;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.AlchemyInventoryType;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author firiz
 */
public class CatalystSelect {

    private final static KettleItemManager KETTLE = KettleItemManager.INSTANCE;
    private final static List<UUID> OPEN_USERS = new ArrayList<>();

    public static boolean isCatalystSelect(final InventoryView view) {
        return view.getTitle().equals(AlchemyInventoryType.KETTLE_SELECT_CATALYST.getCheck());
    }

    public static void openCatalyst(final Player player, final AlchemyRecipe recipe, final Inventory itemInv) {
        final Inventory inv = Bukkit.createInventory(player, 54, AlchemyInventoryType.KETTLE_SELECT_CATALYST.getCheck());
        inv.setItem(1, itemInv.getItem(1).clone());
        inv.setItem(2, itemInv.getItem(2).clone());
        setCatalystSlot(player, inv, recipe);
        player.openInventory(inv);
    }

    private static void setCatalystSlot(final Player player, final Inventory inv, final AlchemyRecipe recipe) {
        for (int i = 3; i < inv.getSize(); i++) {
            inv.setItem(i, null);
        }

        final ItemStack citem = KETTLE.getCatalyst(player.getUniqueId());
        Catalyst catalyst;
        if (citem == null) {
            catalyst = Catalyst.DEFAULT;
        } else {
            catalyst = AlchemyMaterial.getMaterial(citem).getCatalyst();
        }
        catalyst.setInv(inv, recipe, false);
        final List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "使用可能カテゴリー");
        recipe.getCatalyst_categorys().stream().forEach((ct) -> {
            if (ct.startsWith("material:")) {
                lore.add(ChatColor.RESET + "- "
                        + AlchemyMaterial.getMaterial(ct.substring(9)).getName()
                );
            } else if (ct.startsWith("category:")) {
                lore.add(
                        ChatColor.RESET + "- "
                        + Category.valueOf(ct.substring(9)).getName()
                );
            }
        });
        inv.setItem(37, Chore.ci(
                citem == null ? Material.BARRIER : citem.getType(),
                0,
                ChatColor.GRAY + "現在の触媒： " + ChatColor.RESET + (citem == null ? "触媒を指定せずに作成" : citem.getItemMeta().getDisplayName()),
                lore
        ));
    }

    public static void click(final InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) { // 増殖防止 - チェスト内のアイテムにフラッグを付与すれば対処可能
            e.setCancelled(true);
            return;
        }
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final ItemMeta setting = inv.getItem(1).getItemMeta();
        final int raw = e.getRawSlot();
        final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(setting.getLore().get(0)));
        if (!(raw < inv.getSize()) && e.isShiftClick()) {
            e.setCancelled(true);
            if (inv.getItem(37).getType() != Material.BARRIER) {
                Chore.addItem(player, KETTLE.getCatalyst(uuid));
            }
            final ItemStack current = e.getCurrentItem();
            final AlchemyMaterial am = AlchemyMaterial.getMaterial(current);
            if (am != null && am.hasUsefulCatalyst(recipe)) {
                final ItemStack cloneItem = current.clone();
                cloneItem.setAmount(1);
                current.setAmount(current.getAmount() - 1);
                KETTLE.setCatalyst(uuid, cloneItem);
                setCatalystSlot(player, inv, recipe);
            }
            return;
        }
        if (e.getSlotType() == InventoryType.SlotType.CONTAINER) {
            if (raw < inv.getSize()) {
                e.setCancelled(true);
                switch (raw) {
                    case 37: {
                        if (e.getCurrentItem().getType() == Material.BARRIER) {
                            final ItemStack cursor = e.getCursor();
                            final AlchemyMaterial am = AlchemyMaterial.getMaterial(cursor);
                            if (am != null && am.hasUsefulCatalyst(recipe)) {
                                final ItemStack cloneItem = cursor.clone();
                                cloneItem.setAmount(1);
                                cursor.setAmount(cursor.getAmount() - 1);
                                KETTLE.setCatalyst(uuid, cloneItem);
                                setCatalystSlot(player, inv, recipe);
                            }
                        } else {
                            Chore.addItem(player, KETTLE.getCatalyst(uuid));
                            KETTLE.removeCatalyst(uuid);
                            setCatalystSlot(player, inv, recipe);
                        }
                        break;
                    }
                    case 19: {
                        if (KETTLE.getCatalyst(uuid) == null) {
                            KETTLE.setCatalyst(uuid, null);
                        }
                        OPEN_USERS.add(player.getUniqueId());
                        player.closeInventory();
                        AlchemyKettle.openKettle(player, recipe, inv);
                        OPEN_USERS.remove(uuid);
                        break;
                    }
                }
            }
        }
    }

    public static void drag(final InventoryDragEvent e) {
        final Set<Integer> raws = e.getRawSlots();
        final Inventory inv = e.getInventory();
        raws.stream().filter((raw) -> (raw >= 0 && raw < inv.getSize())).forEach((_item) -> {
            e.setCancelled(true);
        });
    }

    public static void close(final InventoryCloseEvent e) {
        if (!OPEN_USERS.contains(e.getPlayer().getUniqueId())) {
            KETTLE.allBack((Player) e.getPlayer());
        }
    }
}
