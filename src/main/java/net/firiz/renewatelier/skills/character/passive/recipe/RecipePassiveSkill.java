package net.firiz.renewatelier.skills.character.passive.recipe;

import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.skills.character.passive.PassiveSkill;

import java.util.Objects;

public class RecipePassiveSkill extends PassiveSkill {

    private final AlchemyRecipe[] recipes;

    protected RecipePassiveSkill(Char character, AlchemyRecipe... recipes) {
        super(character);
        this.recipes = recipes;
    }

    @Override
    public boolean fire() {
        final Char player = getCharacter();
        for (final AlchemyRecipe recipe : recipes) {
            if (!player.hasRecipe(recipe) || !Objects.requireNonNull(player.getRecipeStatus(recipe)).isAcquired()) {
                player.addRecipe(true, recipe, true);
            }
        }
        return false;
    }
}
