package net.firiz.renewatelier.npc;

import net.firiz.renewatelier.version.nms.VEntityPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NPC {

    @NotNull
    private final NPCObject npcObject;
    @Nullable
    private final Entity entity;
    @Nullable
    private final VEntityPlayer entityPlayer;

    NPC(@NotNull NPCObject npcObject, @NotNull Entity entity) {
        this.npcObject = npcObject;
        this.entity = Objects.requireNonNull(entity);
        this.entityPlayer = null;
    }

    NPC(@NotNull NPCObject npcObject, @NotNull VEntityPlayer entityPlayer) {
        this.npcObject = npcObject;
        this.entity = null;
        this.entityPlayer = Objects.requireNonNull(entityPlayer);
    }

    @NotNull
    public NPCObject getNpcObject() {
        return npcObject;
    }

    public boolean isPlayer() {
        return entityPlayer != null;
    }

    public int getEntityId() {
        return isPlayer() ? Objects.requireNonNull(entityPlayer).getEntityId() : Objects.requireNonNull(entity).getEntityId();
    }

    public String getName() {
        return isPlayer() ? Objects.requireNonNull(entityPlayer).getName() : Objects.requireNonNull(entity).getCustomName();
    }

    public Location getLocation() {
        return isPlayer() ? Objects.requireNonNull(entityPlayer).getLocation() : Objects.requireNonNull(entity).getLocation();
    }

    @NotNull
    public Entity getEntity() {
        if (!isPlayer()) {
            return Objects.requireNonNull(entity);
        }
        throw new IllegalStateException("player npc");
    }

    @NotNull
    public VEntityPlayer getEntityPlayer() {
        if (isPlayer()) {
            return Objects.requireNonNull(entityPlayer);
        }
        throw new IllegalStateException("entity npc");
    }
}
