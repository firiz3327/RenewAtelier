package net.firiz.renewatelier.version.packet;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.MinecraftVersion;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.block.Lectern;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_16_R1.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.OptionalInt;

/**
 * @author firiz
 */
public class InventoryPacket {

    @MinecraftVersion("1.15")
    public static void update(final Player player, final String title, final InventoryPacketType type) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(
                entityPlayer.activeContainer.windowId,
                type.getContainer(player.getOpenInventory().getTopInventory().getSize()),
                new ChatMessage(title)
        );
        entityPlayer.playerConnection.sendPacket(packet);
        entityPlayer.updateInventory(entityPlayer.activeContainer);
    }

    @MinecraftVersion("1.15")
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
