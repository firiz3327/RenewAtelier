package net.firiz.renewatelier.characteristic;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterialCategory;

/**
 * @author firiz
 */
public enum CharacteristicCategory {
    ALL(null),
    ALCHEMY(AlchemyMaterialCategory.ALCHEMY),
    ATTACK(AlchemyMaterialCategory.ATTACK),
    HEAL(AlchemyMaterialCategory.HEAL),
    WEAPON(AlchemyMaterialCategory.WEAPON),
    ARMOR(AlchemyMaterialCategory.ARMOR),
    DECORATION(AlchemyMaterialCategory.ACCESSORY);

    private final AlchemyMaterialCategory materialCategory;

    CharacteristicCategory(AlchemyMaterialCategory materialCategory) {
        this.materialCategory = materialCategory;
    }

    public String getName() {
        return this == ALL ? "全種" : materialCategory.getName();
    }

    public boolean c(AlchemyMaterialCategory materialCategory) {
        return this == ALL || this.materialCategory == materialCategory;
    }

}
