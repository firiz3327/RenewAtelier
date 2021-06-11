package net.firiz.renewatelier.damage;

import net.firiz.ateliercommonapi.adventure.text.C;
import org.bukkit.ChatColor;

public enum AttackAttribute {
    NONE("無", ""),
    SLASH("斬撃", "⒤"),
    BLOW("打撃", "⒥"),
    THRUST("突撃", "⒦"),
    MAGIC("魔法", "⒧"),
    FIRE("炎", "⒨", ChatColor.RED, C.RED),
    ICE("氷", "⒩", ChatColor.BLUE, C.BLUE),
    LIGHTNING("雷", "⒪", ChatColor.YELLOW, C.YELLOW),
    ABNORMAL("状態異常", "⒫", ChatColor.LIGHT_PURPLE, C.LIGHT_PURPLE),
    HEAL("回復", "", ChatColor.GREEN, C.GREEN);

    private final String name;
    private final String icon;
    private final ChatColor color;
    private final C color2;

    AttackAttribute(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.color = ChatColor.WHITE;
        this.color2 = C.WHITE;
    }

    AttackAttribute(String name, String icon, ChatColor color, C color2) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.color2 = color2;
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

    public ChatColor getColor() {
        return color;
    }

    public C getColor2() {
        return color2;
    }

    public static boolean isPhysicalAttack(AttackAttribute attackAttribute) {
        return switch (attackAttribute) {
            case SLASH, BLOW, THRUST -> true;
            default -> false;
        };
    }

}
