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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.firiz.renewatelier.alchemy.material.Ingredients;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.AlchemyRecipeLoader;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author firiz
 */
public class AlchemyRecipe {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final String result;
    private final int amount;
    private final List<String> reqMaterial;
    private final int reqAlchemylevel;
    private final List<Ingredients> defaultIngredients;
    private final int reqBar;
    private final List<RecipeEffect> effects;
    private final Map<Integer, List<RecipeLevelEffect>> levels;
    private final List<String> catalystCategorys;

    public AlchemyRecipe(
            final String id,
            final String result,
            final int amount,
            final List<String> reqMaterial,
            final int reqAlchemylevel,
            final List<Ingredients> defaultIngredients,
            final int reqBar,
            final List<RecipeEffect> effects,
            final Map<Integer, List<RecipeLevelEffect>> levels,
            final List<String> catalystCategorys
    ) {
        this.id = id;
        this.result = result;
        this.amount = amount;
        this.reqMaterial = reqMaterial;
        this.reqAlchemylevel = reqAlchemylevel;
        this.defaultIngredients = defaultIngredients;
        this.reqBar = reqBar;
        this.effects = effects;
        this.levels = levels;
        this.catalystCategorys = catalystCategorys;
    }

    public static AlchemyRecipe search(@NotNull final String id) {
        for (final AlchemyRecipe recipe : CONFIG_MANAGER.getList(AlchemyRecipeLoader.class, AlchemyRecipe.class)) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getResult() {
        return result;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getReqMaterial() {
        return new ArrayList<>(reqMaterial);
    }

    public int getReqAlchemyLevel() {
        return reqAlchemylevel;
    }

    public List<Ingredients> getDefaultIngredients() {
        return new ArrayList<>(defaultIngredients);
    }

    public int getReqbar() {
        return reqBar;
    }

    public List<RecipeEffect> getEffects() {
        return new ArrayList<>(effects);
    }

    public Map<Integer, List<RecipeLevelEffect>> getLevels() {
        return new HashMap<>(levels);
    }

    public List<String> getCatalystCategorys() {
        return new ArrayList<>(catalystCategorys);
    }

}
