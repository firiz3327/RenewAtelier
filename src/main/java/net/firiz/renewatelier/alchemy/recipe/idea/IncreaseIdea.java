package net.firiz.renewatelier.alchemy.recipe.idea;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import org.bukkit.inventory.ItemStack;

public class IncreaseIdea {
    private final AlchemyMaterial material;
    private final ItemStack item;
    private final AlchemyRecipe recipe;

    public IncreaseIdea(ItemStack item) {
        this.material = AlchemyItemStatus.getMaterialNullable(item);
        this.item = material == null ? item : null;
        this.recipe = null;
    }

    public IncreaseIdea(AlchemyRecipe recipe) {
        this.material = null;
        this.item = null;
        this.recipe = recipe;
    }

    public AlchemyMaterial getMaterial() {
        return material;
    }

    public ItemStack getItem() {
        return item;
    }

    public AlchemyRecipe getRecipe() {
        return recipe;
    }
}
