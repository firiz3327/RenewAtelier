package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtil;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.entity.arrow.AtelierArrow;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class DamageListener implements Listener {

    private final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final ArrowManager arrowManager = new ArrowManager();

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
                final ItemStack weapon = ((Player) damager).getInventory().getItemInMainHand();
                DamageUtil.normalDamage(
                        GameConstants.isSword(weapon.getType()) ? AttackAttribute.SLASH : AttackAttribute.BLOW,
                        PlayerSaveManager.INSTANCE.getChar(damager.getUniqueId()).getCharStats(),
                        (LivingEntity) e.getEntity(),
                        e.getDamage()
                );
                e.setDamage(0);
            } else if (damager instanceof AtelierArrow) {
                final AtelierArrow arrow = (AtelierArrow) damager;
                if (arrow.getSource() instanceof Player) {
                    DamageUtil.arrowDamage(
                            Objects.requireNonNull(AlchemyItemStatus.load(arrow.getBow())),
                            AlchemyItemStatus.load(arrow.getArrow()),
                            PlayerSaveManager.INSTANCE.getChar(arrow.getSource().getUniqueId()).getCharStats(),
                            (LivingEntity) e.getEntity(),
                            e.getDamage(),
                            arrow.isCritical(),
                            arrow.getForce()
                    );
                    e.setDamage(0);
                }
            } else if (damager instanceof Projectile) {
                final Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                    DamageUtil.arrowNormalDamage(
                            (LivingEntity) e.getEntity(),
                            (Player) projectile.getShooter(),
                            e.getDamage()
                    );
                    e.setDamage(0);
                }
            }
        }
    }

    @EventHandler
    private void shootBow(final EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player && (e.getProjectile() instanceof AbstractArrow)) {
            arrowManager.shootBow((Player) e.getEntity(), e.getBow(), (AbstractArrow) e.getProjectile(), e.getForce());
        }
    }

}
