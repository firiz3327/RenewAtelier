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
package net.firiz.renewatelier.inventory.alchemykettle;

import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.kettle.KettleItemManager;

import java.util.*;

import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.inventory.manager.BiParamInventory;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.utils.Chore;
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
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public final class CatalystSelect implements BiParamInventory<AlchemyRecipe, Inventory> {

    private static final KettleItemManager kettle = KettleItemManager.INSTANCE;
    private final InventoryManager manager;
    private final List<UUID> openUsers = new ArrayList<>();

    public CatalystSelect(final InventoryManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().equals(AlchemyInventoryType.KETTLE_SELECT_CATALYST.getCheck());
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final AlchemyRecipe recipe, @NotNull final Inventory itemInv) {
        final Inventory inv = Bukkit.createInventory(player, 54, AlchemyInventoryType.KETTLE_SELECT_CATALYST.getCheck());
        inv.setItem(1, itemInv.getItem(1).clone());
        inv.setItem(2, itemInv.getItem(2).clone());
        setCatalystSlot(player, inv, recipe);
        player.openInventory(inv);
    }

    private void setCatalystSlot(final Player player, final Inventory inv, final AlchemyRecipe recipe) {
        for (int i = 3; i < inv.getSize(); i++) {
            inv.setItem(i, null);
        }

        final ItemStack cItem = kettle.getCatalyst(player.getUniqueId());
        Catalyst catalyst;
        if (cItem == null) {
            catalyst = Catalyst.getDefaultCatalyst();
        } else {
            catalyst = AlchemyMaterial.getMaterial(cItem).getCatalyst();
        }
        catalyst.setInv(inv, recipe, false);
        final List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "使用可能カテゴリー");
        recipe.getCatalystCategories().forEach(ct -> {
            switch (ct.getType()) {
                case MATERIAL:
                    lore.add(ChatColor.RESET + "- "
                            + ct.getMaterial().getName()
                    );
                    break;
                case CATEGORY:
                    lore.add(
                            ChatColor.RESET + "- "
                                    + ct.getCategory().getName()
                    );
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + ct.getType());
            }
        });
        inv.setItem(37, Chore.ci(
                cItem == null ? Material.BARRIER : cItem.getType(),
                0,
                ChatColor.GRAY + "現在の触媒： " + ChatColor.RESET + (cItem == null ? "触媒を指定せずに作成" : Objects.requireNonNull(cItem.getItemMeta()).getDisplayName()),
                lore
        ));
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent e) {
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
        if (raw >= inv.getSize() && e.isShiftClick()) {
            e.setCancelled(true);
            if (inv.getItem(37).getType() != Material.BARRIER) {
                Chore.addItem(player, kettle.getCatalyst(uuid));
            }
            final ItemStack current = e.getCurrentItem();
            setCatalystItem(inv, player, uuid, recipe, current);
            return;
        }
        if (e.getSlotType() == InventoryType.SlotType.CONTAINER && raw < inv.getSize()) {
            e.setCancelled(true);
            switch (raw) {
                case 37: {
                    if (e.getCurrentItem().getType() == Material.BARRIER) {
                        final ItemStack cursor = e.getCursor();
                        setCatalystItem(inv, player, uuid, recipe, cursor);
                    } else {
                        Chore.addItem(player, kettle.getCatalyst(uuid));
                        kettle.removeCatalyst(uuid);
                        setCatalystSlot(player, inv, recipe);
                    }
                    break;
                }
                case 19: {
                    if (kettle.getCatalyst(uuid) == null) {
                        kettle.setCatalyst(uuid, null);
                    }
                    openUsers.add(player.getUniqueId());
                    player.closeInventory();
                    manager.getInventory(AlchemyKettle.class).open(player, recipe, inv);
                    openUsers.remove(uuid);
                    break;
                }
                default:
                    // 想定外スロット
                    break;
            }
        }
    }

    private void setCatalystItem(Inventory inv, Player player, UUID uuid, AlchemyRecipe recipe, ItemStack item) {
        final AlchemyMaterial am = AlchemyMaterial.getMaterialOrNull(item);
        if (am != null && am.hasUsefulCatalyst(recipe)) {
            final ItemStack cloneItem = item.clone();
            cloneItem.setAmount(1);
            item.setAmount(item.getAmount() - 1);
            kettle.setCatalyst(uuid, cloneItem);
            setCatalystSlot(player, inv, recipe);
        }
    }

    @Override
    public void onDrag(@NotNull final InventoryDragEvent e) {
        final Set<Integer> raws = e.getRawSlots();
        final Inventory inv = e.getInventory();
        raws.stream().filter(raw -> (raw >= 0 && raw < inv.getSize())).forEach(itemValue -> e.setCancelled(true));
    }

    @Override
    public void onClose(@NotNull final InventoryCloseEvent e) {
        if (!openUsers.contains(e.getPlayer().getUniqueId())) {
            kettle.allBack((Player) e.getPlayer());
        }
    }
}
