package net.firiz.renewatelier.entity;

import org.bukkit.entity.LivingEntity;

public enum Race {
    ANIMAL(0), // 動物
    DRAGON(1), // ドラゴン
    ELEMENTAL(2), // 精霊
    ALCHEMY(3), // 錬金生物
    AIJN(4), // 亜人
    PUNI(5), // プニ
    MAGIC(6), // 魔法生物
    UNDEAD(7), // 幽霊・アンデッド
    DEMON(8), // 悪魔・デーモン
    GOD(9), // 神族
    INSECT(10), // 虫
    ;

    private final int id;

    Race(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean hasType(LivingEntity entity) {
        return false;
    }
}