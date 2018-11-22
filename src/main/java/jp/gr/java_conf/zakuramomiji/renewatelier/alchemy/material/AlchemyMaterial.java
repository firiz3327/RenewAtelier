/*
 * AlchemyMaterial.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material;

import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.Catalyst;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.AlchemyRecipe;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import org.bukkit.Material;

/**
 *
 * @author firiz
 */
public class AlchemyMaterial {

    private final String id;
    private final String name;
    private final boolean default_name;
    private final DoubleData<Material, Short> material;
    private final int quality_min;
    private final int quality_max;
    private final List<Category> categorys;
    private final List<DoubleData<Ingredients, Integer>> ingredients;
    private final List<MaterialSizeData> sizes;
    private final List<Object> charas;
    private final Catalyst catalyst;
    private final String script;
    private final boolean unbreaking;
    private final boolean hideAttribute;
    private final boolean hideDestroy;
    private final boolean hideEnchant;
    private final boolean hidePlacedOn;
    private final boolean hidePotionEffect;
    private final boolean hideUnbreaking;

    public AlchemyMaterial(
            String id,
            String name,
            boolean default_name,
            DoubleData<Material, Short> material,
            int quality_min,
            int quality_max,
            List<Category> categorys,
            List<DoubleData<Ingredients, Integer>> ingredients,
            List<MaterialSizeData> sizes,
            List<Object> charas,
            Catalyst catalyst,
            String script,
            boolean unbreaking,
            boolean hideAttribute,
            boolean hideDestroy,
            boolean hideEnchant,
            boolean hidePlacedOn,
            boolean hidePotionEffect,
            boolean hideUnbreaking
    ) {
        this.id = id;
        this.name = name;
        this.default_name = default_name;
        this.material = material;
        this.quality_min = quality_min;
        this.quality_max = quality_max;
        this.categorys = categorys;
        this.ingredients = ingredients;
        this.sizes = sizes;
        this.charas = charas;
        this.catalyst = catalyst;
        this.script = script;
        this.unbreaking = unbreaking;
        this.hideAttribute = hideAttribute;
        this.hideDestroy = hideDestroy;
        this.hideEnchant = hideEnchant;
        this.hidePlacedOn = hidePlacedOn;
        this.hidePotionEffect = hidePotionEffect;
        this.hideUnbreaking = hideUnbreaking;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultName() {
        return default_name;
    }

    public DoubleData<Material, Short> getMaterial() {
        return material;
    }

    public int getQuality_min() {
        return quality_min;
    }

    public int getQuality_max() {
        return quality_max;
    }

    public List<Category> getCategorys() {
        return categorys;
    }

    public List<DoubleData<Ingredients, Integer>> getIngredients() {
        return ingredients;
    }

    public List<MaterialSizeData> getSizes() {
        return sizes;
    }

    public List<Object> getCharas() {
        return charas;
    }

    public Catalyst getCatalyst() {
        return catalyst;
    }

    public String getScript() {
        return script;
    }

    public boolean hasUsefulCatalyst(final AlchemyRecipe recipe) {
        final List<String> catalyst_categorys = recipe.getCatalyst_categorys();
        return catalyst_categorys != null && catalyst != null && catalyst_categorys.stream().anyMatch(
                (str) -> (str.equals("material:".concat(id)) || str.equals("category:".concat(catalyst.getCategory().getName())))
        );
    }

    public boolean isUnbreaking() {
        return unbreaking;
    }

    public boolean isHideAttribute() {
        return hideAttribute;
    }

    public boolean isHideDestroy() {
        return hideDestroy;
    }

    public boolean isHideEnchant() {
        return hideEnchant;
    }

    public boolean isHidePlacedOn() {
        return hidePlacedOn;
    }

    public boolean isHidePotionEffect() {
        return hidePotionEffect;
    }

    public boolean isHideUnbreaking() {
        return hideUnbreaking;
    }
}
