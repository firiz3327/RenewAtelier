package net.firiz.renewatelier.version.entity.projectile.arrow;

import net.firiz.renewatelier.entity.arrow.AtelierAbstractArrow;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public interface INMSAtelierArrow extends INMSAtelierProjectile {

    ItemStack getBow();

    ItemStack getArrow();

    AtelierAbstractArrow getAtelierArrowEntity();

}
