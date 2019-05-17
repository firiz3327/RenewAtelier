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
package net.firiz.renewatelier.version.packet;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public class InventoryPacket {

    public static void update(final Player player, final String title, final InventoryPacketType type) {
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();

        final PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(
                ep.activeContainer.windowId,
                type.getContainer(player.getOpenInventory().getTopInventory().getSize()),
                new ChatMessage(title)
        );
        ep.playerConnection.sendPacket(packet);
        ep.updateInventory(ep.activeContainer);
    }

    public enum InventoryPacketType {
        CHEST(null),
        DISPENSER(Containers.GENERIC_3X3),
        DROPPER(Containers.GENERIC_3X3),
        HOPPER(Containers.HOPPER),
        ANVIL(Containers.ANVIL),
        BEACON(Containers.BEACON),
        FURNACE(Containers.FURNACE),
        CRAFTING(Containers.CRAFTING),
        ENCHANTMENT(Containers.ENCHANTMENT),
        BLAST_FURNACE(Containers.BLAST_FURNACE),
        BREWING_STAND(Containers.BREWING_STAND),
        CARTOGRAPHY(Containers.CARTOGRAPHY),
        GRIND_STONE(Containers.GRINDSTONE),
        LECTERN(Containers.LECTERN),
        LOOM(Containers.LOOM),
        MERCHANT(Containers.MERCHANT),
        SHULKER_BOX(Containers.SHULKER_BOX),
        SMOKER(Containers.SMOKER),
        STONE_CUTTER(Containers.STONECUTTER);

        private final Containers<?> container;

        InventoryPacketType(Containers<?> container) {
            this.container = container;
        }

        public final Containers<?> getContainer(int size) {
            if (this == CHEST) {
                switch (size) {
                    case 9:
                        return Containers.GENERIC_9X1;
                    case 18:
                        return Containers.GENERIC_9X2;
                    case 27:
                        return Containers.GENERIC_9X3;
                    case 36:
                        return Containers.GENERIC_9X4;
                    case 45:
                        return Containers.GENERIC_9X5;
                    case 54:
                        return Containers.GENERIC_9X6;
                    default:
                        throw new IllegalArgumentException(size + " is unsupported size.");
                }
            }
            return container;
        }

    }
}
