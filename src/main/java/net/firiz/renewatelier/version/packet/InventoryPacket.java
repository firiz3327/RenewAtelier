package net.firiz.renewatelier.version.packet;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
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
        CARTOGRAPHY_TABLE(Containers.CARTOGRAPHY_TABLE),
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
