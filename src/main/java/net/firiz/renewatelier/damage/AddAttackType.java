package net.firiz.renewatelier.damage;

public enum AddAttackType {
    DAMAGE, // 追加ダメージ％
    DAMAGE_FIXED, // 追加ダメージ
    ATTRIBUTE_DAMAGE, // 属性追加ダメージ <属性, ダメージ％>
    ATTRIBUTE_DAMAGE_FIXED, // 属性追加ダメージ <属性, ダメージ>
    HEAL, // ダメージ還元HP％
    TARGET, // ターゲット値を上昇させる
    DAMAGE_HEAL, // 追加ダメージ％ かつ与えたダメージ分回復
    ATTRIBUTE_DAMAGE_HEAL, // 属性追加ダメージ <属性, ダメージ％> かつ与えたダメージ分回復
}
