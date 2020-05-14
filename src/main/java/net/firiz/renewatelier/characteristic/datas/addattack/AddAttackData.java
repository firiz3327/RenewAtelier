package net.firiz.renewatelier.characteristic.datas.addattack;

import net.firiz.renewatelier.characteristic.datas.CharacteristicData;
import net.firiz.renewatelier.characteristic.datas.addattack.x.AddAttackX;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AddAttackData implements CharacteristicData {

    // 追加攻撃 <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
    private final AddAttackType addAttackType;
    private final int percent;
    private final AttackCategory attackCategory;
    private final AddAttackX x;

    public AddAttackData(@NotNull AddAttackType addAttackType, int percent, @NotNull AttackCategory attackCategory, @NotNull AddAttackX x) {
        this.addAttackType = Objects.requireNonNull(addAttackType);
        this.percent = percent;
        this.attackCategory = Objects.requireNonNull(attackCategory);
        this.x = Objects.requireNonNull(x);
    }

    public AddAttackType getAddAttackType() {
        return addAttackType;
    }

    public int getPercent() {
        return percent;
    }

    public AttackCategory getAttackCategory() {
        return attackCategory;
    }

    public AddAttackX getX() {
        return x;
    }

    public enum AttackCategory {
        ALL_ATTACK(-1), // 全ての攻撃
        OTHER_SKILL(0), // スキル以外
        ITEM_ONLY(1), // アイテムのみ
        WEAPON_ONLY(2), // 武器のみ
        NORMAL_ATTACK_ONLY(3); // 通常攻撃のみ

        private final int id;

        AttackCategory(int id) {
            this.id = id;
        }

        public static AttackCategory search(int id) {
            final Optional<AttackCategory> value = Arrays.stream(values()).filter(category -> category.id == id).findFirst();
            if (!value.isPresent()) {
                throw new IllegalArgumentException("[AttackCategory] not found id " + id);
            }
            return value.get();
        }

    }

}
