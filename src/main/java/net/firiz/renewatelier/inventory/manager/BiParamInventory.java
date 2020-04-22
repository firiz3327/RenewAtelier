package net.firiz.renewatelier.inventory.manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BiParamInventory<L, R> extends CustomInventory {

    void open(@NotNull Player player, @NotNull L param1, @NotNull R param2);

}
