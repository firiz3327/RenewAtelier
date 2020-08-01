package net.firiz.renewatelier.damage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterialCategory;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.characteristic.datas.CharacteristicBuff;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackData;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.entity.arrow.AtelierAbstractArrow;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.item.json.itemeffect.RaisePower;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.chores.CObjects;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.firiz.renewatelier.version.nms.NMSEntityUtils;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public enum DamageUtilV2 {
    INSTANCE;

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final HoloDamage holo = new HoloDamage();
    private final MeruruCalcDamage meruruCalcDamage = new MeruruCalcDamage();

    private final double defaultCriticalMag = 150;

    public void itemDamage(@Nullable final AlchemyItemStatus itemStatus, @NotNull final Player damager, @NotNull final LivingEntity victim, final float force, final double power, @NotNull final AttackAttribute attackAttribute) {
        final CalcDamageData data = calcData(itemStatus, getEntityStatus(damager), victim, power, force, false, AttackCategory.ITEM);
        final ImmutablePair<Double, Double> def = getDef(victim, data.victimStatus, data.buffs, attackAttribute);
        final double damage = meruruCalcDamage.itemDamage(
                data.powerValue,
                def.getRight(),
                defaultCriticalMag,
                data.criticalRate >= 100 || Randomizer.percent(data.criticalRate)
        );
        final EntityStatus damagerStatus = psm.getChar(damager.getUniqueId()).getCharStats();
        final DamageComponent baseDamageComponent = new DamageComponent(damage, attackAttribute);
        final List<DamageComponent> damageComponents = new ObjectArrayList<>();
        damageComponents.add(baseDamageComponent);
        for (final AddAttackData addAttack : data.addAttacks) {
            final DamageComponent damageComponent = addAttack.getAddAttackType().applyDamage(addAttack, damagerStatus, victim, baseDamageComponent);
            if (damageComponent != null) {
                // とりあえず攻撃属性によるパーセント減少のみ
                final double defense = getAddAttackDefense(addAttack, data, victim, damageComponent);
                damageComponent.setDamage(damageComponent.getDamage() * ((100 - defense) / 100));
                damageComponents.add(damageComponent);
            }
        }
        NMSEntityUtils.hurt(victim, damager, null);
        holo.holoDamage(victim, damager, damageComponents);
    }

    private double getAddAttackDefense(@NotNull final AddAttackData addAttack, @NotNull final CalcDamageData data, @NotNull final LivingEntity victim, @NotNull final DamageComponent damageComponent) {
        if (!addAttack.isIgnoreDefense()) {
            final ImmutablePair<Double, Double> def2 = getDef(victim, data.victimStatus, data.buffs, damageComponent.getAttackAttribute());
            return def2.getRight();
        }
        return 0;
    }

    public void abnormalDamage(@NotNull final EntityStatus victimStatus, final double damage) {
        final DamageComponent baseDamageComponent = new DamageComponent(damage, AttackAttribute.ABNORMAL);
        final List<DamageComponent> damageComponents = new ObjectArrayList<>();
        damageComponents.add(baseDamageComponent);
        holo.holoDamage((LivingEntity) victimStatus.getEntity(), null, damageComponents);
    }

    void handlePlayerEnvironmentalDamage(@NotNull final CharStats charStats, final double damage) {
        final DamageComponent baseDamageComponent = new DamageComponent(charStats.getMaxHp() * (damage / 20), AttackAttribute.NONE);
        final List<DamageComponent> damageComponents = new ObjectArrayList<>();
        damageComponents.add(baseDamageComponent);
        holo.holoDamage(charStats.getPlayer(), null, damageComponents);
    }

    void handlePlayerDamage(@NotNull final CharStats charStats, @NotNull final Entity damager, final double damage) {
        playerDamage(charStats, damager, damage, AttackAttribute.NONE);
    }

    void playerDamage(@NotNull final CharStats charStats, @NotNull final Entity damager, final double damage, @NotNull final AttackAttribute attackAttribute) {
        EntityStatus entityStatus = null;
        if (damager instanceof Player) {
            entityStatus = psm.getChar(damager.getUniqueId()).getCharStats();
        } else if (damager instanceof LivingEntity && aEntityUtils.hasLivingData(damager)) {
            entityStatus = aEntityUtils.getLivingData((LivingEntity) damager).getStats();
        }
        calcDamage(
                null,
                entityStatus,
                damager,
                charStats.getPlayer(),
                100,
                damage,
                1,
                attackAttribute,
                false,
                false,
                AttackCategory.NORMAL
        );
    }

    void mobEnvironmentalDamage(@NotNull final LivingEntity victim, final double damage) {
        if (damage > 0) {
            final DamageComponent baseDamageComponent;
            if (aEntityUtils.hasLivingData(victim) && aEntityUtils.getLivingData(victim).hasStats()) {
                final MonsterStats stats = aEntityUtils.getLivingData(victim).getStats();
                assert stats != null;
                baseDamageComponent = new DamageComponent(
                        Math.max(1, stats.getMaxHp() * (damage / 100)),
                        AttackAttribute.NONE
                );
            } else {
                baseDamageComponent = new DamageComponent(damage, AttackAttribute.NONE);
            }
            victim.playEffect(EntityEffect.HURT);
            final List<DamageComponent> damageComponents = new ObjectArrayList<>();
            damageComponents.add(baseDamageComponent);
            holo.holoDamage(victim, null, damageComponents);
        }
    }

    public void normalDamage(@NotNull final AttackAttribute attribute, @NotNull final CharStats charStats, @NotNull final LivingEntity victim, final double normalDamage, final double force) {
        final AlchemyItemStatus weaponStats = charStats.getWeapon();
        final Player player = charStats.getPlayer();
        final boolean isMinecraftCritical = GameConstants.isCritical(player);

        final double normalAttackPower = 100;

        if (weaponStats != null && weaponStats.getCategories().contains(Category.WEAPON)) {
            final AlchemyMaterial alchemyMaterial = weaponStats.getAlchemyMaterial();
            final double baseDamage = Math.max(0D, Randomizer.rand(alchemyMaterial.getBaseDamageMin(), alchemyMaterial.getBaseDamageMax()));
            if (baseDamage == 0) {
                return;
            }
            calcDamage(weaponStats, charStats, player, victim, normalAttackPower, baseDamage, force, attribute, isMinecraftCritical, false, AttackCategory.NORMAL);
        } else {
            calcDamage(weaponStats, charStats, player, victim, normalAttackPower, normalDamage * 1.5, force, attribute, isMinecraftCritical, false, AttackCategory.NORMAL);
        }
    }

    public void arrowDamage(@Nullable final AtelierAbstractArrow arrowEntity, @Nullable final AlchemyItemStatus bow, @Nullable final AlchemyItemStatus arrow, @Nullable final EntityStatus status, @NotNull final LivingEntity victim, final double damage, final boolean isMinecraftCritical, final float force) {
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
        final boolean arrowEntityNonNull = arrowEntity != null;
        final double resultDamage = calcDamage(
                bow,
                status,
                status == null ? null : status.getEntity(),
                victim,
                normalAttackPower,
                baseDamage,
                force,
                AttackAttribute.THRUST,
                isMinecraftCritical,
                true,
                arrowEntityNonNull && arrowEntity.isSkill() ? AttackCategory.SKILL : AttackCategory.NORMAL
        );
        if (arrowEntityNonNull && resultDamage > 0) {
            arrowEntity.remove(); // Pierceエンチャントの効果は考慮していない
        }
    }

    private CalcDamageData calcData(@Nullable final AlchemyItemStatus itemStatus, @Nullable final EntityStatus damagerStatus, @NotNull final LivingEntity victim, final double power, final double force, final boolean arrow, @NotNull final AttackCategory attackCategory) {
        final boolean hasItemStatus = itemStatus != null;
        @Nullable final EntityStatus victimStatus = getEntityStatus(victim);
        final CalcItemData calcItemData = new CalcItemData();
        // 威力値
        double powerValue = (power * 0.2) + (power * 0.8 / 2) * force; // forceが0でも最低威力値２割が保証される
        if (hasItemStatus) {
            powerValue *= calcQualityCorrection(itemStatus.getQuality()); // 品質による威力値上昇
            calcItemData.calc(victim, victimStatus, itemStatus, damagerStatus, attackCategory);
        }
        // 特性による％威力値上昇
        powerValue *= calcItemData.percentPower == 0 ? 1D : ((100D + calcItemData.percentPower) * 0.01);
        // 特性による固定値上昇
        powerValue += calcItemData.fixedPower;
        // 効果による上昇 (％ or 固定値)
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
                    calcItemData.criticalRate = calcCriticalRate(victim.getType(), damagerStatus, calcItemData.criticalRate, c);
                }
            }
        }
        return new CalcDamageData(
                victimStatus,
                calcItemData.criticalRate,
                calcItemData.buffs,
                calcItemData.addAttacks,
                powerValue
        );
    }

    private double calcDamage(@Nullable final AlchemyItemStatus itemStatus, @Nullable final EntityStatus damagerStatus, @Nullable final Entity damager, @NotNull final LivingEntity victim, final double power, final double baseDamage, final double force, @NotNull final AttackAttribute baseAttribute, final boolean isMinecraftCritical, final boolean arrow, @NotNull final AttackCategory attackCategory) {
        final CalcDamageData data = calcData(itemStatus, damagerStatus, victim, power, force, arrow, attackCategory);
        final double damage = damage(
                baseDamage,
                data.powerValue,
                damagerStatus,
                victim,
                data.victimStatus,
                baseAttribute,
                data.buffs,
                isMinecraftCritical || data.criticalRate >= 100 || Randomizer.percent(data.criticalRate)
        );
        final DamageComponent baseDamageComponent = new DamageComponent(damage, baseAttribute);
        final List<DamageComponent> damageComponents = new ObjectArrayList<>();
        damageComponents.add(baseDamageComponent);
        double allDamage = damage;
        for (final AddAttackData addAttack : data.addAttacks) {
            final DamageComponent damageComponent = addAttack.getAddAttackType().applyDamage(addAttack, damagerStatus, victim, baseDamageComponent);
            if (damageComponent != null) {
                // とりあえず攻撃属性によるパーセント減少のみ
                final double defense = getAddAttackDefense(addAttack, data, victim, damageComponent);
                damageComponent.setDamage(damageComponent.getDamage() * ((100 - defense) / 100));
                damageComponents.add(damageComponent);
                allDamage += damageComponent.getDamage();
            }
        }
        holo.holoDamage(victim, damager, damageComponents);
        return allDamage;
    }

    private double damage(
            final double baseDamage,
            final double powerValue,
            @Nullable final EntityStatus damagerStatus,
            @NotNull final LivingEntity victim,
            @Nullable final EntityStatus victimStatus,
            @NotNull final AttackAttribute baseAttribute,
            @NotNull final List<Buff> buffs,
            final boolean isCritical
    ) {
        final boolean isPhysicalAttack = AttackAttribute.isPhysicalAttack(baseAttribute);
        final ImmutablePair<Double, Double> def = getDef(victim, victimStatus, buffs, baseAttribute);
        if (isPhysicalAttack) {
            return meruruCalcDamage.calcPhysicsDamage(
                    baseDamage, // original
                    damagerStatus == null ? 0 : damagerStatus.getAtk(),
                    powerValue,
                    def.getLeft(),
                    def.getRight(),
                    defaultCriticalMag,
                    isCritical
            );
        } else {
            return meruruCalcDamage.attributeDamage(
                    damagerStatus == null ? 0 : damagerStatus.getAtk(),
                    powerValue,
                    def.getRight(),
                    defaultCriticalMag,
                    isCritical
            );
        }
    }

    /**
     * @return def, percentDef
     */
    private ImmutablePair<Double, Double> getDef(@NotNull final Entity victim, @Nullable final EntityStatus victimStatus, @NotNull final List<Buff> buffs, @NotNull final AttackAttribute attackAttribute) {
        double victimDef = 0;
        double victimPercentDef = 0;
        if (victimStatus != null) {
            if (victimStatus instanceof MonsterStats) {
                victimPercentDef += ((MonsterStats) victimStatus).getBuffResistances().object2ObjectEntrySet().stream()
                        .filter(entry -> entry.getKey() == attackAttribute)
                        .mapToDouble(entry -> entry.getValue().getMagnification())
                        .sum();
            } else if (victim instanceof Player && ((Player) victim).isBlocking()) {
                victimDef = victimStatus.getDef();
                victimDef *= 1.2; // 盾で守っている時は、防御力を20％上昇
            }
            buffs.forEach(victimStatus::addBuff);
        }
        return new ImmutablePair<>(victimDef, victimPercentDef);
    }

    @Nullable
    private EntityStatus getEntityStatus(@Nullable final LivingEntity entity) {
        if (entity != null) {
            if (aEntityUtils.hasLivingData(entity)) {
                final LivingData livingData = aEntityUtils.getLivingData(entity);
                if (livingData.hasStats()) {
                    return livingData.getStats();
                }
            } else if (entity instanceof Player) {
                return psm.getChar(entity.getUniqueId()).getCharStats();
            }
        }
        return null;
    }

    private int calcCriticalRate(@NotNull final EntityType type, @Nullable final EntityStatus status, int criticalRate, final Characteristic c) {
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

    /**
     * 品質が100以下の場合、0.75 + 品質 / 200 (倍)
     * 品質が100以上の場合、1.25 + √{(品質 - 100) * 10} / 100 (倍)
     * 品質最大値999では、倍率はおよそ2.198倍
     *
     * @param quality 品質
     * @return 倍率
     */
    private double calcQualityCorrection(final int quality) {
        return quality <= 100 ? 0.75 + quality / 200D : 1.25 + Math.sqrt((quality - 100) * 10D) / 100;
    }

    private static class CalcDamageData {
        EntityStatus victimStatus;
        int criticalRate;
        List<Buff> buffs;
        List<AddAttackData> addAttacks;
        double powerValue;

        public CalcDamageData(EntityStatus victimStatus, int criticalRate, List<Buff> buffs, List<AddAttackData> addAttacks, double powerValue) {
            this.victimStatus = victimStatus;
            this.criticalRate = criticalRate;
            this.buffs = buffs;
            this.addAttacks = addAttacks;
            this.powerValue = powerValue;
        }
    }

    private static class CalcItemData {
        int criticalRate = 0;
        final List<Buff> buffs = new ObjectArrayList<>();
        final List<AddAttackData> addAttacks = new ObjectArrayList<>();
        int characteristicLevel = 0;
        int characteristicPower = 0;
        int percentPower = 0;
        int fixedPower = 0;
        boolean death = false;

        void calc(
                @NotNull final LivingEntity victim,
                @Nullable final EntityStatus victimStatus,
                @NotNull final AlchemyItemStatus itemStatus,
                @Nullable final EntityStatus damagerStatus,
                @NotNull final AttackCategory attackCategory
        ) {
            calcCharacteristics(victim, victimStatus, itemStatus, damagerStatus, attackCategory);
            calcActiveEffect(victim, itemStatus);
            percentPower += characteristicPower == 0 ? 0D : Math.pow(characteristicLevel, 0.7) + characteristicPower;
        }

        void calcCharacteristics(
                @NotNull final LivingEntity victim,
                @Nullable final EntityStatus victimStatus,
                @NotNull final AlchemyItemStatus itemStatus,
                @Nullable final EntityStatus damagerStatus,
                @NotNull final AttackCategory attackCategory
        ) {
            itemStatus.getCharacteristics().stream().filter(c -> c.hasCategory(itemStatus)).forEach(c -> {
                characteristicLevel += c.getLevel();
                percentPower += c.hasData(CharacteristicType.POWER) ? c.getIntData(CharacteristicType.POWER) : 0;
                if (attackCategory == AttackCategory.SKILL) {
                    skill(c);
                } else if (attackCategory == AttackCategory.ITEM) {
                    item(c, itemStatus, victimStatus);
                }

                criticalRate = INSTANCE.calcCriticalRate(victim.getType(), damagerStatus, criticalRate, c);

                if (victimStatus != null) {
                    final CharacteristicBuff characteristicBuff = (CharacteristicBuff) c.getData(CharacteristicType.BUFF);
                    if (characteristicBuff != null) {
                        final int percent = characteristicBuff.getPercent();
                        final double totalAbnormalResistanceMag;
                        if (victimStatus instanceof MonsterStats) {
                            totalAbnormalResistanceMag = ((MonsterStats) victimStatus).getBuffResistances().object2ObjectEntrySet().stream()
                                    .filter(entry -> entry.getKey() == AttackAttribute.ABNORMAL)
                                    .mapToDouble(entry -> entry.getValue().getMagnification()).sum();
                        } else {
                            totalAbnormalResistanceMag = 0;
                        }
                        final int abnormalPercent = (int) (100 + totalAbnormalResistanceMag);
                        if (percent >= abnormalPercent || Randomizer.percent(percent, abnormalPercent)) {
                            buffs.add(new Buff(
                                    victimStatus,
                                    BuffValueType.CHARACTERISTIC,
                                    characteristicBuff.getX(), // level
                                    characteristicBuff.getBuffType(),
                                    0,
                                    characteristicBuff.getDuration(),
                                    (int) (characteristicBuff.getX() * INSTANCE.calcQualityCorrection(itemStatus.getQuality())),
                                    characteristicBuff.getY()
                            ));
                        }
                    }
                }

                final AddAttackData addAttack = (AddAttackData) c.getData(CharacteristicType.ADD_ATTACK);
                if (addAttack != null) {
                    final int percent = addAttack.getPercent();
                    final AddAttackData.AttackLimitCategory addAttackLimitCategory = addAttack.getAttackLimitCategory();
                    if (addAttackLimitCategory.isAvailableAttack(attackCategory) && percent >= 100 || Randomizer.percent(percent)) {
                        addAttacks.add(addAttack);
                    }
                }
            });
        }

        void calcActiveEffect(@NotNull final LivingEntity victim, @NotNull final AlchemyItemStatus itemStatus) {
            itemStatus.getActiveEffects().forEach(effect -> {
                effect.mobHit(victim);
                CObjects.nonNullConsumer(effect.getAddAttackData(), addAttacks::add);
                if (effect.getRaisePower() != null) {
                    final RaisePower raisePower = effect.getRaisePower();
                    if (raisePower.isMultiply()) {
                        percentPower += raisePower.getPower();
                    } else {
                        fixedPower += raisePower.getPower();
                    }
                }
            });
        }

        void skill(@NotNull final Characteristic c) {
            if (c.hasData(CharacteristicType.SKILL_POWER)) {
                percentPower += c.getIntData(CharacteristicType.SKILL_POWER);
            }
            if (c.hasData(CharacteristicType.SKILL_POWER_FIXED)) {
                fixedPower += c.getIntData(CharacteristicType.SKILL_POWER_FIXED);
            }
        }

        void item(@NotNull final Characteristic c, @NotNull final AlchemyItemStatus itemStatus, @Nullable final EntityStatus victimStatus) {
            final AlchemyMaterialCategory materialCategory = itemStatus.getAlchemyMaterial().getMaterialCategory();
            percentPower += c.hasData(CharacteristicType.ITEM_POWER) ? c.getIntData(CharacteristicType.ITEM_POWER) : 0;
            characteristicPower += c.hasData(CharacteristicType.CHARACTERISTIC_POWER) ? c.getIntData(CharacteristicType.CHARACTERISTIC_POWER) : 0;
            if (c.hasData(CharacteristicType.SIZE_POWER)) {
                final int sizeCount = itemStatus.getSizeCount();
                percentPower += sizeCount * c.getIntData(CharacteristicType.SIZE_POWER);
            }
            switch (materialCategory) {
                case ATTACK:
                    if (c.hasData(CharacteristicType.CHASING) && victimStatus instanceof MonsterStats && ((MonsterStats) victimStatus).isDefenseBreaking()) {
                        percentPower += c.getIntData(CharacteristicType.CHASING);
                    }
                    // DEATH どうしよう 0=ボスを除き一撃死, 1=一部ボスを除き一撃死
                    break;
                case HEAL:
                    percentPower += c.hasData(CharacteristicType.HEAL) ? c.getIntData(CharacteristicType.HEAL) : 0;
                    fixedPower += c.hasData(CharacteristicType.HEAL_FIXED) ? c.getIntData(CharacteristicType.HEAL_FIXED) : 0;
                    break;
                default:
                    // nothing
                    break;
            }
            // COUNT_POWER アイテムの使用回数実装後、記載
            // USE_UP 同様
            // USE_ONE 同様
            // USE_SPEED_POWER アイテムのクールタイムを実装後、記載
            // MULTIPLE_BONUS どうしよう
            // SINGULAR_BONUS どうしよう
        }

    }
}
