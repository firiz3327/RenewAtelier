package net.firiz.renewatelier.alchemy.material;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum AlchemyMaterialCategory {
    MATERIAL("素材"),
    ALCHEMY("調合"),
    ATTACK("攻撃"),
    HEAL("回復"),
    SUPPORT("補助"), // 竜の神薬とか
    TOOL("探索"),
    WEAPON("武器"),
    ARMOR("防具"),
    ACCESSORY("装飾品"),
    OBJECT("オブジェ"),
    IMPORTANT("重要");

    private final String name;

    AlchemyMaterialCategory(String name) {
        this.name = name;
    }

    public static AlchemyMaterialCategory search(@NotNull final String name) {
        return Arrays.stream(values()).filter(materialCategory -> materialCategory.name.equals(name)).findFirst().orElseThrow(() -> {
            throw new IllegalArgumentException(name + " is not found.");
        });
    }

    public String getName() {
        return name;
    }
}
