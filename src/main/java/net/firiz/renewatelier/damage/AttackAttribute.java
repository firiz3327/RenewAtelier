package net.firiz.renewatelier.damage;

import org.bukkit.ChatColor;

public enum AttackAttribute {
    NONE("無", '❋'),
    BLOW("打撃", '❤'),
    SLASH("斬撃", '✗'),
    RUSH("突撃", '✸'),
    MAGIC("魔法", '۞'),
    ABNORMAL("状態異常", '☠'),
    FIRE("炎", '❁', ChatColor.RED),
    ICE("氷", '❆', ChatColor.BLUE),
    LIGHTNING("雷", '⚡', ChatColor.YELLOW),
    WIND("風", '☁', ChatColor.GREEN),
    PHYSICS("物理", '✴');

    private final String name;
    private final char icon;
    private final ChatColor color;

    AttackAttribute(String name, char icon) {
        this.name = name;
        this.icon = icon;
        this.color = ChatColor.WHITE;
    }

    AttackAttribute(String name, char icon, ChatColor color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public char getIcon() {
        return icon;
    }

    public ChatColor getColor() {
        return color;
    }
}
