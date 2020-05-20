package net.firiz.renewatelier.damage;

import org.bukkit.ChatColor;

public enum AttackAttribute {
    NONE("無", ""),
    SLASH("斬撃", "⒤"),
    BLOW("打撃", "⒥"),
    THRUST("突撃", "⒦"),
    MAGIC("魔法", "⒧"),
    FIRE("炎", "⒨", ChatColor.RED),
    ICE("氷", "⒩", ChatColor.BLUE),
    LIGHTNING("雷", "⒪", ChatColor.YELLOW),
    ABNORMAL("状態異常", "⒫");

    private final String name;
    private final String icon;
    private final ChatColor color;

    AttackAttribute(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.color = ChatColor.WHITE;
    }

    AttackAttribute(String name, String icon, ChatColor color) {
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

    public ChatColor getColor() {
        return color;
    }

    public static boolean isPhysicalAttack(AttackAttribute attackAttribute) {
        switch (attackAttribute) {
            case SLASH:
            case BLOW:
            case THRUST:
                return true;
            default:
                return false;
        }
    }

}
