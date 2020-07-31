package net.firiz.renewatelier.entity.arrow;

import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AtelierAbstractArrow extends AbstractArrow {

    LivingEntity getSource();
    ItemStack getBow();
    @NotNull
    ItemStack getArrow();
    boolean isCritical();
    float getForce();
    boolean isSkill();

    @Nullable
    default Location getOrigin() {
        return null;
    }

}
