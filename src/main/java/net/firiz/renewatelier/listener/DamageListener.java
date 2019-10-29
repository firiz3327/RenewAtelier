package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtil;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.version.entity.projectile.AtelierProjectile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class DamageListener implements Listener {

    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;

    @EventHandler
    private void damage(final EntityDamageEvent e) {
        if (e.getDamage() == 0) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            DamageUtil.damagePlayer(
                    psm.getChar(e.getEntity().getUniqueId()).getCharStats(),
                    e.getDamage(),
                    e.getCause()
            );
            e.setDamage(0);
        }
    }

    @EventHandler
    private void entityDamageByDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamage() == 0) {
            return;
        }
        final Entity damager = e.getDamager();
        if (e.getCause() != EntityDamageEvent.DamageCause.CUSTOM && e.getEntity() instanceof LivingEntity) {
            if (damager instanceof Player) {
                DamageUtil.normalAttack(AttackAttribute.BLOW, PlayerSaveManager.INSTANCE.getChar(damager.getUniqueId()).getCharStats(), (LivingEntity) e.getEntity(), e.getDamage());
            } else if (damager instanceof AtelierProjectile) {
                final AtelierProjectile projectile = (AtelierProjectile) damager;
                if (projectile.getSource() != null && projectile.getSource() instanceof Player) {
                    final ItemStack bow = projectile.getBow();
                    if (bow != null) {

                    }
                }
            }
        }
    }

}
