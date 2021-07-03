package net.firiz.renewatelier.damage;

import net.firiz.ateliercommonapi.adventure.text.C;

public enum AttackAttribute {
    NONE("無", ""),
    SLASH("斬撃", "⒤"),
    BLOW("打撃", "⒥"),
    THRUST("突撃", "⒦"),
    MAGIC("魔法", "⒧"),
    FIRE("炎", "⒨", C.RED),
    ICE("氷", "⒩", C.BLUE),
    LIGHTNING("雷", "⒪", C.YELLOW),
    ABNORMAL("状態異常", "⒫", C.LIGHT_PURPLE),
    HEAL("回復", "", C.GREEN);

    private final String name;
    private final String icon;
    private final C color;

    AttackAttribute(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.color = C.WHITE;
    }

    AttackAttribute(String name, String icon, C color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public boolean hasIcon() {
        return !icon.isEmpty();
    }

    public C getColor() {
        return color;
    }

    public static boolean isPhysicalAttack(AttackAttribute attackAttribute) {
        return switch (attackAttribute) {
            case SLASH, BLOW, THRUST -> true;
            default -> false;
        };
    }

}
