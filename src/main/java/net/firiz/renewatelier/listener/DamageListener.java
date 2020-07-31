package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.damage.HandleDamage;
import net.firiz.renewatelier.entity.arrow.AtelierAbstractArrow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class DamageListener implements Listener {

    private final HandleDamage handleDamage = new HandleDamage();

    @EventHandler
    private void regainHealth(final EntityRegainHealthEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof Player) {
            e.setCancelled(true);
            handleDamage.playerHeal((Player) entity, e.getAmount());
        } else if (entity instanceof EnderDragon && e.getRegainReason() == EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL) {
            // enderdragon crystal heal
        }
    }

    @EventHandler
    private void damage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            handleDamage.environmentalPlayerDamage(e);
        } else if (e.getEntity() instanceof LivingEntity) {
            handleDamage.environmentalDamage(e);
        }
    }

    @EventHandler
    private void entityDamageByDamage(final EntityDamageByEntityEvent e) {
        final double damage = e.getDamage();
        if (damage == 0) {
            e.setCancelled(true);
            return;
        }
        final Entity damager = e.getDamager();
        if (e.getCause() != EntityDamageEvent.DamageCause.CUSTOM && e.getEntity() instanceof LivingEntity) {
            final LivingEntity victim = (LivingEntity) e.getEntity();
            if (damager instanceof Player) {
                e.setCancelled(true);
                handleDamage.playerAttack(victim, damager);
            } else if (damager instanceof AtelierAbstractArrow) {
                e.setCancelled(true);
                handleDamage.arrowAttack(victim, damager, damage);
            } else if (victim instanceof Player) {
                e.setCancelled(true);
                handleDamage.victimPlayer(victim, damager, damage);
            }
        }
    }

}
