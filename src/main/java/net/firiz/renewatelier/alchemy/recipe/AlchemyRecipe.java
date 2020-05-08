/*
 * AlchemyRecipe.java
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

import java.util.*;

import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.idea.RecipeIdea;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.AlchemyRecipeLoader;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public class AlchemyRecipe {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final String result;
    private final int amount;
    private final List<RequireAmountMaterial> reqMaterial;
    private final int reqAlchemyLevel;
    private final List<AlchemyIngredients> defaultIngredients;
    private final int reqBar;
    private final List<RecipeEffect> effects;
    private final Map<Integer, List<RecipeLevelEffect>> levels;
    private final List<RequireMaterial> catalystCategories;
    private final List<RequireAmountMaterial> ideaRequires;

    public AlchemyRecipe(
            final String id,
            final String result,
            final int amount,
            final List<RequireAmountMaterial> requireMaterials,
            final int reqAlchemyLevel,
            final List<AlchemyIngredients> defaultIngredients,
            final int reqBar,
            final List<RecipeEffect> effects,
            final Map<Integer, List<RecipeLevelEffect>> levels,
            final List<RequireMaterial> catalystCategories,
            final List<RequireAmountMaterial> ideaRequires
    ) {
        this.id = id;
        this.result = result;
        this.amount = amount;
        this.reqMaterial = requireMaterials;
        this.reqAlchemyLevel = reqAlchemyLevel;
        this.defaultIngredients = defaultIngredients;
        this.reqBar = reqBar;
        this.effects = effects;
        this.levels = levels;
        this.catalystCategories = catalystCategories;
        this.ideaRequires = ideaRequires;
    }

    public static AlchemyRecipe search(@NotNull final String id) {
        for (final AlchemyRecipe recipe : CONFIG_MANAGER.getList(AlchemyRecipeLoader.class, AlchemyRecipe.class)) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    public static List<AlchemyRecipe> getIdeaRecipeList() {
        return ((AlchemyRecipeLoader) CONFIG_MANAGER.getLoader(AlchemyRecipeLoader.class, AlchemyRecipe.class)).getIdeaRecipes();
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getResult() {
        return result;
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    public List<RequireAmountMaterial> getReqMaterial() {
        return new ArrayList<>(reqMaterial);
    }

    public int getReqAlchemyLevel() {
        return reqAlchemyLevel;
    }

    @NotNull
    public List<AlchemyIngredients> getDefaultIngredients() {
        return new ArrayList<>(defaultIngredients);
    }

    public int getReqBar() {
        return reqBar;
    }

    @NotNull
    public List<RecipeEffect> getEffects() {
        return new ArrayList<>(effects);
    }

    @NotNull
    public Map<Integer, List<RecipeLevelEffect>> getLevels() {
        return new HashMap<>(levels);
    }

    @NotNull
    public List<RequireMaterial> getCatalystCategories() {
        return new ArrayList<>(catalystCategories);
    }

    @NotNull
    public List<RequireAmountMaterial> getIdeaRequires() {
        return ideaRequires;
    }

    public boolean isIdeaRequired() {
        return !ideaRequires.isEmpty();
    }

    public boolean hasIdeaRequire(@NotNull RecipeIdea.IncreaseIdea idea) {
        if (!isIdeaRequired()) {
            throw new IllegalStateException("There are no ideas for this recipe.");
        }
        Objects.requireNonNull(idea);
        if (idea.getMaterial() != null) {
            for (final RequireAmountMaterial requireMaterial : ideaRequires) {
                if ((
                        requireMaterial.getType() == RequireMaterial.RequireType.MATERIAL
                                && requireMaterial.getMaterial() == idea.getMaterial()
                ) || (
                        requireMaterial.getType() == RequireMaterial.RequireType.CATEGORY
                                && idea.getMaterial().getCategories().contains(requireMaterial.getCategory())
                )) {
                    return true;
                }
            }
        } else if (idea.getRecipe() != null) {
            for (final RequireAmountMaterial requireMaterial : ideaRequires) {
                if (requireMaterial.getType() == RequireMaterial.RequireType.RECIPE
                        && requireMaterial.getRecipe() == idea.getRecipe()) {
                    return true;
                }
            }
        }
        return false;
    }

}
