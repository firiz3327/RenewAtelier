package net.firiz.renewatelier.alchemy.material;

import net.md_5.bungee.api.ChatColor;

/**
 * @author firiz
 */
public enum AlchemyAttribute {
    RED(ChatColor.RED, "赤"),
    BLUE(ChatColor.BLUE, "青"),
    GREEN(ChatColor.GREEN, "緑"),
    YELLOW(ChatColor.YELLOW, "黄"),
    PURPLE(ChatColor.DARK_PURPLE, "紫");
    private final String color;
    private final String name;

    AlchemyAttribute(ChatColor color, String name) {
        this.color = color.toString();
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public static AlchemyAttribute searchColor(String color) {
        for (AlchemyAttribute type : AlchemyAttribute.values()) {
            if (type.getColor().equals(color)) {
                return type;
            }
        }
        return null;
    }
}
