package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtil;
import net.firiz.renewatelier.utils.doubledata.DoubleData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    private void entityDamageByDamage(final EntityDamageByEntityEvent e) {
        final Entity damager = e.getDamager();
        if (e.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
            if (damager instanceof Player) {
                DamageUtil.holoDamage(
                        e.getEntity().getLocation(),
                        new DoubleData<>(e.getDamage() * 2, AttackAttribute.BLOW),
                        new DoubleData<>(e.getDamage(), AttackAttribute.FIRE),
                        new DoubleData<>(e.getDamage() * 0.2, AttackAttribute.FIRE),
                        new DoubleData<>(e.getDamage() * 0.2, AttackAttribute.LIGHTNING)
                );
            } else if (damager instanceof Projectile) {
                final Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                    final String customData = projectile.getCustomName();
                }
            }
        }
    }

}
