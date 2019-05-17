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
import net.firiz.renewatelier.alchemy.material.MaterialSizeData;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.loader.AlchemyRecipeLoader;

/**
 *
 * @author firiz
 */
public class AlchemyRecipe {

    private final static ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final String result;
    private final int amount;
    private final List<String> req_material;
    private final List<Ingredients> default_ingredients;
    private final int req_bar;
    private final List<RecipeEffect> effects;
    private final Map<Integer, List<RecipeLevelEffect>> levels;
    private final List<String> catalyst_categorys;
    private final List<MaterialSizeData> sizes;

    public AlchemyRecipe(
            final String id,
            final String result,
            final int amount,
            final List<String> req_material,
            final List<Ingredients> default_ingredients,
            final int req_bar,
            final List<RecipeEffect> effects,
            final Map<Integer, List<RecipeLevelEffect>> levels,
            final List<String> catalyst_categorys,
            final List<MaterialSizeData> sizes
    ) {
        this.id = id;
        this.result = result;
        this.amount = amount;
        this.req_material = req_material;
        this.default_ingredients = default_ingredients;
        this.req_bar = req_bar;
        this.effects = effects;
        this.levels = levels;
        this.catalyst_categorys = catalyst_categorys;
        this.sizes = sizes;
    }

    public static AlchemyRecipe search(final String id) {
        for (final Object obj : CONFIG_MANAGER.getList(AlchemyRecipeLoader.class)) {
            final AlchemyRecipe recipe = (AlchemyRecipe) obj;
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
        return new ArrayList<>(req_material);
    }

    public List<Ingredients> getDefaultIngredients() {
        return new ArrayList<>(default_ingredients);
    }

    public int getReqbar() {
        return req_bar;
    }

    public List<RecipeEffect> getEffects() {
        return new ArrayList<>(effects);
    }

    public Map<Integer, List<RecipeLevelEffect>> getLevels() {
        return new HashMap<>(levels);
    }

    public List<String> getCatalyst_categorys() {
        return new ArrayList<>(catalyst_categorys);
    }

    public List<MaterialSizeData> getSizes() {
        return sizes;
    }

}
