package net.firiz.renewatelier.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated Use {@link com.destroystokyo.paper.event.player.PlayerArmorChangeEvent}, Duplicate API
 */
@Deprecated(forRemoval = true)
public class PlayerArmorChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final ItemStack itemStack;
    private final ChangeType changeType;

    public PlayerArmorChangeEvent(@NotNull Player player, @Nullable ItemStack itemStack, @NotNull ChangeType changeType) {
        this.player = player;
        this.itemStack = itemStack;
        this.changeType = changeType;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    @NotNull
    public ChangeType getChangeType() {
        return changeType;
    }

    public enum ChangeType {
        ADD,
        SET,
        REMOVE,
        CLEAR
    }

}
