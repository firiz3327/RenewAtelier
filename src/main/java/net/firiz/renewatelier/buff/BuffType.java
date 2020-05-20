package net.firiz.renewatelier.buff;

import java.util.Arrays;

public enum BuffType { // 全て <BuffType, 確率, 時間, 値>
    STATS_LEVEL(0), // レベル％
    STATS_HP(1), // HP％
    STATS_MP(1), // MP％
    STATS_ATK(0), // 攻撃力％
    STATS_DEF(0), // 防御力％
    STATS_SPD(0), // 素早さ％
    STATS_LEVEL_FIXED(0), // レベル
    STATS_HP_FIXED(1), // HP
    STATS_MP_FIXED(1), // MP
    STATS_ATK_FIXED(0), // 攻撃力
    STATS_DEF_FIXED(0), // 防御力
    STATS_SPD_FIXED(0), // 素早さ
    SLEEP(2), // 眠り付与 <確率, 時間, レベル>
    POISON(2), // 毒付与 <確率, 時間, ダメージ>
    SLOW(2), // スロウ付与 <確率, 時間, レベル>
    CURSE(2), // 呪い付与 <確率, 時間, ダメージ>
    DARKNESS(2), // 暗闇付与 <確率, 時間, 攻撃可能確率>
    BURN(3), // 火傷付与 <確率, 時間, ダメージ>
    DOT(3), // 持続ダメージ <確率, 時間, ダメージ>
    WEAKNESS(4), // 弱体化付与 <確率, 時間, 値>
    SEALED(2), // 封印付与 <確率, 時間, レベル>
    ANTI_HEAL(2), // 回復無効付与 <確率, 時間, レベル>
    AUTO_HEAL(5), // 持続回復 <確率, 時間, 回復量>
    DEBUFF_SLOW_SKILL(-1), // 敵のスキルループ待機時間増加％
    EXP_UP(-1), // 経験値量増加％
    ALCHEMY_EXP_UP(-1), // 錬金経験値量増加％
    RECIPE_EXP_UP(-1), // レシピ経験値量増加％
    ;

    private final Icon icon;

    BuffType(int group) {
        this.icon = Arrays.stream(Icon.values())
                .filter(ico -> ico.group == group)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("[BuffType] not support icon."));
    }

    public String getWord(boolean up) {
        if (icon.up == null) {
            return icon.word;
        }
        return up ? icon.up : icon.down;
    }

    private enum Icon {
        NONE(-1, ""),
        STATUS(0, "⒜", "⒝"), // level, atk, def, speed
        HP_MP(1, "⒞", "⒟"), // hp, mp
        CURSE(2, "⒠"), // slow, sleep, poison, curse, darkness, sealed, anti_heal
        BURN(3, "⒡"), // burn, burn2
        WEAKNESS(4, "⒢"), // weakness
        HEAL(5, "⒣") // auto_heal
        ;

        final int group;
        final String word;
        final String up;
        final String down;

        Icon(int group, String word) {
            this.group = group;
            this.word = word;
            this.up = null;
            this.down = null;
        }

        Icon(int group, String up, String down) {
            this.group = group;
            this.word = null;
            this.up = up;
            this.down = down;
        }
    }

}
