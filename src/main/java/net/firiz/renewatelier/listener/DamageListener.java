package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.entity.arrow.AtelierArrow;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

public class DamageListener implements Listener {

    private final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final ArrowManager arrowManager = new ArrowManager();
    private final DamageUtilV2 damageUtilV2 = new DamageUtilV2();

    @EventHandler
    private void damage(final EntityDamageEvent e) {
        if (e.getDamage() == 0) {
            return;
        }
        if (e.getEntity() instanceof Player) {
//            DamageUtil.damagePlayer(
//                    psm.getChar(e.getEntity().getUniqueId()).getCharStats(),
//                    e.getDamage(),
//                    e.getCause()
//            );
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
                if (damager.isOp() && ((Player) damager).isSneaking() && ((Player) damager).getInventory().getItemInMainHand().getType() == Material.AIR) {
                    final LivingEntity victim = (LivingEntity) e.getEntity();
                    final boolean hasLivingData = AtelierEntityUtils.INSTANCE.hasLivingData(victim);
                    damager.sendMessage("hasLivingData: " + hasLivingData);
                    if (hasLivingData) {
                        final LivingData livingData = AtelierEntityUtils.INSTANCE.getLivingData(victim);
                        final boolean hasStats = livingData.hasStats();
                        damager.sendMessage("hasStats: " + hasStats);
                        if (hasStats) {
                            final MonsterStats stats = livingData.getStats();
                            damager.sendMessage(victim.getCustomName());
                            damager.sendMessage("LV: " + stats.getLevel());
                            damager.sendMessage("HP: " + stats.getHp() + " / " + stats.getMaxHp());
                            damager.sendMessage("ATK: " + stats.getAtk() + " DEF: " + stats.getDef() + " SPD: " + stats.getSpeed());
                            damager.sendMessage("Buffs: " + stats.getBuffs());
                        }
                    }
                    e.setCancelled(true);
                    return;
                }
                final ItemStack weapon = ((Player) damager).getInventory().getItemInMainHand();
                damageUtilV2.normalDamage(
                        GameConstants.isSword(weapon.getType()) ? AttackAttribute.SLASH : AttackAttribute.BLOW,
                        PlayerSaveManager.INSTANCE.getChar(damager.getUniqueId()).getCharStats(),
                        (LivingEntity) e.getEntity(),
                        e.getDamage()
                );
                e.setDamage(0);
            } else if (damager instanceof AtelierArrow) {
                final AtelierArrow arrow = (AtelierArrow) damager;
                if (arrow.getSource() instanceof Player) {
                    damageUtilV2.arrowDamage(
                            AlchemyItemStatus.load(arrow.getBow()),
                            AlchemyItemStatus.load(arrow.getArrow()),
                            PlayerSaveManager.INSTANCE.getChar(arrow.getSource().getUniqueId()).getCharStats(),
                            (LivingEntity) e.getEntity(),
                            e.getDamage(),
                            arrow.isCritical(),
                            arrow.getForce()
                    );
                    e.setDamage(0);
                }
            }
        }
    }

    @EventHandler
    private void shootBow(final EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player && (e.getProjectile() instanceof AbstractArrow)) {
            final ItemStack bow = e.getBow();
            if (bow != null && bow.getType() == Material.CROSSBOW) {
                final CrossbowMeta itemMeta = (CrossbowMeta) bow.getItemMeta();
                assert itemMeta != null;
                arrowManager.shootCrossbow((Player) e.getEntity(), bow, (AbstractArrow) e.getProjectile(), itemMeta.getChargedProjectiles().get(0));
            } else {
                arrowManager.shootBow((Player) e.getEntity(), bow, (AbstractArrow) e.getProjectile(), e.getForce());
            }
        }
    }

}
