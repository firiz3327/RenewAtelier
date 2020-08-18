package net.firiz.renewatelier.inventory;

import net.firiz.renewatelier.inventory.manager.ParamInventory;
import net.firiz.renewatelier.skill.character.CharSkillManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public final class SkillInventory implements ParamInventory<CharSkillManager> {

    @Override
    public void open(@NotNull Player player, @NotNull CharSkillManager param) {

    }

    @Override
    public boolean check(@NotNull InventoryView view) {
        return false;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {

    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {

    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {

    }
}
