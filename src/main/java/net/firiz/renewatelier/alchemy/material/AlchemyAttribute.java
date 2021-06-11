package net.firiz.renewatelier.alchemy.material;

import net.firiz.ateliercommonapi.adventure.text.C;

/**
 * @author firiz
 */
public enum AlchemyAttribute {
    RED(C.RED, "赤", 2),
    BLUE(C.BLUE, "青", 3),
    GREEN(C.GREEN, "緑", 4),
    YELLOW(C.YELLOW, "黄", 5),
    PURPLE(C.DARK_PURPLE, "紫", 6);

    private final C color;
    private final String name;
    private final int value;

    AlchemyAttribute(C color, String name, int value) {
        this.color = color;
        this.name = name;
        this.value = value;
    }

    public C getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
