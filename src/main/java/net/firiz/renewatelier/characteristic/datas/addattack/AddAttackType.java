package net.firiz.renewatelier.characteristic.datas.addattack;

import net.firiz.renewatelier.characteristic.datas.addattack.x.AddAttackX;
import net.firiz.renewatelier.characteristic.datas.addattack.x.AttributeAddAttack;
import net.firiz.renewatelier.characteristic.datas.addattack.x.IntAddAttack;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageComponent;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum AddAttackType { // <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
    DAMAGE(AddAttackType::createIntAddAttack, addAttackCalcData -> { // 追加ダメージ％ <ダメージ％(int)>
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        return new DamageComponent(
                addAttackCalcData.damageComponent.getDamage() * (x.getX() * 0.01),
                addAttackCalcData.damageComponent.getAttackAttribute()
        );
    }),
    DAMAGE_FIXED(AddAttackType::createIntAddAttack, addAttackCalcData -> {
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        return new DamageComponent(x.getX(), addAttackCalcData.damageComponent.getAttackAttribute());
    }), // 追加ダメージ <ダメージ(int)>
    ATTRIBUTE_DAMAGE(AddAttackType::createAttributeAddAttack, addAttackCalcData -> {
        final AttributeAddAttack x = (AttributeAddAttack) addAttackCalcData.addAttack.getX();
        return new DamageComponent(addAttackCalcData.damageComponent.getDamage() * (x.getY() * 0.01), x.getX());
    }), // 属性追加ダメージ <属性(AttackAttribute), ダメージ％(int)>
    ATTRIBUTE_DAMAGE_FIXED(AddAttackType::createAttributeAddAttack, addAttackCalcData -> {
        final AttributeAddAttack x = (AttributeAddAttack) addAttackCalcData.addAttack.getX();
        return new DamageComponent(x.getY(), x.getX());
    }), // 属性追加ダメージ <属性(AttackAttribute), ダメージ(int)>
    HEAL(AddAttackType::createIntAddAttack, addAttackCalcData -> { // ダメージ還元HP％ <ダメージから回復する割合％(int)>
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        final double heal = addAttackCalcData.damageComponent.getDamage() * (x.getX() * 0.01);
        if (addAttackCalcData.entityStatus instanceof CharStats) {
            ((CharStats) addAttackCalcData.entityStatus).heal(heal);
        }
        return null;
    }),
    TARGET(AddAttackType::createIntAddAttack, addAttackCalcData -> { // 未実装 ターゲット値を上昇させる
        return null;
    }),
    DAMAGE_HEAL(AddAttackType::createIntAddAttack, addAttackCalcData -> { // 追加ダメージ％ <ダメージ％(int)> (与えた追加ダメージ分回復)
        final IntAddAttack x = (IntAddAttack) addAttackCalcData.addAttack.getX();
        final double percent = x.getX() * 0.01;
        final double damage = addAttackCalcData.damageComponent.getDamage() * percent;
        if (addAttackCalcData.entityStatus instanceof CharStats) {
            ((CharStats) addAttackCalcData.entityStatus).heal(damage);
        }
        return new DamageComponent(addAttackCalcData.damageComponent.getDamage() * percent, addAttackCalcData.damageComponent.getAttackAttribute());
    }),
    ATTRIBUTE_DAMAGE_HEAL(AddAttackType::createAttributeAddAttack, addAttackCalcData -> { // 属性追加ダメージ <属性(AttackAttribute), ダメージ％(int)> (与えた追加ダメージ分回復)
        final AttributeAddAttack x = (AttributeAddAttack) addAttackCalcData.addAttack.getX();
        final double percent = x.getY() * 0.01;
        final double damage = addAttackCalcData.damageComponent.getDamage() * percent;
        // 属性ダメージ特攻・軽減の計算が含まれてない
        if (addAttackCalcData.entityStatus instanceof CharStats) {
            ((CharStats) addAttackCalcData.entityStatus).heal(damage);
        }
        return new DamageComponent(damage, x.getX());
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
    public DamageComponent applyDamage(AddAttackData addAttack, EntityStatus entityStatus, LivingEntity victim, DamageComponent damageComponent) {
        return function.apply(new AddAttackCalcData(addAttack, entityStatus, victim, damageComponent));
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

    @FunctionalInterface
    private interface AddAttackFunction {
        DamageComponent apply(AddAttackCalcData addAttackCalcData);
    }

    private static class AddAttackCalcData {
        final AddAttackData addAttack;
        final EntityStatus entityStatus;
        final LivingEntity victim;
        final DamageComponent damageComponent;

        private AddAttackCalcData(AddAttackData addAttack, EntityStatus entityStatus, LivingEntity victim, DamageComponent damageComponent) {
            this.addAttack = addAttack;
            this.entityStatus = entityStatus;
            this.victim = victim;
            this.damageComponent = damageComponent;
        }
    }

}
