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
package net.firiz.renewatelier.alchemy.material;

import java.util.List;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.loader.AlchemyMaterialLoader;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.DoubleData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class AlchemyMaterial {

    private final static ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final String name;
    private final boolean default_name;
    private final DoubleData<Material, Short> material;
    private final int quality_min;
    private final int quality_max;
    private final int price;
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
            int price,
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
        this.price = price;
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

    public static AlchemyMaterial getMaterial(final String id) {
        for (final Object obj : CONFIG_MANAGER.getList(AlchemyMaterialLoader.class)) {
            final AlchemyMaterial am = (AlchemyMaterial) obj;
            if (am.getId().equalsIgnoreCase(id)) {
                return am;
            }
        }
        return null;
    }

    public static AlchemyMaterial getMaterial(final ItemStack item) {
        final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.ID, item);
        if (!lores.isEmpty()) {
            return getMaterial(AlchemyItemStatus.getId(item));
        }
        return null;
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

    public int getQualityMin() {
        return quality_min;
    }

    public int getQualityMax() {
        return quality_max;
    }

    public int getPrice() {
        return price;
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
        if (catalyst_categorys != null && catalyst != null) {
            for (final String str : catalyst_categorys) {
                if (str.equals("material:".concat(id))) {
                    return true;
                }
                for(final Category c : categorys) {
                    if(str.equals("category:".concat(c.name()))) {
                        return true;
                    }
                }
            }
        }
        return false;
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
