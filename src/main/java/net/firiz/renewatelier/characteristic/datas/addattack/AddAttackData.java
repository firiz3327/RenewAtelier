package net.firiz.renewatelier.characteristic.datas.addattack;

import net.firiz.renewatelier.characteristic.datas.ChData;
import net.firiz.renewatelier.characteristic.datas.addattack.x.AddAttackX;
import net.firiz.renewatelier.damage.AttackCategory;
import net.firiz.renewatelier.item.json.itemeffect.IItemEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AddAttackData implements ChData, IItemEffect {

    // 追加攻撃 <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
    private final AddAttackType addAttackType;
    private final int percent;
    private final AttackLimitCategory attackLimitCategory;
    private final AddAttackX x;
    private final boolean ignoreDefense;

    public AddAttackData(@NotNull AddAttackType addAttackType, int percent, @NotNull AttackLimitCategory attackLimitCategory, @NotNull AddAttackX x) {
        this.addAttackType = Objects.requireNonNull(addAttackType);
        this.percent = percent;
        this.attackLimitCategory = Objects.requireNonNull(attackLimitCategory);
        this.x = Objects.requireNonNull(x);
        this.ignoreDefense = false;
    }

    public AddAttackData(AddAttackType addAttackType, int percent, AttackLimitCategory attackLimitCategory, AddAttackX x, boolean ignoreDefense) {
        this.addAttackType = addAttackType;
        this.percent = percent;
        this.attackLimitCategory = attackLimitCategory;
        this.x = x;
        this.ignoreDefense = ignoreDefense;
    }

    public static ChData newInstance(String[] args) {
        final AddAttackType addAttackType = AddAttackType.valueOf(args[0]);
        return new AddAttackData(addAttackType, Integer.parseInt(args[1]), AttackLimitCategory.search(Integer.parseInt(args[2])), addAttackType.createAddAttackX(args));
    }

    public AddAttackType getAddAttackType() {
        return addAttackType;
    }

    public int getPercent() {
        return percent;
    }

    public AttackLimitCategory getAttackLimitCategory() {
        return attackLimitCategory;
    }

    public AddAttackX getX() {
        return x;
    }

    public boolean isIgnoreDefense() {
        return ignoreDefense;
    }

    public enum AttackLimitCategory {
        ALL_ATTACK(-1), // 全ての攻撃
        OTHER_SKILL(0), // スキル以外
        ITEM_ONLY(1), // アイテムのみ
        WEAPON_ONLY(2), // 武器のみ 未使用 よくわからない
        NORMAL_ATTACK_ONLY(3); // 通常攻撃のみ

        // 下記正規表現で探せる
        // ADD\_ATTACK, [a-z]+, [0-1]+, <id>
        private final int id;

        AttackLimitCategory(int id) {
            this.id = id;
        }

        public static AttackLimitCategory search(int id) {
            final Optional<AttackLimitCategory> value = Arrays.stream(values()).filter(category -> category.id == id).findFirst();
            if (!value.isPresent()) {
                throw new IllegalArgumentException("[AttackLimitCategory] not found id " + id);
            }
            return value.get();
        }

        public boolean isAvailableAttack(AttackCategory attackCategory) {
            if (this == OTHER_SKILL && attackCategory == AttackCategory.SKILL) {
                return false;
            }
            if (this == ITEM_ONLY && attackCategory == AttackCategory.ITEM) {
                return true;
            }
            if (this == NORMAL_ATTACK_ONLY && attackCategory == AttackCategory.NORMAL) {
                return true;
            }
            return this == ALL_ATTACK;
        }

    }

}
