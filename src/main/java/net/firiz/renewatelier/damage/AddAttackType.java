package net.firiz.renewatelier.damage;

import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import net.firiz.renewatelier.utils.doubledata.Four;
import org.bukkit.entity.LivingEntity;

import java.util.function.Function;

public enum AddAttackType { // <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
    DAMAGE(t -> new FinalDoubleData<>(t.getD().getLeft() * (Integer.parseInt(t.getA()[3]) * 0.01), t.getD().getRight())), // 追加ダメージ％
    DAMAGE_FIXED(t -> new FinalDoubleData<>(Double.parseDouble(t.getA()[3]), t.getD().getRight())), // 追加ダメージ
    ATTRIBUTE_DAMAGE(t -> new FinalDoubleData<>(t.getD().getLeft() * (Integer.parseInt(t.getA()[4]) * 0.01), AttackAttribute.valueOf(t.getA()[3]))), // 属性追加ダメージ <属性, ダメージ％>
    ATTRIBUTE_DAMAGE_FIXED(t -> new FinalDoubleData<>(Double.parseDouble(t.getA()[4]), AttackAttribute.valueOf(t.getA()[3]))), // 属性追加ダメージ <属性, ダメージ>
    HEAL(t -> { // ダメージ還元HP％
        final double heal = t.getD().getLeft() * (Integer.parseInt(t.getA()[3]) * 0.01);
        if (t.getB() instanceof CharStats) {
            ((CharStats) t.getB()).damageHp(-heal);
        }
        return null;
    }),
    TARGET(t -> { // ターゲット値を上昇させる
        return null; //    未実装
    }),
    DAMAGE_HEAL(t -> { // 追加ダメージ％ かつ与えたダメージ分回復
        final double percent = Integer.parseInt(t.getA()[3]) * 0.01;
        final double heal = t.getD().getLeft() * percent;
        if (t.getB() instanceof CharStats) {
            ((CharStats) t.getB()).damageHp(-heal);
        }
        return new FinalDoubleData<>(t.getD().getLeft() * percent, t.getD().getRight());
    }),
    ATTRIBUTE_DAMAGE_HEAL(t -> { // 属性追加ダメージ <属性, ダメージ％> かつ与えたダメージ分回復 <属性, ダメージ>
        final double percent = Integer.parseInt(t.getA()[4]) * 0.01;
        final double heal = t.getD().getLeft() * percent;
        if (t.getB() instanceof CharStats) {
            ((CharStats) t.getB()).damageHp(-heal);
        }
        return new FinalDoubleData<>(t.getD().getLeft() * percent, AttackAttribute.valueOf(t.getA()[3]));
    });

    /**
     * Four(Data, キャラステータス, 被害者エンティティ, ダメージ)
     * return 追加ダメージ
     */
    private final Function<Four<String[], EntityStatus, LivingEntity, FinalDoubleData<Double, AttackAttribute>>, FinalDoubleData<Double, AttackAttribute>> run;

    AddAttackType(Function<Four<String[], EntityStatus, LivingEntity, FinalDoubleData<Double, AttackAttribute>>, FinalDoubleData<Double, AttackAttribute>> run) {
        this.run = run;
    }

    public FinalDoubleData<Double, AttackAttribute> runDamage(String[] data, EntityStatus entityStatus, LivingEntity entity, FinalDoubleData<Double, AttackAttribute> damage) {
        return run.apply(new Four<>(data, entityStatus, entity, damage));
    }

}
