package net.firiz.renewatelier.version.minecraft;

import net.firiz.renewatelier.version.MinecraftVersion;

/**
 * @author firiz
 */
@MinecraftVersion("1.15")
public enum MinecraftRecipeSaveType {
    CAULDRON("minecraft:cauldron");

    private final String id;

    MinecraftRecipeSaveType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static MinecraftRecipeSaveType search(final String id) {
        for (MinecraftRecipeSaveType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
