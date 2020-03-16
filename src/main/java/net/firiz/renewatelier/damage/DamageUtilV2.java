package net.firiz.renewatelier.damage;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageUtilV2 {

    private final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private final HoloDamage holo = new HoloDamage();
    private final MeruruCalcDamage meruruCalcDamage = new MeruruCalcDamage();

    public void normalDamage(AttackAttribute attribute, CharStats charStats, LivingEntity victim, double normalDamage) {
        final AlchemyItemStatus weaponStats = charStats.getWeapon();
        final Player player = charStats.getPlayer();
        final boolean isMinecraftCritical = !player.isOnGround() && (player.getLocation().getY() % 1 != 0 || player.getVelocity().getY() < -0.0784); // クリティカルハック及びフライハック対策

        double normalAttackPower = 100;

        if (weaponStats != null && weaponStats.getCategories().contains(Category.WEAPON)) {

            final AlchemyMaterial alchemyMaterial = weaponStats.getAlchemyMaterial();
            final double baseDamage = Math.max(0D, Randomizer.rand(alchemyMaterial.getBaseDamageMin(), alchemyMaterial.getBaseDamageMax()));
            if (baseDamage == 0) {
                return;
            }

            calcDamage(weaponStats, charStats, victim, normalAttackPower, baseDamage, 1, attribute, isMinecraftCritical, false);
        } else {
            calcDamage(weaponStats, charStats, victim, normalAttackPower, normalDamage, 1, attribute, isMinecraftCritical, false);
        }
    }

    public void arrowDamage(@Nullable AlchemyItemStatus bow, @Nullable AlchemyItemStatus arrow, CharStats charStats, LivingEntity victim, double damage, boolean isMinecraftCritical, float force) {
        double normalAttackPower = Randomizer.rand(63, 70);
        double baseDamage = damage;
        if (bow != null) {
            final AlchemyMaterial bowAlchemyMaterial = bow.getAlchemyMaterial();
            baseDamage = Math.max(0D, Randomizer.rand(bowAlchemyMaterial.getBaseDamageMin(), bowAlchemyMaterial.getBaseDamageMax()));
        }
        if (arrow != null) {
            final AlchemyMaterial arrowAlchemyMaterial = arrow.getAlchemyMaterial();
            baseDamage += Math.max(0D, Randomizer.rand(arrowAlchemyMaterial.getBaseDamageMin(), arrowAlchemyMaterial.getBaseDamageMax())) * calcQualityCorrection(arrow.getQuality());
        }
        calcDamage(bow, charStats, victim, normalAttackPower, baseDamage, force, AttackAttribute.PHYSICS, isMinecraftCritical, true);
    }

    private void calcDamage(@Nullable AlchemyItemStatus itemStatus, CharStats charStats, LivingEntity victim, double power, double baseDamage, double force, AttackAttribute baseAttribute, boolean isMinecraftCritical, boolean arrow) {
        final boolean hasItemStatus = itemStatus != null;

        int criticalRate = 0;

        final List<String[]> addAttacks = new ArrayList<>(); // <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
        int characteristicLevel = 0;
        int characteristicCPower = 0;
        double characteristicPowers = 0;
        if (hasItemStatus) {
            for (final Characteristic c : itemStatus.getCharacteristics()) {
                characteristicLevel += c.getLevel();
                characteristicPowers += c.hasData(CharacteristicType.POWER) ? (int) c.getData(CharacteristicType.POWER) : 0;
                characteristicCPower += c.hasData(CharacteristicType.CHARACTERISTIC_POWER) ? (int) c.getData(CharacteristicType.CHARACTERISTIC_POWER) : 0;
                criticalRate = calcCriticalRate(victim, criticalRate, c);
                final Object addAttack = c.getData(CharacteristicType.ADD_ATTACK);
                if (addAttack != null) {
                    final String[] data = (String[]) addAttack;
                    final int percent = Integer.parseInt(data[1]);
                    final int attackType = Integer.parseInt(data[2]);
                    if ((attackType == -1 || attackType == 0 || attackType == 2 || attackType == 3) && (percent >= 100 || Randomizer.percent(percent))) {
                        addAttacks.add(data);
                    }
                }
            }
        }
        characteristicPowers += characteristicCPower == 0 ? 0D : Math.pow(characteristicLevel, 0.7) + characteristicCPower;

        // 威力値
        double powerValue = (power * 0.2) + (power * 0.8 + baseDamage / 2) * force; // forceが0でも最低威力値２割が保証される
        if (hasItemStatus) {
            powerValue *= calcQualityCorrection(itemStatus.getQuality()); // 品質による威力値上昇
        }
        powerValue *= characteristicPowers == 0 ? 1D : ((100D + characteristicPowers) * 0.01); // 特性による威力値上昇
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
                criticalRate = calcCriticalRate(victim, criticalRate, c);
            }
        }

        double victimDef = 0;
        double victimPhysicsDef = 0;
        if (aEntityUtils.hasLivingData(victim)) {
            final LivingData livingData = aEntityUtils.getLivingData(victim);
            if (livingData.hasStats()) {
                final MonsterStats stats = livingData.getStats();
                victimDef = stats.getDef();
//            victimPhysicsDef = 0; 敵の物理防御率という概念がまだ実装されてないっす
            }
        }

        final Player player = charStats.getPlayer();
        final double damage = meruruCalcDamage.calcPhysicsDamage(
                charStats.getAtk(),
                powerValue,
                victimDef,
                victimPhysicsDef,
                150,
                isMinecraftCritical || criticalRate >= 100 || Randomizer.percent(criticalRate)
        );
        final FinalDoubleData<Double, AttackAttribute> baseDamageComponent = new FinalDoubleData<>(damage, baseAttribute);
        final List<FinalDoubleData<Double, AttackAttribute>> damageComponents = new ArrayList<>();
        damageComponents.add(baseDamageComponent);
        for (final String[] addAttack : addAttacks) {
            final FinalDoubleData<Double, AttackAttribute> damageComponent = AddAttackType.valueOf(addAttack[0]).runDamage(addAttack, charStats, victim, baseDamageComponent);
            damageComponents.add(damageComponent);
        }
        holo.holoDamage(victim, player, damageComponents);
    }

    private int calcCriticalRate(LivingEntity victim, int criticalRate, Characteristic c) {
        criticalRate += c.hasData(CharacteristicType.CRITICAL) ? (int) c.getData(CharacteristicType.CRITICAL) : 0;
        if (c.hasData(CharacteristicType.CRITICAL_RACE)) {
            final String[] data = (String[]) c.getData(CharacteristicType.CRITICAL_RACE);
            final Race race = Race.valueOf(Objects.requireNonNull(data)[1]);
            if (race.hasType(victim)) {
                criticalRate += Integer.parseInt(data[0]);
            }
        }
        return criticalRate;
    }

    private double calcQualityCorrection(final int quality) {
        return quality <= 100 ? 0.75 + quality / 200D : 1.25 + Math.sqrt((quality - 100) * 10D) / 100;
    }
}
