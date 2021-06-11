package net.firiz.renewatelier.skills.character.passive.recipe;

import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.entity.player.Char;

public class NeutralizationRecipeSkill extends RecipePassiveSkill {
    public NeutralizationRecipeSkill(Char character) {
        super(character, AlchemyRecipe.search("red"));
    }
}
