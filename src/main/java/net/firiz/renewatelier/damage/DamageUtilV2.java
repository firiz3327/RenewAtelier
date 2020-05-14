package net.firiz.renewatelier.damage;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.characteristic.datas.CharacteristicBuff;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackData;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DamageUtilV2 {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final HoloDamage holo = new HoloDamage();
    private final MeruruCalcDamage meruruCalcDamage = new MeruruCalcDamage();

    public void playerDamage(@NotNull final CharStats charStats, double damage) {
        final DamageComponent baseDamageComponent = new DamageComponent(charStats.getMaxHp() * (damage / 20), AttackAttribute.PHYSICS);
        final List<DamageComponent> damageComponents = new ArrayList<>();
        damageComponents.add(baseDamageComponent);
        holo.holoDamage(charStats.getPlayer(), null, damageComponents);
    }

    public double playerDamage(@NotNull final CharStats charStats, @NotNull Entity damager, double damage) {
        net.firiz.renewatelier.entity.EntityStatus entityStatus = null;
        if (damager instanceof Player) {
            entityStatus = psm.getChar(damager.getUniqueId()).getCharStats();
        } else if (damager instanceof LivingEntity && aEntityUtils.hasLivingData(damager)) {
            entityStatus = aEntityUtils.getLivingData((LivingEntity) damager).getStats();
        }
        return calcDamage(null, entityStatus, damager, charStats.getPlayer(), 100, damage, 1, AttackAttribute.PHYSICS, false, false);
    }

    public void mobDamage(@NotNull final LivingEntity victim, double damage) {
        if (damage > 0) {
            final DamageComponent baseDamageComponent;
            if (aEntityUtils.hasLivingData(victim) && aEntityUtils.getLivingData(victim).hasStats()) {
                baseDamageComponent = new DamageComponent(
                        Math.max(1, aEntityUtils.getLivingData(victim).getStats().getMaxHp() * (damage / 100)),
                        AttackAttribute.NONE
                );
            } else {
                baseDamageComponent = new DamageComponent(damage, AttackAttribute.NONE);
            }
            victim.playEffect(EntityEffect.HURT);
            final List<DamageComponent> damageComponents = new ArrayList<>();
            damageComponents.add(baseDamageComponent);
            holo.holoDamage(victim, null, damageComponents);
        }
    }

    public double normalDamage(AttackAttribute attribute, CharStats charStats, LivingEntity victim, double normalDamage) {
        final AlchemyItemStatus weaponStats = charStats.getWeapon();
        final Player player = charStats.getPlayer();
        final boolean isMinecraftCritical = !player.isOnGround() && (player.getLocation().getY() % 1 != 0 || player.getVelocity().getY() < -0.0784); // クリティカルハック及びフライハック対策

        double normalAttackPower = 100;

        if (weaponStats != null && weaponStats.getCategories().contains(Category.WEAPON)) {
            final AlchemyMaterial alchemyMaterial = weaponStats.getAlchemyMaterial();
            final double baseDamage = Math.max(0D, Randomizer.rand(alchemyMaterial.getBaseDamageMin(), alchemyMaterial.getBaseDamageMax()));
            if (baseDamage == 0) {
                return 0;
            }
            return calcDamage(weaponStats, charStats, player, victim, normalAttackPower, baseDamage, 1, attribute, isMinecraftCritical, false);
        } else {
            return calcDamage(weaponStats, charStats, player, victim, normalAttackPower, normalDamage * 1.5, 1, attribute, isMinecraftCritical, false);
        }
    }

    public double arrowDamage(@Nullable AlchemyItemStatus bow, @Nullable AlchemyItemStatus arrow, @Nullable EntityStatus status, @NotNull LivingEntity victim, double damage, boolean isMinecraftCritical, float force) {
        double normalAttackPower = Randomizer.rand(63, 70);
        double baseDamage = damage;
        if (bow == null && arrow == null) {
            baseDamage *= 1.2;
        } else {
            if (bow != null) {
                final AlchemyMaterial bowAlchemyMaterial = bow.getAlchemyMaterial();
                baseDamage = Math.max(0D, Randomizer.rand(bowAlchemyMaterial.getBaseDamageMin(), bowAlchemyMaterial.getBaseDamageMax()));
            }
            if (arrow != null) {
                final AlchemyMaterial arrowAlchemyMaterial = arrow.getAlchemyMaterial();
                baseDamage += Math.max(0D, Randomizer.rand(arrowAlchemyMaterial.getBaseDamageMin(), arrowAlchemyMaterial.getBaseDamageMax())) * calcQualityCorrection(arrow.getQuality());
            }
        }
        return calcDamage(bow, status, status == null ? null : status.getEntity(), victim, normalAttackPower, baseDamage, force, AttackAttribute.PHYSICS, isMinecraftCritical, true);
    }

    private double calcDamage(@Nullable AlchemyItemStatus itemStatus, @Nullable EntityStatus damagerStatus, @Nullable Entity damager, @NotNull LivingEntity victim, double power, double baseDamage, double force, AttackAttribute baseAttribute, boolean isMinecraftCritical, boolean arrow) {
        final boolean hasItemStatus = itemStatus != null;

        int criticalRate = 0;

        final List<Buff> buffs = new ArrayList<>();
        final List<AddAttackData> addAttacks = new ArrayList<>();
        int characteristicLevel = 0;
        int characteristicCPower = 0;
        double characteristicPowers = 0;
        if (hasItemStatus) {
            for (final Characteristic c : itemStatus.getCharacteristics()) {
                characteristicLevel += c.getLevel();
                characteristicPowers += c.hasData(CharacteristicType.POWER) ? c.getIntData(CharacteristicType.POWER) : 0;
                characteristicCPower += c.hasData(CharacteristicType.CHARACTERISTIC_POWER) ? c.getIntData(CharacteristicType.CHARACTERISTIC_POWER) : 0;
                criticalRate = calcCriticalRate(victim.getType(), damagerStatus, criticalRate, c);

                final CharacteristicBuff characteristicBuff = (CharacteristicBuff) c.getData(CharacteristicType.BUFF);
                if (characteristicBuff != null) {
                    final int percent = characteristicBuff.getPercent();
                    if (percent >= 100 || Randomizer.percent(percent)) {
                        buffs.add(new Buff(
                                BuffValueType.CHARACTERISTIC,
                                characteristicBuff.getX(), // level
                                characteristicBuff.getBuffType(),
                                0,
                                characteristicBuff.getDuration(),
                                characteristicBuff.getX()
                        ));
                    }
                }

                final AddAttackData addAttack = (AddAttackData) c.getData(CharacteristicType.ADD_ATTACK);
                if (addAttack != null) {
                    final int percent = addAttack.getPercent();
                    final AddAttackData.AttackCategory attackCategory = addAttack.getAttackCategory(); // 攻撃カテゴリの制限が設けられてない
                    if (percent >= 100 || Randomizer.percent(percent)) {
                        addAttacks.add(addAttack);
                    }
                }
            }
        }
        characteristicPowers += characteristicCPower == 0 ? 0D : Math.pow(characteristicLevel, 0.7) + characteristicCPower;

        // 威力値
        double powerValue = (power * 0.2) + (power * 0.8 / 2) * force; // forceが0でも最低威力値２割が保証される
        if (hasItemStatus) {
            powerValue *= calcQualityCorrection(itemStatus.getQuality()); // 品質による威力値上昇
        }
        powerValue *= characteristicPowers == 0 ? 1D : ((100D + characteristicPowers) * 0.01); // 特性による威力値上昇
        if (damagerStatus instanceof CharStats) {
            final CharStats charStats = (CharStats) damagerStatus;
            switch (charStats.getWeaponItem().getType()) {
                case BOW:
                case ARROW:
                case SPECTRAL_ARROW:
                case TIPPED_ARROW:
                    if (!arrow) {
                        powerValue /= 6; // 弓の時、威力値減少
                    }
                    break;
                default:
                    break;
            }

            final List<AlchemyItemStatus> equips = charStats.getEquips();
            for (final AlchemyItemStatus equip : equips) {
                for (final Characteristic c : equip.getCharacteristics()) {
                    criticalRate = calcCriticalRate(victim.getType(), damagerStatus, criticalRate, c);
                }
            }
        }

        double victimDef = 0;
        double victimPhysicsDef = 0;
        final EntityStatus victimStatus = ((Supplier<EntityStatus>) () -> {
            if (aEntityUtils.hasLivingData(victim)) {
                final LivingData livingData = aEntityUtils.getLivingData(victim);
                if (livingData.hasStats()) {
                    return livingData.getStats();
                }
            } else if (victim instanceof Player) {
                return psm.getChar(victim.getUniqueId()).getCharStats();
            }
            return null;
        }).get();
        if (victimStatus != null) {
            victimDef = victimStatus.getDef();
            victimPhysicsDef = 0; // 物理防御率という概念がまだ実装されてないっす
            if (victim instanceof Player && ((Player) victim).isBlocking()) {
                victimDef *= 1.2; // 盾で守っている時は、防御力を20％上昇
            }
            buffs.forEach(victimStatus::addBuff);
        }

        final double damage = meruruCalcDamage.calcPhysicsDamage(
                baseDamage, // original
                damagerStatus == null ? 0 : damagerStatus.getAtk(),
                powerValue,
                victimDef,
                victimPhysicsDef,
                150,
                isMinecraftCritical || criticalRate >= 100 || Randomizer.percent(criticalRate)
        );
        final DamageComponent baseDamageComponent = new DamageComponent(damage, baseAttribute);
        final List<DamageComponent> damageComponents = new ArrayList<>();
        damageComponents.add(baseDamageComponent);
        double allDamage = damage;
        for (final AddAttackData addAttack : addAttacks) {
            final DamageComponent damageComponent = addAttack.getAddAttackType().applyDamage(addAttack, damagerStatus, victim, baseDamageComponent);
            if (damageComponent != null) {
                damageComponents.add(damageComponent);
                allDamage += damageComponent.getDamage();
            }
        }
        holo.holoDamage(victim, damager, damageComponents);
        return allDamage;
    }

    private int calcCriticalRate(@NotNull EntityType type, @Nullable EntityStatus status, int criticalRate, Characteristic c) {
        criticalRate += c.hasData(CharacteristicType.CRITICAL) ? c.getIntData(CharacteristicType.CRITICAL) : 0;
        if (c.hasData(CharacteristicType.CRITICAL_RACE)) {
            final String[] data = c.getArrayData(CharacteristicType.CRITICAL_RACE);
            final Race race = Race.valueOf(Objects.requireNonNull(data)[1]);
            if (status instanceof MonsterStats && ((MonsterStats) status).getRace() == race || race.hasVanillaType(type)) {
                criticalRate += Integer.parseInt(data[0]);
            }
        }
        return criticalRate;
    }

    private double calcQualityCorrection(final int quality) {
        return quality <= 100 ? 0.75 + quality / 200D : 1.25 + Math.sqrt((quality - 100) * 10D) / 100;
    }
}