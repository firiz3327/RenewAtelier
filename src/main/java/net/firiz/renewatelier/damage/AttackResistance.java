package net.firiz.renewatelier.damage;

import java.util.Arrays;

public enum AttackResistance {
    WEAKNESS(0, "×", 2), // 弱点
    NONE(1, "－", 1), // なし
    SMALL(2, "△", 0.8), // 小耐性
    MEDIUM(3, "○", 0.6), // 中耐性
    LARGE(4, "◎", 0.4), // 大耐性
    SUPER(5, "☆", 0.2); // 超耐性

    private final int id;
    private final String icon;
    private final double magnification;

    AttackResistance(int id, String icon, double magnification) {
        this.id = id;
        this.icon = icon;
        this.magnification = magnification;
    }

    public AttackResistance down() {
        if (this == WEAKNESS) {
            return this;
        }
        final int downId = this.id - 1;
        return Arrays.stream(values()).filter(attackResistance -> downId == attackResistance.id).findFirst().orElse(AttackResistance.NONE);
    }

    public String getIcon() {
        return icon;
    }

    public double getMagnification() {
        return magnification;
    }
}
