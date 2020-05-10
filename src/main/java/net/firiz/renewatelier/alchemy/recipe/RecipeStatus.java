package net.firiz.renewatelier.alchemy.recipe;

import net.firiz.renewatelier.alchemy.recipe.idea.RecipeIdea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author firiz
 */
public class RecipeStatus {

    @NotNull
    private final AlchemyRecipe recipe;
    private boolean acquired;
    private int level;
    private int exp;

    @Nullable
    private final RecipeIdea idea;

    public RecipeStatus(final String id) {
        this.recipe = Objects.requireNonNull(AlchemyRecipe.search(id));
        this.level = 0;
        this.exp = 0;
        this.idea = new RecipeIdea(recipe);
    }

    public RecipeStatus(final AlchemyRecipe recipe, final boolean acquired) {
        this.recipe = Objects.requireNonNull(recipe);
        this.acquired = acquired;
        this.idea = new RecipeIdea(recipe);
    }

    public RecipeStatus(final AlchemyRecipe recipe) {
        this.recipe = Objects.requireNonNull(recipe);
        this.idea = new RecipeIdea(recipe);
    }

    public RecipeStatus(final String id, final int level, final int exp) {
        this.recipe = Objects.requireNonNull(AlchemyRecipe.search(id));
        this.acquired = true;
        this.level = level;
        this.exp = exp;
        this.idea = null;
    }

    public RecipeStatus(final String id, final int level, final int exp, final int[] idea) {
        this.recipe = Objects.requireNonNull(AlchemyRecipe.search(id));
        this.level = level;
        this.exp = exp;
        if (idea == null) {
            if (recipe.isIdeaRequired()) {
                this.idea = new RecipeIdea(recipe);
            } else {
                this.acquired = true;
                this.idea = null;
            }
        } else {
            this.idea = new RecipeIdea(recipe, idea);
        }
    }

    @NotNull
    public AlchemyRecipe getRecipe() {
        return recipe;
    }

    public String getId() {
        return recipe.getId();
    }

    public boolean isAcquired() {
        return acquired;
    }

    public void setAcquired(boolean acquired) {
        this.acquired = acquired;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(final int exp) {
        this.exp = exp;
    }

    @Nullable
    public RecipeIdea getIdea() {
        return idea;
    }
}
