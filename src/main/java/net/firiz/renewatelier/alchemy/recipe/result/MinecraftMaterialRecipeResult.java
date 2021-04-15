package net.firiz.renewatelier.alchemy.recipe.result;

import net.firiz.renewatelier.inventory.item.CustomModelMaterial;
import org.bukkit.Material;

public class MinecraftMaterialRecipeResult extends ARecipeResult<Material> {

    private final CustomModelMaterial customModelMaterial;

    public MinecraftMaterialRecipeResult(CustomModelMaterial result) {
        super(result.getMaterial());
        this.customModelMaterial = result;
    }

    @Override
    public CustomModelMaterial getCustomModelMaterial() {
        return customModelMaterial;
    }

}
