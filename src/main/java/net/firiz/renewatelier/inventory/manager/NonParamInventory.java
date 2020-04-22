package net.firiz.renewatelier.inventory.manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NonParamInventory extends CustomInventory {

    void open(@NotNull Player player);

}
