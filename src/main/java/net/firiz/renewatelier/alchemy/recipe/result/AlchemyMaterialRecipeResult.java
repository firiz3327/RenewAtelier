package net.firiz.renewatelier.alchemy.recipe.result;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.inventory.item.CustomModelMaterial;

public class AlchemyMaterialRecipeResult extends ARecipeResult<AlchemyMaterial> {

    public AlchemyMaterialRecipeResult(AlchemyMaterial material) {
        super(material);
    }

    @Override
    public CustomModelMaterial getCustomModelMaterial() {
        return getResult().material();
    }

}
