package net.firiz.renewatelier.alchemy.recipe;

/**
 *
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
        if(type == this.type) {
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
        ADD_INHERITING("特性引継ぎ可能数", true), // 特性引継ぎ可能数増加 +x
        ADD_QUALITY("品質", true), // 品質増加 +x
        ADD_AMOUNT("作成数", true), // 作成数増加 +x
        ADD_USECOUNT("使用回数", true), // 使用回数増加 +x
        ADD_VERTICAL_ROTATION("上下反転", false), // 上下反転
        ADD_HORIZONTAL_ROTATION("左右反転", false), // 左右反転
        ADD_ROTATION("回転", false) // 回転
        ;
        
        private final String name;
        private final boolean viewnumber;
        
        RecipeLEType(String name, boolean viewnumber) {
            this.name = name;
            this.viewnumber = viewnumber;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isViewNumber() {
            return viewnumber;
        }
    }

}
