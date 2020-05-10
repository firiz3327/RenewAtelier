package net.firiz.renewatelier.characteristic;

/**
 *
 * @author firiz
 */
public enum CharacteristicCategory {
    ALL("全種"),
    ALCHEMY("調合"),
    ATTACK("攻撃"),
    HEAL("回復"),
    WEAPON("武器"),
    ARMOR("防具"),
    DECORATION("装飾");

    private final String name;

    CharacteristicCategory(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

}
