package net.firiz.renewatelier.alchemy.recipe;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author firiz
 */
public class RecipeLevelEffect {
    private final RecipeLEType type;
    private final int count;

    public RecipeLevelEffect(RecipeLEType type, int count) {
        this.type = type;
        this.count = count;
    }

    public int getCount(RecipeLEType type) {
        if (type == this.type) {
            return count;
        }
        return 0;
    }

    public int getCount() {
        return count;
    }

    public RecipeLEType getType() {
        return type;
    }

    public enum RecipeLEType {
        ADD_INHERITING("特性引継数", true), // 特性引継ぎ可能数増加 +x
        ADD_QUALITY("品質", true), // 品質増加 +x
        ADD_AMOUNT("作成数", true), // 作成数増加 +x
        ADD_USECOUNT("使用回数", true), // 使用回数増加 +x
        @Deprecated ADD_VERTICAL_ROTATION("上下反転", false), // 上下反転
        @Deprecated ADD_HORIZONTAL_ROTATION("左右反転", false), // 左右反転
        @Deprecated ADD_ROTATION("回転", false) // 回転
        ;

        private final String name;
        private final boolean viewNumber;

        RecipeLEType(String name, boolean viewNumber) {
            this.name = name;
            this.viewNumber = viewNumber;
        }

        public String getName() {
            return name;
        }

        public boolean isViewNumber() {
            return viewNumber;
        }

        @NotNull
        public static RecipeLEType search(String value) {
            return Arrays.stream(RecipeLEType.values())
                    .filter(type -> type.name().equals(value) || type.name.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("RecipeLEType - " + value + " is not found."));
        }
    }

}
