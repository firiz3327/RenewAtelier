package net.firiz.renewatelier.version.entity.projectile.arrow;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface INMSAtelierProjectile {

    void shoot(Vector velocity);

    LivingEntity getSource();

    ItemStack getItem();

}
