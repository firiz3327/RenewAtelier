package net.firiz.renewatelier.entity.arrow;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public interface AtelierArrow {

    LivingEntity getSource();
    ItemStack getBow();
    ItemStack getArrow();

}
