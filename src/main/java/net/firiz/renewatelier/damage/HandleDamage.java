package net.firiz.renewatelier.damage;

import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.arrow.AtelierArrow;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.npc.NPCObject;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.NMSEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public final class HandleDamage {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private static final DamageUtilV2 damageUtilV2 = DamageUtilV2.INSTANCE;

    public void playerAttack(LivingEntity victim, Entity damager) {
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
                sweepAttack(charStats, victim.getLocation(), vanillaItemDamage);
                return;
            }
            damageUtilV2.normalDamage(
                    weaponType == Material.TRIDENT ? AttackAttribute.THRUST : AttackAttribute.BLOW,
                    charStats,
                    victim,
                    vanillaItemDamage,
                    1
            );
            if (!NMSEntityUtils.isDead(victim)) {
                NMSEntityUtils.hurt(victim, player, null);
                percentKnockBack(victim, player);
            }
        }
    }

    private void sweepAttack(CharStats charStats, Location location, double vanillaItemDamage) {
        final Player player = charStats.getPlayer();
        NMSEntityUtils.sweepParticle(player);
        location.getNearbyLivingEntities(1.5).stream()
                .filter(entity -> !(entity instanceof Player) && !NPCObject.hasEntity(entity))
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
                        percentKnockBack(entity, player);
                    }
                });
    }

    public void arrowAttack(LivingEntity victim, Entity damager, double damage) {
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
                victim,
                damage,
                force == 1 && arrow.isCritical(),
                force
        );
        if (!NMSEntityUtils.isDead(victim)) {
            NMSEntityUtils.hurt(victim, damager, null);
            NMSEntityUtils.knockBackArrow(victim, damager);
        }
    }

    public void playerHeal(Player player, double amount) {
        psm.getChar(player.getUniqueId()).getCharStats().heal(amount);
    }

    public void environmentalPlayerDamage(EntityDamageEvent e) {
        environmentalFilter(e.getCause(), () -> {
            e.setCancelled(true);
            damageUtilV2.handlePlayerEnvironmentalDamage(psm.getChar(e.getEntity().getUniqueId()).getCharStats(), e.getDamage());
        });
    }

    public void environmentalDamage(EntityDamageEvent e) {
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

    public void victimPlayer(LivingEntity victim, Entity damager, double damage) {
        damageUtilV2.handlePlayerDamage(
                psm.getChar(victim.getUniqueId()).getCharStats(),
                damager,
                damage
        );
        if (!NMSEntityUtils.isDead(victim)) {
            NMSEntityUtils.hurt(victim, damager, null);
            NMSEntityUtils.knockBack(victim, damager);
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

    private void percentKnockBack(LivingEntity victim, Entity damager) {
        if (Randomizer.percent(30)) {
            NMSEntityUtils.knockBack(victim, damager);
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
