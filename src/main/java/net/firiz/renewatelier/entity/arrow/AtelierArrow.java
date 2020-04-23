package net.firiz.renewatelier.entity.arrow;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface AtelierArrow {

    LivingEntity getSource();
    ItemStack getBow();
    ItemStack getArrow();
    boolean isCritical();
    float getForce();

    @Nullable
    default Location getOrigin() {
        return null;
    }

}
