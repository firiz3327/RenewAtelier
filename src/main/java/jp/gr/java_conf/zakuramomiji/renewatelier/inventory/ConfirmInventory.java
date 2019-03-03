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
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.InventoryPacket;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.InventoryPacket.InventoryPacketType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

/**
 *
 * @author firiz
 */
public final class ConfirmInventory {

    private static final Map<UUID, DoubleData<String, ClickRunnable>> RUNS = new HashMap<>();
    private static final String CONFSTR = "-Confirm";

    private ConfirmInventory() {
    }

    public static boolean isConfirmInventory(final InventoryView view) {
        return view.getTitle().endsWith(CONFSTR);
    }

    public static void openInventory(final Player player, final String title, final String yes, final String no, final ClickRunnable run) {
        final UUID uuid = player.getUniqueId();
        final Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, uuid.toString().concat(CONFSTR));
        inv.setItem(1, Chore.ci(Material.LIME_WOOL, 0, yes, null));
        inv.setItem(3, Chore.ci(Material.RED_WOOL, 0, no, null));
        if (!RUNS.containsKey(uuid) || !RUNS.get(uuid).getLeft().equals(title)) {
            RUNS.put(uuid, new DoubleData<>(title, run));
        }
        player.openInventory(inv);
        InventoryPacket.update(player, title, InventoryPacketType.HOPPER);
    }

    public static void click(final InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final ClickRunnable cr = RUNS.get(uuid).getRight();
        switch (e.getRawSlot()) {
            case 1:
                cr.run(player, 1);
                break;
            case 3:
                cr.run(player, 0);
                break;
        }
    }

    public static void drag(final InventoryDragEvent e) {
        final Inventory inv = e.getInventory();
        e.getRawSlots().stream()
                .filter((raw) -> (raw >= 0 && raw < inv.getSize()))
                .forEach((_item) -> e.setCancelled(true));
    }

    public static void close(final InventoryCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (RUNS.containsKey(uuid)) {
            final ClickRunnable cr = RUNS.get(uuid).getRight();
            cr.run(player, -1);
            RUNS.remove(uuid);
        }
    }

    public interface ClickRunnable {

        void run(Player player, int select);
    }

}
