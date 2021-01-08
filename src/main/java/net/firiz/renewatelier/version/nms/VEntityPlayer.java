package net.firiz.renewatelier.version.nms;

import java.util.UUID;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Location;

/**
 *
 * @author firiz
 */
public class VEntityPlayer extends VEntity<EntityPlayer> {

    public VEntityPlayer(EntityPlayer entityPlayer, int entityId, UUID uuid, String name, Location location) {
        super(entityPlayer, entityId, uuid, name, location);
    }

}
