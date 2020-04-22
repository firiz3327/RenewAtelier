package net.firiz.renewatelier.inventory.manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ParamInventory<T> extends CustomInventory {

    void open(@NotNull Player player, @NotNull T param);

}
