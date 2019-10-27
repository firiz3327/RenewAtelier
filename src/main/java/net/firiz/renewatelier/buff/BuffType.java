package net.firiz.renewatelier.buff;

public enum BuffType { // 全て <BuffType, 確率, 時間, 値>
    STATS_LEVEL, // レベル％
    STATS_HP, // HP％
    STATS_MP, // MP％
    STATS_ATK, // 攻撃力％
    STATS_DEF, // 防御力％
    STATS_SPD, // 素早さ％
    STATS_LEVEL_FIXED, // レベル
    STATS_HP_FIXED, // HP
    STATS_MP_FIXED, // MP
    STATS_ATK_FIXED, // 攻撃力
    STATS_DEF_FIXED, // 防御力
    STATS_SPD_FIXED, // 素早さ
    SLEEP, // 眠り付与 <確率, 時間, レベル>
    POISON, // 毒付与 <確率, 時間, ダメージ>
    SLOW, // スロウ付与 <確率, 時間, レベル>
    CURSE, // 呪い付与 <確率, 時間, ダメージ>
    DARKNESS, // 暗闇付与 <確率, 時間, 攻撃可能確率>
    WEAKNESS, // 弱体化付与 <確率, 時間, 値>
    SEALED, // 封印付与 <確率, 時間, レベル>
    DISABLE_HEAL, // 回復無効付与 <確率, 時間, レベル>
    DEBUFF_SLOW_SKILL, // 敵のスキルループ待機時間増加％
    EXP_UP, // 経験値量増加％
    ALCHEMY_EXP_UP, // 錬金経験値量増加％
    RECIPE_EXP_UP, // レシピ経験値量増加％
    ;
}
