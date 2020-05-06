/*
 * RecipeStatus.java
 *
 * Copyright (c) 2018 firiz.
 *
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 *
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
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

    public RecipeStatus(final String id, final boolean acquired) {
        this.recipe = Objects.requireNonNull(AlchemyRecipe.search(id));
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
