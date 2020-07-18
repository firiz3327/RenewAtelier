package net.firiz.renewatelier.entity.arrow;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AtelierArrow {

    LivingEntity getSource();
    ItemStack getBow();
    @NotNull
    ItemStack getArrow();
    boolean isCritical();
    float getForce();

    @Nullable
    default Location getOrigin() {
        return null;
    }

}
