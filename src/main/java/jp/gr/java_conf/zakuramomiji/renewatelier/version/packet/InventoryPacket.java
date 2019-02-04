/*
 * InventoryPacket.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.version.packet;

import net.minecraft.server.v1_13_R2.ChatMessage;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public class InventoryPacket {

    public static void update(final Player player, final String title, final InventoryPacketType type) {
        update(player, title, type.id);
    }

    public static void update(final Player player, final String title, final String id) {
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        final PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(
                ep.activeContainer.windowId,
                "minecraft:".concat(id),
                new ChatMessage(title),
                player.getOpenInventory().getTopInventory().getSize()
        );
        ep.playerConnection.sendPacket(packet);
        ep.updateInventory(ep.activeContainer);
    }

    public enum InventoryPacketType {
        CHEST("chest"),
        HOPPER("hopper");

        private final String id;

        private InventoryPacketType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

    }
}
