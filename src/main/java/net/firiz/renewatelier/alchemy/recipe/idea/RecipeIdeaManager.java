package net.firiz.renewatelier.alchemy.recipe.idea;

import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeIdeaManager {

    private final List<RecipeIdea> ideaList = new ArrayList<>();

    public void increaseItem(ItemStack item) {
        ideaList.forEach(idea -> {
            if(idea.increaseIdea(new RecipeIdea.IncreaseIdea(item))) {

            }
        });
    }

    public void increaseRecipe(AlchemyRecipe recipe) {

    }

}
