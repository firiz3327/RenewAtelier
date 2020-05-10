package net.firiz.renewatelier.version.packet;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * @author firiz
 */
public class PayloadPacket {

    private PayloadPacket() {
    }

    public static void openBook(final Player player, final EquipmentSlot hand) {
        PacketUtils.sendPacket(
                player,
                new PacketPlayOutOpenBook(
                        hand == EquipmentSlot.HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND
                )
        );
    }

}
