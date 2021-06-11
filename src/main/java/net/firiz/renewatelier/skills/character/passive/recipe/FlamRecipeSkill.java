package net.firiz.renewatelier.skills.character.passive.recipe;

import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.entity.player.Char;

public class FlamRecipeSkill extends RecipePassiveSkill {
    public FlamRecipeSkill(Char character) {
        super(character, AlchemyRecipe.search("flam"));
    }
}
