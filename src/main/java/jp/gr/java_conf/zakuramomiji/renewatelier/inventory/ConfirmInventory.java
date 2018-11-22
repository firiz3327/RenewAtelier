/*
 * ConfirmInventory.java
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

import java.util.HashMap;
import java.util.Map;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.InventoryPacket.InventoryPacketType;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.PlayerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;

/**
 *
 * @author firiz
 */
public final class ConfirmInventory {

    private static final Map<String, PlayerRunnable> RUNS = new HashMap<>();
    private static final String CONFSTR = "-Confirm";

    private ConfirmInventory() {
    }

    public static boolean isConfirmInventory(Inventory inv) {
        return inv.getTitle().endsWith(CONFSTR);
    }

    public static void openInventory(final Player player, final String id, final String title, final String yes, final String no, final PlayerRunnable run) {
        final Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, id.concat(CONFSTR));
        inv.setItem(1, Chore.ci(Material.LIME_WOOL, 0/*5*/, yes, null));
        inv.setItem(3, Chore.ci(Material.RED_WOOL, 0/*14*/, no, null));
        RUNS.put(id, run);
        player.openInventory(inv);
        InventoryPacket.update(player, title, InventoryPacketType.HOPPER);
    }

    public static void openInventory(final Player player, final String title, final String yes, final String no, final PlayerRunnable run) {
        final String uuid = player.getUniqueId().toString();
        final Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, uuid.concat(CONFSTR));
        inv.setItem(1, Chore.ci(Material.LIME_WOOL, 0/*5*/, yes, null));
        inv.setItem(3, Chore.ci(Material.RED_WOOL, 0/*14*/, no, null));
        RUNS.put(uuid, run);
        player.openInventory(inv);
        InventoryPacket.update(player, title, InventoryPacketType.HOPPER);
    }

    public static void click(final InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();
        final String invname = inv.getTitle().replaceAll(CONFSTR, "");
        if (e.getSlotType() == SlotType.CONTAINER) {
            e.setCancelled(true);
        }
        switch (e.getRawSlot()) {
            case 1:
                RUNS.get(invname).run(player);
                player.closeInventory();
                break;
            case 3:
                player.closeInventory();
                break;
        }
        if (invname.equals(player.getUniqueId().toString())) {
            RUNS.remove(invname);
        }
    }

    public static void drag(final InventoryDragEvent e) {
        final Inventory inv = e.getInventory();
        e.getRawSlots().stream()
                .filter((raw) -> (raw >= 0 && raw < inv.getSize()))
                .forEach((_item) -> {
                    e.setCancelled(true);
                });
    }

    public static void close(final InventoryCloseEvent e) {
        final String invname = e.getInventory().getTitle().replaceAll(CONFSTR, "");
        final Player player = (Player) e.getPlayer();
        if (invname.equals(player.getUniqueId().toString())) {
            RUNS.remove(invname);
        }
    }

}
