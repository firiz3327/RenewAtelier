/*
 * ItemSelect.java
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

import net.firiz.renewatelier.alchemy.kettle.KettleItemManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.inventory.manager.BiParamInventory;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.doubledata.ImmutablePair;
import net.firiz.renewatelier.version.packet.InventoryPacket;
import net.firiz.renewatelier.version.packet.InventoryPacket.InventoryPacketType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
public final class ItemSelect implements BiParamInventory<AlchemyRecipe, Inventory> {

    private final InventoryManager manager;
    private final KettleItemManager kettle = KettleItemManager.INSTANCE;
    private final List<UUID> openUsers = new ArrayList<>();

    public ItemSelect(final InventoryManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().equals(AlchemyInventoryType.KETTLE_SELECT_ITEM.getCheck());
    }

    @Override
    public void open(@NotNull Player player, @NotNull AlchemyRecipe recipe, @NotNull Inventory recipeInv) {
        final Inventory inv = Bukkit.createInventory(player, 45, AlchemyInventoryType.KETTLE_SELECT_ITEM.getCheck());
        final ItemStack settingItem = Chore.ci(Material.BARRIER, 0, "", null);
        final ItemMeta setting = settingItem.getItemMeta();
        AlchemyChore.setSettingStr(setting, 0, recipe.getId(), ""); // レシピID
        AlchemyChore.setSetting(setting, 1, 0, ""); // ページ
        settingItem.setItemMeta(setting);
        inv.setItem(0, Chore.ci(Material.DIAMOND_AXE, 1508, "", null));
        inv.setItem(36, Chore.ci(Material.DIAMOND_AXE, 1562, "", null));
        inv.setItem(1, settingItem);
        inv.setItem(2, recipeInv.getItem(2).clone());

        inv.setItem(3, Chore.ci(Material.BARRIER, 0, "", null)); // itemdatas
        for (int i = 4; i < inv.getSize(); i++) {
            if (i != 36) {
                inv.setItem(i, Chore.ci(Material.BARRIER, 0, "", null));
            }
        }
        setItemSelectNumber(player, inv, recipe, 0);
        player.openInventory(inv);
        InventoryPacket.update(player, "", InventoryPacketType.CHEST);
    }

    private void setItemSelectNumber(final Player player, final Inventory inv, final AlchemyRecipe recipe, final int add_page) {
        final List<String> reqs = recipe.getReqMaterial();
        final ItemStack settingItem = inv.getItem(1);
        final ItemMeta setting = settingItem.getItemMeta();
        final int new_page = Math.min(reqs.size() - 1, Math.max(0, AlchemyChore.getSetting(setting, 1) + add_page));
        AlchemyChore.setSetting(setting, 1, new_page, "");
        settingItem.setItemMeta(setting);

        final String[] data = reqs.get(new_page).split(",");
        String name = null;
        ImmutablePair<Material, Integer> material = null;
        if (data[0].startsWith("material:")) {
            final AlchemyMaterial am = AlchemyMaterial.getMaterial(data[0].substring(9));
            if (!am.isDefaultName()) {
                name = am.getName();
            }
            material = am.getMaterial();
        } else if (data[0].startsWith("category:")) {
            final Category c = Category.valueOf(data[0].substring(9));
            name = "§r" + c.getName();
            material = c.getMaterial();
        }

        if (material != null) {
            final int req_amount = Integer.parseInt(data[1]);
            final ItemStack item = new ItemStack(material.getLeft(), 1);
            final ItemMeta meta = item.getItemMeta();
            if (name != null) {
                meta.setDisplayName(name);
            }
            final List<String> lore = new ArrayList<>();
            lore.add("§7必要個数: " + req_amount);
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(4, item);

            final List<ItemStack> useItems = kettle.getPageItems(player.getUniqueId(), new_page);
            int count = 0;
            for (int i = 1; i <= 3; i++) { //12,13,14 - 21,22,23 - 30,31,32
                for (int j = 0; j < 3; j++) {
                    final int new_slot = i * 12 - ((i - 1) * 3) + j;
                    if (count >= useItems.size()) {
                        inv.setItem(new_slot, null);
                    } else {
                        inv.setItem(new_slot, useItems.get(count));
                    }
                    count++;
                }
            }
        }

    }

    private boolean checkMaxSlot(final UUID uuid, final AlchemyRecipe recipe, final int page) {
        final List<String> reqs = recipe.getReqMaterial();
        final List<ItemStack> pageItems = kettle.getPageItems(uuid, page);
        final String[] data = reqs.get(page).split(",");
        final ImmutablePair<Material, Integer> material = data[0].startsWith("material:")
                ? AlchemyMaterial.getMaterial(data[0].substring(9)).getMaterial()
                : (data[0].startsWith("category:")
                ? Category.valueOf(data[0].substring(9)).getMaterial() : null);
        if (material != null && !pageItems.isEmpty()) {
            final int req_amount = Integer.parseInt(data[1]);
            Chore.log(pageItems.size() + " " + (pageItems.size() >= req_amount) + " " + Arrays.toString(data));
            return pageItems.size() < req_amount;
        }
        return true;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) { // 増殖防止
            e.setCancelled(true);
            return;
        }
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final ItemMeta setting = inv.getItem(1).getItemMeta();
        final int page = AlchemyChore.getSetting(setting, 1);
        final int raw = e.getRawSlot();
        final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(setting.getLore().get(0)));
        if ((e.getSlotType() == InventoryType.SlotType.CONTAINER || e.getSlotType() == InventoryType.SlotType.QUICKBAR) && raw >= inv.getSize() && e.isShiftClick()) {
            e.setCancelled(true);
            final ItemStack current = e.getCurrentItem();
            if (current != null && current.getType() != Material.AIR && checkMaxSlot(uuid, recipe, page)) {
                final String[] data = recipe.getReqMaterial().get(AlchemyChore.getSetting(setting, 1)).split(",");
                if (Chore.checkMaterial(current, data[0])) {
                    final ItemStack cloneItem = current.clone();
                    cloneItem.setAmount(1);
                    kettle.addPageItem(uuid, cloneItem, page);
                    e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
                    setItemSelectNumber(player, inv, recipe, 0); // reflesh page
                }
            }
            return;
        }
        if (e.getSlotType() == InventoryType.SlotType.CONTAINER && raw < inv.getSize()) {
            e.setCancelled(true);
            if ((raw >= 12 && raw <= 14) // 配置可能スロット
                    || (raw >= 21 && raw <= 23)
                    || (raw >= 30 && raw <= 32)) {
                final ItemStack current = e.getCurrentItem();
                if (current != null) {
                    final ItemStack cursor = e.getCursor();
                    if (current.getType() != Material.AIR) { // アイテムをスロットから外す
                        final List<ItemStack> items = kettle.getPageItems(uuid, page);
                        if (items != null && !items.isEmpty()) {
                            final int slot = raw >= 21 ? (raw >= 30 ? raw - 24 : raw - 18) : raw - 12;
                            final ItemStack item = items.get(slot);
                            Chore.addItem(player, item);
                            kettle.removePageItem(uuid, slot, page);
                        }
                    } else if (checkMaxSlot(uuid, recipe, page)) { // アイテムをスロットに設置
                        final String[] data = recipe.getReqMaterial().get(AlchemyChore.getSetting(setting, 1)).split(",");
                        if (Chore.checkMaterial(cursor, data[0])) {
                            final ItemStack cloneItem = cursor.clone();
                            cloneItem.setAmount(1);
                            cursor.setAmount(cursor.getAmount() - 1);
                            kettle.addPageItem(uuid, cloneItem, page);
                        }
                    }
                    setItemSelectNumber(player, inv, recipe, 0); // reflesh page
                }
            } else if (raw == 20 || raw == 24) { // ページ変更
                setItemSelectNumber(player, inv, recipe, raw != 20 ? 1 : -1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
            } else if (raw == 40) { // 決定
                int check = 0;
                for (int i = 0; i < recipe.getReqMaterial().size(); i++) {
                    if (checkMaxSlot(uuid, recipe, i)) {
                        check = 0;
                        break;
                    }
                    check++;
                }
                if (check == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                    return;
                }
                openUsers.add(uuid);
                player.closeInventory();
                manager.getInventory(CatalystSelect.class).open(player, recipe, inv);
                openUsers.remove(uuid);
            }
        }
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent e) {
        final Set<Integer> raws = e.getRawSlots();
        final Inventory inv = e.getInventory();
        raws.stream().filter(raw -> (raw >= 0 && raw < inv.getSize())).forEach(itemValue -> e.setCancelled(true));
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent e) {
        if (!openUsers.contains(e.getPlayer().getUniqueId())) {
            kettle.allBack((Player) e.getPlayer());
        }
    }
}
