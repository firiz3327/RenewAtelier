package net.firiz.renewatelier.damage;

import org.apache.commons.lang.ArrayUtils;
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
    ABNORMAL("状態異常", "⒫"),
    PHYSICS(SLASH, THRUST, BLOW);

    private final String name;
    private final String icon;
    private final ChatColor color;
    private final AttackAttribute[] categories;

    AttackAttribute(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.color = ChatColor.WHITE;
        this.categories = null;
    }

    AttackAttribute(String name, String icon, ChatColor color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.categories = null;
    }

    AttackAttribute(AttackAttribute... categories) {
        this.name = null;
        this.icon = null;
        this.color = null;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        if (categories == null) {
            return icon;
        }
        return categories[0].getIcon();
    }

    public ChatColor getColor() {
        return color;
    }

    public static boolean isPhysicalAttack(AttackAttribute attackAttribute) {
        return ArrayUtils.contains(AttackAttribute.PHYSICS.categories, attackAttribute);
    }

}
