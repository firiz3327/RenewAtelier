package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.arrow.AtelierArrow;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.NMSEntityUtils;
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

public class DamageListener implements Listener {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private static final DamageUtilV2 damageUtilV2 = DamageUtilV2.INSTANCE;

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
            environmentalFilter(e.getCause(), () -> {
                e.setCancelled(true);
                damageUtilV2.handlePlayerEnvironmentalDamage(psm.getChar(e.getEntity().getUniqueId()).getCharStats(), e.getDamage());
            });
        } else if (e.getEntity() instanceof LivingEntity) {
            environmentalFilter(e.getCause(), () -> {
                damageUtilV2.mobEnvironmentalDamage((LivingEntity) e.getEntity(), e.getDamage());
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
        final double damage = e.getDamage();
        if (damage == 0) {
            return;
        }
        final Entity damager = e.getDamager();
        if (e.getCause() != EntityDamageEvent.DamageCause.CUSTOM && e.getEntity() instanceof LivingEntity) {
            final LivingEntity victim = (LivingEntity) e.getEntity();
            if (damager instanceof Player) {
                e.setCancelled(true);
                final Player player = (Player) damager;
                if (player.isOp() && player.isSneaking() && player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    debugStats(player, victim);
                    return;
                }
                final CharStats charStats = psm.getChar(player.getUniqueId()).getCharStats();
                final Material weaponType = player.getInventory().getItemInMainHand().getType();
                if (charStats.attack(weaponType)) {
                    final double vanillaItemDamage = GameConstants.getVanillaItemDamage(weaponType);
                    if (GameConstants.isSword(weaponType)) {
                        player.setCooldown(weaponType, 20);
                        e.setCancelled(true);
                        NMSEntityUtils.sweepParticle(player);
                        victim.getLocation().getNearbyLivingEntities(1.5).stream()
                                .filter(entity -> !(entity instanceof Player))
                                .limit(6) // 6 mob hit
                                .forEach(entity -> {
                                    damageUtilV2.normalDamage(
                                            AttackAttribute.SLASH,
                                            charStats,
                                            entity,
                                            vanillaItemDamage,
                                            1
                                    );
                                    if (!NMSEntityUtils.isDead(entity)) {
                                        NMSEntityUtils.hurt(entity, player, null);
                                        if (Randomizer.percent(30)) {
                                            NMSEntityUtils.knockBack(entity, player);
                                        }
                                    }
                                });
                    } else {
                        damageUtilV2.normalDamage(
                                AttackAttribute.BLOW,
                                charStats,
                                (LivingEntity) e.getEntity(),
                                vanillaItemDamage,
                                1
                        );
                        if (!NMSEntityUtils.isDead(victim)) {
                            NMSEntityUtils.hurt(victim, player, null);
                            if (Randomizer.percent(30)) {
                                NMSEntityUtils.knockBack((LivingEntity) e.getEntity(), player);
                            }
                        }
                    }
                }
            } else if (damager instanceof AtelierArrow) {
                e.setCancelled(true);
                final AtelierArrow arrow = (AtelierArrow) damager;
                EntityStatus status = null;
                float force = arrow.getForce();
                if (arrow.getSource() instanceof Player) {
                    status = psm.getChar(arrow.getSource().getUniqueId()).getCharStats();
                } else if (aEntityUtils.hasLivingData(arrow.getSource())) {
                    status = aEntityUtils.getLivingData(arrow.getSource()).getStats();
                    force = 1f; // プレイヤー以外が放つ矢のforce値はデフォルトだと0.7 (1.15で確認)
                }
                damageUtilV2.arrowDamage(
                        damager,
                        AlchemyItemStatus.load(arrow.getBow()),
                        AlchemyItemStatus.load(arrow.getArrow()),
                        status,
                        (LivingEntity) e.getEntity(),
                        damage,
                        force == 1 && arrow.isCritical(),
                        force
                );
                if (!NMSEntityUtils.isDead(victim)) {
                    NMSEntityUtils.hurt(victim, e.getDamager(), null);
                    NMSEntityUtils.knockBack((LivingEntity) e.getEntity(), e.getDamager());
                }
            } else if (victim instanceof Player) {
                e.setCancelled(true);
                damageUtilV2.handlePlayerDamage(
                        psm.getChar(e.getEntity().getUniqueId()).getCharStats(),
                        e.getDamager(),
                        damage
                );
                if (!NMSEntityUtils.isDead(victim)) {
                    NMSEntityUtils.hurt(victim, e.getDamager(), null);
                    NMSEntityUtils.knockBack((LivingEntity) e.getEntity(), e.getDamager());
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

    private void environmentalFilter(EntityDamageEvent.DamageCause cause, Runnable runnable) {
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
