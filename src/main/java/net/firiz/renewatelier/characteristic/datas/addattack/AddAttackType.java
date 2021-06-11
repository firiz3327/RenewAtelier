package net.firiz.renewatelier.characteristic.datas.addattack;

import net.firiz.renewatelier.buff.IBuff;
import net.firiz.renewatelier.characteristic.datas.addattack.x.AddAttackX;
import net.firiz.renewatelier.characteristic.datas.addattack.x.AttributeAddAttack;
import net.firiz.renewatelier.characteristic.datas.addattack.x.IntAddAttack;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageComponent;
import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * addAttack用のダメージ計算がいる
 */
public enum AddAttackType { // <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
    DAMAGE(AddAttackType::createIntAddAttack, addAttackCalcData -> { // 追加ダメージ％ <ダメージ％(int)>
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        return addAttackCalc(addAttackCalcData, x.getX(), false, addAttackCalcData.damageComponent.getAttackAttribute());
    }),
    DAMAGE_FIXED(AddAttackType::createIntAddAttack, addAttackCalcData -> {
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        return addAttackCalc(addAttackCalcData, x.getX(), true, addAttackCalcData.damageComponent.getAttackAttribute());
    }), // 追加ダメージ <ダメージ(int)>
    ATTRIBUTE_DAMAGE(AddAttackType::createAttributeAddAttack, addAttackCalcData -> {
        final AttributeAddAttack x = (AttributeAddAttack) addAttackCalcData.addAttack.getX();
        return addAttackCalc(addAttackCalcData, x.getY(), false, x.getX());
    }), // 属性追加ダメージ <属性(AttackAttribute), ダメージ％(int)>
    ATTRIBUTE_DAMAGE_FIXED(AddAttackType::createAttributeAddAttack, addAttackCalcData -> {
        final AttributeAddAttack x = (AttributeAddAttack) addAttackCalcData.addAttack.getX();
        return addAttackCalc(addAttackCalcData, x.getY(), true, x.getX());
    }), // 属性追加ダメージ <属性(AttackAttribute), ダメージ(int)>
    HEAL(AddAttackType::createIntAddAttack, addAttackCalcData -> { // ダメージ還元HP％ <ダメージから回復する割合％(int)>
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        heal(addAttackCalcData, addAttackCalcData.damageComponent.getDamage() * (x.getX() * 0.01));
        return null;
    }),
    TARGET(AddAttackType::createIntAddAttack, addAttackCalcData -> { // 未実装 ターゲット値を上昇させる
        return null;
    }),
    DAMAGE_HEAL(AddAttackType::createIntAddAttack, addAttackCalcData -> { // 追加ダメージ％ <ダメージ％(int)> (与えた追加ダメージ分回復)
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        final DamageComponent damageComponent = addAttackCalc(addAttackCalcData, x.getX(), false, addAttackCalcData.damageComponent.getAttackAttribute());
        heal(addAttackCalcData, damageComponent.getDamage());
        return damageComponent;
    }),
    ATTRIBUTE_DAMAGE_HEAL(AddAttackType::createAttributeAddAttack, addAttackCalcData -> { // 属性追加ダメージ <属性(AttackAttribute), ダメージ％(int)> (与えた追加ダメージ分回復)
        final AttributeAddAttack x = (AttributeAddAttack) addAttackCalcData.addAttack.getX();
        final DamageComponent damageComponent = addAttackCalc(addAttackCalcData, x.getY(), false, x.getX());
        heal(addAttackCalcData, damageComponent.getDamage());
        return damageComponent;
    });

    private final Function<String[], AddAttackX> createAddAttackX;
    private final AddAttackFunction function;

    AddAttackType(Function<String[], AddAttackX> createAddAttackX, AddAttackFunction function) {
        this.createAddAttackX = createAddAttackX;
        this.function = function;
    }

    /**
     * addAttackData, キャラステータス, 被害者エンティティ, ダメージ
     *
     * @return 追加ダメージ\<ダメージ, 攻撃属性\> 存在しない場合はnullを返す
     */
    @Nullable
    public DamageComponent applyDamage(boolean isItem, double power, int criticalRate, AddAttackData addAttack, Entity damager, EntityStatus damagerStatus, LivingEntity victim, EntityStatus victimStatus, DamageComponent damageComponent, List<IBuff> buffs, AlchemyItemStatus itemStatus) {
        return function.apply(new AddAttackCalcData(isItem, power, criticalRate, addAttack, damager, damagerStatus, victim, victimStatus, damageComponent, buffs, itemStatus));
    }

    public AddAttackX createAddAttackX(String[] addAttack) {
        return createAddAttackX.apply(addAttack);
    }

    private static AddAttackX createIntAddAttack(String[] addAttack) {
        return new IntAddAttack(Integer.parseInt(addAttack[3]));
    }

    private static AddAttackX createAttributeAddAttack(String[] addAttack) {
        return new AttributeAddAttack(AttackAttribute.valueOf(addAttack[3]), Integer.parseInt(addAttack[4]));
    }

    private static DamageComponent addAttackCalc(AddAttackCalcData addAttackCalcData, int x, boolean fixed, AttackAttribute attackAttribute) {
        return addAttackCalc(addAttackCalcData, x, fixed, attackAttribute, null, null);
    }

    private static void heal(AddAttackCalcData addAttackCalcData, double x) {
        if (addAttackCalcData.damager instanceof LivingEntity) {
            DamageUtilV2.INSTANCE.healHPNonActiveEffect(addAttackCalcData.damager, (LivingEntity) addAttackCalcData.damager, x);
        }
    }

    private static DamageComponent addAttackCalc(AddAttackCalcData addAttackCalcData, int x, boolean fixed, AttackAttribute attackAttribute, @Nullable LivingEntity victim, @Nullable EntityStatus victimStatus) {
        return DamageUtilV2.INSTANCE.addAttackDamage(
                addAttackCalcData.isItem,
                x,
                fixed,
                addAttackCalcData.addAttack.isIgnoreDefense(),
                addAttackCalcData.criticalRate,
                addAttackCalcData.damager,
                addAttackCalcData.damagerStatus,
                victim == null ? addAttackCalcData.victim : victim,
                victim == null ? addAttackCalcData.victimStatus : victimStatus,
                addAttackCalcData.buffs,
                addAttackCalcData.itemStatus,
                attackAttribute
        );
    }

    @FunctionalInterface
    private interface AddAttackFunction {
        DamageComponent apply(AddAttackCalcData addAttackCalcData);
    }

    private static class AddAttackCalcData {
        final boolean isItem;
        final double power;
        final int criticalRate;
        final AddAttackData addAttack;
        final Entity damager;
        @Nullable
        final EntityStatus damagerStatus;
        final LivingEntity victim;
        @Nullable
        final EntityStatus victimStatus;
        final DamageComponent damageComponent;
        final List<IBuff> buffs;
        @Nullable
        final AlchemyItemStatus itemStatus;

        private AddAttackCalcData(boolean isItem, double power, int criticalRate, AddAttackData addAttack, Entity damager, @Nullable EntityStatus damagerStatus, LivingEntity victim, @Nullable EntityStatus victimStatus, DamageComponent damageComponent, List<IBuff> buffs, @Nullable AlchemyItemStatus itemStatus) {
            this.isItem = isItem;
            this.power = power;
            this.criticalRate = criticalRate;
            this.addAttack = addAttack;
            this.damager = damager;
            this.damagerStatus = damagerStatus;
            this.victim = victim;
            this.victimStatus = victimStatus;
            this.damageComponent = damageComponent;
            this.buffs = buffs;
            this.itemStatus = itemStatus;
        }
    }

}
