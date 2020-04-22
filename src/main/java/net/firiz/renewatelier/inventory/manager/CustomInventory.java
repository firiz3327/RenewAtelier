package net.firiz.renewatelier.inventory.manager;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public interface CustomInventory {

    boolean check(@NotNull InventoryView view);

    default void onClick(@NotNull InventoryClickEvent event) {}

    default void onDrag(@NotNull InventoryDragEvent event) {}

    default void onClose(@NotNull InventoryCloseEvent event) {}

}
