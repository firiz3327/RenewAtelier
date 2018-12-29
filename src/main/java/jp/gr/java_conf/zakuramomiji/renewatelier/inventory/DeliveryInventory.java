/*
 * DeliveryInventory.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Ingredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.InventoryPacket.InventoryPacketType;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.npc.NPCManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class DeliveryInventory {

    private static final String DELISTR = "-Delivery";

    private DeliveryInventory() {
    }

    public static boolean isDeliveryInventory(Inventory inv) {
        return inv.getTitle().endsWith(DELISTR);
    }

    // slots
    // 00 01 02 03 04 05 06 07 08
    // 09 10 11 12 13 14 15 16 17
    // 18 19 20 21 22 23 24 25 26
    public static void openInventory(final Player player, final String title, final int line_size, final AlchemyMaterial material, final List<Characteristic> characteristics, final List<AlchemyIngredients> ingredients) {
        if (line_size < 2) {
            throw new IllegalArgumentException("The line_size variable must be 2 or more.");
        }

        // materialid,[req characteristics] or none,[req ingredients] or none,-Delivery
        final StringBuilder sb = new StringBuilder();
        sb.append(material.getId());

        boolean first = true;
        for (final Characteristic c : characteristics) {
            sb.append(first ? ",c[" : ",").append(c.toString());
            first = false;
        }
        sb.append(characteristics.isEmpty() ? "" : "]");

        first = true;
        for (final AlchemyIngredients ing : ingredients) {
            sb.append(first ? ",i[" : ",").append(ing.toString());
            first = false;
        }
        sb.append(ingredients.isEmpty() ? "" : "]").append(DELISTR);

        final int size = line_size * 9;
        final Inventory inv = Bukkit.createInventory(player, line_size * 9, sb.toString());
        for (int i = size - 9; i < size; i++) {
            switch (i) {
                case 21:
                    inv.setItem(1, Chore.ci(Material.LIME_WOOL, 0, ChatColor.GREEN + "納品", null));
                    break;
                case 23:
                    inv.setItem(i, Chore.ci(Material.RED_WOOL, 0, ChatColor.RED + "キャンセル", null));
                    break;
                default:
                    inv.setItem(i, Chore.ci(Material.BARRIER, 0, "§r", null));
                    break;
            }
        }
        player.openInventory(inv);
        InventoryPacket.update(player, title, InventoryPacketType.CHEST);
    }

    public static void click(final InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final Inventory inv = e.getInventory();
        final int confirm_pos = inv.getSize() - 6;
        final int cancel_pos = inv.getSize() - 4;

        if (e.getRawSlot() == confirm_pos) {
            e.setCancelled(true);
            invoke(uuid, "confirm");
            player.closeInventory();
        } else if (e.getRawSlot() == cancel_pos) {
            e.setCancelled(true);
            player.closeInventory();
        } else if (e.getSlotType() == SlotType.CONTAINER && e.getRawSlot() <= inv.getSize() - 9) {
            e.setCancelled(true);
        }

        updateInventory(inv, player);
    }

    private static void updateInventory(final Inventory inv, final Player player) {
        final String[] datas = inv.getTitle().split(",");
        final AlchemyMaterial material = AlchemyMaterial.getMaterial(datas[0]);
        final List<Characteristic> req_characteristics = new ArrayList<>();
        final List<Ingredients> req_ingredients = new ArrayList<>();
        
        if (datas.length <= 2) {
            for (int i = 1; i < datas.length; i++) {
                final String[] strs = datas[i].substring(2, datas[i].length() - 1).split(",");
                if (datas[i].startsWith("c[")) {
                    for (final String str : strs) {
                        req_characteristics.add(Characteristic.valueOf(str));
                    }
                } else if (datas[i].startsWith("i[")) {
                    for (final String str : strs) {
                        req_ingredients.add(AlchemyIngredients.valueOf(str));
                    }
                }
            }
        }

        final ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            final AlchemyMaterial am = AlchemyMaterial.getMaterial(item);

            boolean check = true;
            check_itemdata:
            if (am != null && material == am) {
                check = false;
                for (final Characteristic c : req_characteristics) {
                    if (!AlchemyItemStatus.getCharacteristics(item).contains(c)) {
                        check = true;
                        break check_itemdata;
                    }
                }
                for (final Ingredients ing : req_ingredients) {
                    if (!AlchemyItemStatus.getIngredients(item).contains(ing)) {
                        check = true;
                        break check_itemdata;
                    }
                }
            }
            if (check) {
                items[i] = null;
                Chore.addItem(player, item);
            }
        }
        inv.setContents(items);
    }

    private static void invoke(final UUID uuid, final String method) {
        try {
            NPCManager.INSTANCE.getNPCConversation(uuid).getIv().invokeFunction(method);
        } catch (ScriptException | NoSuchMethodException ex) {
            Logger.getLogger(DeliveryInventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void drag(final InventoryDragEvent e) {
        final Inventory inv = e.getInventory();
        final int confirm_pos = inv.getSize() - 6;
        final int cancel_pos = inv.getSize() - 4;
        for (final int raw : e.getRawSlots()) {
            if (raw == confirm_pos || raw == cancel_pos || raw <= inv.getSize() - 9) {
                e.setCancelled(true);
                break;
            }
        }
    }

    public static void close(final InventoryCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        final UUID uuid = player.getUniqueId();
        invoke(uuid, "close");
    }

}
