package net.firiz.renewatelier.damage;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.CharSettings;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakeEntity;
import net.firiz.renewatelier.version.packet.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DamageUtil {

    private DamageUtil() {
    }

    /**
     * フィリスのアトリエを出来るだけ模したダメージ計算を行う
     * <p>
     * このダメージ計算では敵の防御力によるダメージ変動を無視しています
     * 詳細は http://uq-laboratory.com/play/firis_itemdamage/top.html を見てください
     *
     * @param baseDamage           基礎ダメージ(攻略本参照)
     * @param powerValue           威力値(効果による威力値 * 品質補正倍率 * 割合強化倍率 + 反比例強化量)
     * @param itemPowerEnhancement アイテム威力強化(100%基準から足していく)
     * @param criticalValue        クリティカル倍率(125%基準から足していく)
     * @param attributeValue       属性ダメージ
     * @param finalDamageRate      最終ダメージ倍率(100%基準から足していく)
     * @param cooperationRate      連携率
     * @param critical             クリティカルしているかどうか
     * @return 計算したダメージの値
     */
    private static double calcDamage(
            double baseDamage,
            double powerValue,
            double itemPowerEnhancement,
            double criticalValue,
            double attributeValue,
            double finalDamageRate,
            double cooperationRate,
            boolean critical
    ) {
        return (baseDamage * powerValue) * ((100 + itemPowerEnhancement) * 0.01) * (critical ? (125 + criticalValue) * 0.01 : 1) * attributeValue * ((100 + finalDamageRate) * 0.01) * cooperationRate;
    }

    public static void normalDamage(AttackAttribute attribute, CharStats charStats, LivingEntity entity, double normalDamage) {
        final AlchemyItemStatus weaponStats = charStats.getWeapon();
        final Player player = charStats.getPlayer();
        final boolean isMinecraftCritical = !player.isOnGround() && (player.getLocation().getY() % 1 != 0 || player.getVelocity().getY() < -0.0784); // クリティカルハック及びフライハック対策
        if (weaponStats != null && weaponStats.getCategories().contains(Category.WEAPON)) {
            double normalAttackPower = 1;

            final AlchemyMaterial alchemyMaterial = weaponStats.getAlchemyMaterial();
            final double baseDamage = Math.max(0D, Randomizer.rand(alchemyMaterial.getBaseDamageMin(), alchemyMaterial.getBaseDamageMax()));
            if (baseDamage == 0) {
                return;
            }

            calcDamage(weaponStats, charStats, entity, normalAttackPower, baseDamage, attribute, isMinecraftCritical);
        } else {
            holoDamage(entity, charStats.getPlayer(), new FinalDoubleData<>(normalDamage, attribute));
        }
    }

    public static void arrowNormalDamage(@NotNull LivingEntity entity, @NotNull LivingEntity damager, double damage) {
        DamageUtil.holoDamage(entity, damager, new FinalDoubleData<>(damage, AttackAttribute.PHYSICS));
    }

    public static void arrowDamage(@NotNull AlchemyItemStatus bow, @Nullable AlchemyItemStatus arrow, CharStats charStats, LivingEntity entity, double normalDamage, boolean isMinecraftCritical, float force) {
        double normalAttackPower = 0.8;

        final AlchemyMaterial bowAlchemyMaterial = bow.getAlchemyMaterial();
        double baseDamage = Math.max(0D, Randomizer.rand(bowAlchemyMaterial.getBaseDamageMin(), bowAlchemyMaterial.getBaseDamageMax()));
        if (baseDamage == 0) {
            arrowNormalDamage(entity, charStats.getPlayer(), normalDamage);
            return;
        }
        if (arrow != null) {
            final AlchemyMaterial arrowAlchemyMaterial = arrow.getAlchemyMaterial();
            baseDamage += Math.max(0D, Randomizer.rand(arrowAlchemyMaterial.getBaseDamageMin(), arrowAlchemyMaterial.getBaseDamageMax())) * calcQualityCorrection(arrow.getQuality());
        }

        baseDamage *= force;
        calcDamage(bow, charStats, entity, normalAttackPower, baseDamage, AttackAttribute.PHYSICS, isMinecraftCritical);
    }

    private static void calcDamage(@NotNull AlchemyItemStatus itemStatus, CharStats charStats, LivingEntity entity, double normalAttackPower, double baseDamage, AttackAttribute baseAttribute, boolean isMinecraftCritical) {
        int criticalRate = 0;

        final List<String[]> addAttacks = new ArrayList<>(); // <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
        int characteristicLevel = 0;
        int characteristicCPower = 0;
        double characteristicPowers = 0;
        for (final Characteristic c : itemStatus.getCharacteristics()) {
            characteristicLevel += c.getLevel();
            characteristicPowers += c.hasData(CharacteristicType.POWER) ? (int) c.getData(CharacteristicType.POWER) : 0;
            characteristicCPower += c.hasData(CharacteristicType.CHARACTERISTIC_POWER) ? (int) c.getData(CharacteristicType.CHARACTERISTIC_POWER) : 0;
            criticalRate = calcCriticalRate(entity, criticalRate, c);
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
        characteristicPowers += characteristicCPower == 0 ? 0D : Math.pow(characteristicLevel, 0.7) + characteristicCPower;
        final double powerValue = normalAttackPower * calcQualityCorrection(itemStatus.getQuality()) * (characteristicPowers == 0 ? 1D : ((100D + characteristicPowers) * 0.01));

        final List<AlchemyItemStatus> equips = charStats.getEquips();
        for (final AlchemyItemStatus equip : equips) {
            for (final Characteristic c : equip.getCharacteristics()) {
                criticalRate = calcCriticalRate(entity, criticalRate, c);
            }
        }

        final Player player = charStats.getPlayer();
        final double damage = calcDamage(
                baseDamage,
                powerValue,
                0,
                0,
                1,
                0,
                1,
                isMinecraftCritical || criticalRate >= 100 || Randomizer.percent(criticalRate)
        );
        final FinalDoubleData<Double, AttackAttribute> baseDamageComponent = new FinalDoubleData<>(damage, baseAttribute);
        final List<FinalDoubleData<Double, AttackAttribute>> damageComponents = new ArrayList<>();
        damageComponents.add(baseDamageComponent);
        for (final String[] addAttack : addAttacks) {
            final FinalDoubleData<Double, AttackAttribute> damageComponent = AddAttackType.valueOf(addAttack[0]).runDamage(addAttack, charStats, entity, baseDamageComponent);
            damageComponents.add(damageComponent);
        }
        holoDamage(entity, player, damageComponents);
    }

    private static int calcCriticalRate(LivingEntity entity, int criticalRate, Characteristic c) {
        criticalRate += c.hasData(CharacteristicType.CRITICAL) ? (int) c.getData(CharacteristicType.CRITICAL) : 0;
        if (c.hasData(CharacteristicType.CRITICAL_RACE)) {
            final String[] data = (String[]) c.getData(CharacteristicType.CRITICAL_RACE);
            final Race race = Race.valueOf(Objects.requireNonNull(data)[1]);
            if (race.hasType(entity)) {
                criticalRate += Integer.parseInt(data[0]);
            }
        }
        return criticalRate;
    }

    private static double calcQualityCorrection(final int quality) {
        return quality <= 100 ? 0.75 + quality / 200D : 1.25 + Math.sqrt((quality - 100) * 10D) / 100;
    }

    public static void damagePlayer(CharStats charStats, double damage, EntityDamageEvent.DamageCause damageCause) {
        double resultDamage = damage;

        // ~~~

        charStats.damageHp(resultDamage);
    }

    private static List<FinalDoubleData<Double, AttackAttribute>> defDamage(@NotNull LivingEntity entity, @NotNull LivingEntity damager, List<FinalDoubleData<Double, AttackAttribute>> damages) {
        if(AtelierEntityUtils.INSTANCE.hasLivingData(entity)) {
            final LivingData livingData = AtelierEntityUtils.INSTANCE.getLivingData(entity);
            final MonsterStats stats = livingData.getStats();
            if(stats != null) {
                final List<FinalDoubleData<Double, AttackAttribute>> resultDamages = new ArrayList<>(damages.size());
                final int def = stats.getDef();
                for (FinalDoubleData<Double, AttackAttribute> dd : damages) {
                    resultDamages.add(new FinalDoubleData<>(dd.getLeft() - (def * 0.5), dd.getRight()));
                }
            }
        }
        return damages;
    }

    @SafeVarargs
    private static void holoDamage(@NotNull LivingEntity entity, @NotNull LivingEntity damager, FinalDoubleData<Double, AttackAttribute>... damages) {
        holoDamage(entity, damager, Arrays.asList(damages));
    }

    private static void holoDamage(@NotNull LivingEntity entity, @NotNull LivingEntity damager, List<FinalDoubleData<Double, AttackAttribute>> damages) {
        final Location location = entity.getEyeLocation();
        location.setY(location.getY() + 0.2);
        double allDamage = 0;
        for (int i = 0; i < damages.size(); i++) {
            final double damage = damages.get(i).getLeft();
            final AttackAttribute attribute = damages.get(i).getRight();
            final int intDamage = (int) damage;
            final String viewDamage;
            if (intDamage <= 0) { // 1.0 ダメージ以上でないとダメージ換算なし
                viewDamage = ChatColor.WHITE + "Miss";
            } else {
                viewDamage = String.valueOf(attribute.getColor()) + intDamage + ' ' + attribute.getIcon();
                allDamage += damage;
            }
            final FakeEntity fakeEntity = new FakeEntity(-Randomizer.nextInt(Integer.MAX_VALUE), FakeEntity.FakeEntityType.ARMOR_STAND, 0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> {
                        Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 20, 10, 20).stream().filter(e -> e instanceof Player).forEach(e -> {
                            final Player player = (Player) e;
                            final CharSettings settings = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId()).getSettings();
                            final boolean showDamage = player == damager ? settings.isShowDamage() : settings.isShowOthersDamage();
                            if (showDamage) {
                                PacketUtils.sendPacket(player, EntityPacket.getSpawnPacket(fakeEntity, location));
                                PacketUtils.sendPacket(player, EntityPacket.getMessageSmallStandMeta(player, viewDamage).compile(fakeEntity.getEntityId()));
                                Bukkit.getScheduler().runTaskLater(
                                        AtelierPlugin.getPlugin(),
                                        () -> PacketUtils.sendPacket(player, EntityPacket.getDespawnPacket(fakeEntity.getEntityId())),
                                        10
                                );
                            }
                        });
                        location.setY(location.getY() + 0.3);
                    },
                    2L * i
            );
        }
        entity.setHealth(Math.max(0, entity.getHealth() - allDamage));
        entity.setLastDamageCause(new EntityDamageEvent(
                damager,
                EntityDamageEvent.DamageCause.CUSTOM,
                allDamage
        ));
    }

}
