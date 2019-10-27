package net.firiz.renewatelier.version.entity.projectile;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface AtelierProjectile {

    void shoot(Vector velocity);

    LivingEntity getSource();

    ItemStack getBow();

    ItemStack getItem();

    void setVelocity(Vector velocity);

}
