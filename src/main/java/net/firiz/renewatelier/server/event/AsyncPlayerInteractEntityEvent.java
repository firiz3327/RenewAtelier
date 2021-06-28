package net.firiz.renewatelier.server.event;

import net.firiz.renewatelier.utils.java.CObjects;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsyncPlayerInteractEntityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final int entityId;
    private final boolean isRightClick;
    @NotNull
    private final EquipmentSlot hand;
    private boolean cancelled;

    public AsyncPlayerInteractEntityEvent(@NotNull Player player, int entityId, boolean isRightClick, @NotNull EquipmentSlot hand) {
        super(player, true);
        this.entityId = entityId;
        this.isRightClick = isRightClick;
        this.hand = hand;
    }

    public int getEntityId() {
        return entityId;
    }

    public boolean isRightClick() {
        return isRightClick;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    @Nullable
    public Entity getEntity() {
        return CObjects.nullIfFunction(
                ((CraftWorld) player.getWorld()).getHandle().getEntity(entityId),
                net.minecraft.world.entity.Entity::getBukkitEntity,
                null
        );
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
