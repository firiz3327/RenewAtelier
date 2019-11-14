package net.firiz.renewatelier.version.entity.projectile.arrow;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public interface IAtelierArrow extends IAtelierProjectile {

    ItemStack getBow();

    ItemStack getArrow();

    void setShooter(ProjectileSource shooter);

    void setPickupStatus(AbstractArrow.PickupStatus pickupStatus);

    void setFireTicks(int fireTicks);

    void setPierceLevel(int pierceLevel);

    void setKnockbackStrength(int knockbackStrength);

    void setDamage(double damage);

    void setCritical(boolean critical);

}
