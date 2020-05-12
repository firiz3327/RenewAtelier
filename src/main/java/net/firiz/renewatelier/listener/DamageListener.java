package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.arrow.AtelierArrow;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public class DamageListener implements Listener {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final DamageUtilV2 damageUtilV2 = new DamageUtilV2();

    @EventHandler
    private void regainHealth(final EntityRegainHealthEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof Player) {
            e.setCancelled(true);
            psm.getChar(entity.getUniqueId()).getCharStats().heal(e.getAmount());
        } else if (entity instanceof EnderDragon && e.getRegainReason() == EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL) {
            // enderdragon crystal heal
        }
    }

    @EventHandler
    private void damage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            causeFilter(e.getCause(), () -> {
                e.setCancelled(true);
                damageUtilV2.playerDamage(psm.getChar(e.getEntity().getUniqueId()).getCharStats(), e.getDamage());
            });
        } else if (e.getEntity() instanceof LivingEntity) {
            causeFilter(e.getCause(), () -> {
                damageUtilV2.mobDamage((LivingEntity) e.getEntity(), e.getDamage());
                if (Randomizer.percent(30)) {
                    e.setDamage(0);
                } else {
                    e.getEntity().playEffect(EntityEffect.HURT);
                    e.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    private void entityDamageByDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamage() == 0) {
            return;
        }
        final Entity damager = e.getDamager();
        if (e.getCause() != EntityDamageEvent.DamageCause.CUSTOM && e.getEntity() instanceof LivingEntity) {
            final LivingEntity victim = (LivingEntity) e.getEntity();
            if (damager instanceof Player) {
                if (damager.isOp() && ((Player) damager).isSneaking() && ((Player) damager).getInventory().getItemInMainHand().getType() == Material.AIR) {
                    debugStats(damager, victim);
                    e.setCancelled(true);
                    return;
                }
                final ItemStack weapon = ((Player) damager).getInventory().getItemInMainHand();
                double weaponDamage = GameConstants.getVanillaItemDamage(weapon.getType());
                if (GameConstants.isAxe(weapon.getType())) {
                    weaponDamage = Randomizer.rand((int) weaponDamage - 4, (int) weaponDamage); // 斧のダメージは整数なので問題ない
                }
                final double allDamage = damageUtilV2.normalDamage(
                        GameConstants.isSword(weapon.getType()) ? AttackAttribute.SLASH : AttackAttribute.BLOW,
                        psm.getChar(damager.getUniqueId()).getCharStats(),
                        (LivingEntity) e.getEntity(),
                        weaponDamage
                );
                if (allDamage == 0) {
                    e.setCancelled(true);
                } else if (Randomizer.percent(30)) {
                    e.setDamage(0);
                } else {
                    victim.playEffect(EntityEffect.HURT);
                    e.setCancelled(true);
                }
            } else if (damager instanceof AtelierArrow) {
                final AtelierArrow arrow = (AtelierArrow) damager;
                EntityStatus status = null;
                float force = arrow.getForce();
                if (arrow.getSource() instanceof Player) {
                    status = psm.getChar(arrow.getSource().getUniqueId()).getCharStats();
                } else if (aEntityUtils.hasLivingData(arrow.getSource())) {
                    status = aEntityUtils.getLivingData(arrow.getSource()).getStats();
                    force = 1f;
                }
                final double allDamage = damageUtilV2.arrowDamage(
                        AlchemyItemStatus.load(arrow.getBow()),
                        AlchemyItemStatus.load(arrow.getArrow()),
                        status,
                        (LivingEntity) e.getEntity(),
                        e.getDamage(),
                        force >= 1 && arrow.isCritical(),
                        force
                );
                if (allDamage == 0) {
                    e.setCancelled(true);
                } else {
                    e.setDamage(0);
                }
            } else if (victim instanceof Player) {
                final double allDamage = damageUtilV2.playerDamage(
                        psm.getChar(e.getEntity().getUniqueId()).getCharStats(),
                        e.getDamager(),
                        e.getDamage()
                );
                if (allDamage == 0) {
                    e.setCancelled(true);
                } else {
                    e.setDamage(0);
                }
            }
        }
    }

    private void debugStats(Entity damager, LivingEntity victim) {
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
    }

    private void causeFilter(EntityDamageEvent.DamageCause cause, Runnable runnable) {
        switch (cause) {
            case CONTACT:
            case BLOCK_EXPLOSION:
            case CRAMMING:
            case DRAGON_BREATH:
            case DROWNING:
            case FALL:
            case FALLING_BLOCK:
            case FIRE:
            case FIRE_TICK:
            case FLY_INTO_WALL:
            case HOT_FLOOR:
            case LAVA:
            case LIGHTNING:
            case MAGIC:
            case POISON:
            case STARVATION:
            case SUFFOCATION:
            case SUICIDE:
            case THORNS:
            case VOID:
            case WITHER:
                runnable.run();
                break;
            default:
                // 想定しない
                break;
        }
    }

}
