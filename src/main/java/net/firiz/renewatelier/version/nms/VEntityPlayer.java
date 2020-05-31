package net.firiz.renewatelier.version.nms;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author firiz
 */
public class VEntityPlayer {

    private final Object entityPlayer; // EntityPlayer class
    private final int entityId;
    private final UUID uuid;
    private final String name;
    private final Location location;

    public VEntityPlayer(Object entityPlayer, int entityId, UUID uuid, String name, Location location) {
        this.entityPlayer = entityPlayer;
        this.entityId = entityId;
        this.uuid = uuid;
        this.name = name;
        this.location = location;
    }

    public Object getEntityPlayer() {
        return entityPlayer;
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public World getWorld() {
        return location.getWorld();
    }

}
