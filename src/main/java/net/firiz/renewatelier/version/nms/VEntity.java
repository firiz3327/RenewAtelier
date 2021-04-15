package net.firiz.renewatelier.version.nms;

import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class VEntity<T extends Entity> {

    private final T entity;
    private final int entityId;
    private final UUID uuid;
    private final String name;
    private final Location location;

    public VEntity(T entity, int entityId, String name, Location location) {
        this(entity, entityId, UUID.randomUUID(), name, location);
    }

    public VEntity(T entity, int entityId, UUID uuid, String name, Location location) {
        this.entity = entity;
        this.entityId = entityId;
        this.uuid = uuid;
        this.name = name;
        this.location = location;
    }

    public T getEntity() {
        return entity;
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
        return location.clone();
    }

    public Location getEyeLocation() {
        return getLocation().add(0, entity.getHeadHeight(), 0);
    }

    public World getWorld() {
        return location.getWorld();
    }
}
