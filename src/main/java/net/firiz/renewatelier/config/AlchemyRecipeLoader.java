/*
 * AlchemyRecipeLoader.java
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
package net.firiz.renewatelier.config;

import java.io.File;
import java.util.*;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.material.Ingredients;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeLevelEffect;
import net.firiz.renewatelier.alchemy.recipe.StarEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author firiz
 */
public class AlchemyRecipeLoader extends ConfigLoader<AlchemyRecipe> {

    AlchemyRecipeLoader() {
        super(new File(AtelierPlugin.getPlugin().getDataFolder(), "recipes"), true);
    }

    @Override
    protected void loadConfig(final FileConfiguration config) {
        config.getKeys(false).forEach(key -> {
            final ConfigurationSection item = config.getConfigurationSection(key);
            // リザルトマテリアル (material: AlchemyMaterial, なし: Material)
            final String result = item.getString("result");
            // リザルト・初期アイテム数
            final int amount = item.getInt("amount");
            // 必要素材
            final List<String> reqMaterials = item.getStringList("req_materials");
            // 必要錬金レベル
            final int req_alchemylevel = item.contains("req_alchemylevel") ? item.getInt("req_alchemylevel") : 1;
            // 初期錬金属性
            final List<String> defaultIngredientsStr = item.getStringList("default_ingredients");
            final List<Ingredients> defaultIngredients = new ArrayList<>();
            defaultIngredientsStr.forEach(str -> defaultIngredients.add(AlchemyIngredients.searchName(str)));
            // 効果-必要ゲージ数
            final int req_bar = item.getInt("req_bar");
            // 効果
            final List<RecipeEffect> effects = new ArrayList<>();
            if (item.contains("effects")) {
                final ConfigurationSection effectsItem = item.getConfigurationSection("effects");
                effectsItem.getKeys(false).stream().map(effectsItem::getConfigurationSection).forEachOrdered(esec -> {
                    StarEffect defaultSE = null;
                    if (esec.contains("default_star_effect")) {
                        defaultSE = getStarEffect(Objects.requireNonNull(esec.getConfigurationSection("default_star_effect")));
                    }
                    final AlchemyAttribute attribute = AlchemyAttribute.valueOf(esec.getString("attribute"));
                    final List<Integer> star = esec.getIntegerList("star");
                    final List<StarEffect> starEffects = new ArrayList<>();
                    star.stream()
                            .filter(s -> (s != 0))
                            .map(s -> esec.getConfigurationSection("star_effect_" + s))
                            .filter(Objects::nonNull)
                            .map(this::getStarEffect)
                            .filter(Objects::nonNull)
                            .forEachOrdered(starEffects::add);
                    effects.add(new RecipeEffect(
                            attribute,
                            star,
                            starEffects,
                            defaultSE
                    ));
                });
            }
            // 熟練度
            final Map<Integer, List<RecipeLevelEffect>> levels = new HashMap<>();
            final ConfigurationSection levelsec = item.getConfigurationSection("levels");
            for (int i = 1; i <= 4; i++) {
                final List<String> levelEffectStr = levelsec.getStringList("level_" + i);
                final List<RecipeLevelEffect> levelEffects = new ArrayList<>();
                levelEffectStr.stream()
                        .map(effect -> effect.split(","))
                        .forEachOrdered(effectSplit -> levelEffects.add(new RecipeLevelEffect(
                                RecipeLevelEffect.RecipeLEType.valueOf(effectSplit[0].trim()),
                                Integer.parseInt(effectSplit[1].trim())
                        )));
                levels.put(i, levelEffects);
            }
            // 使用可能触媒
            final List<String> catalystCategorys = item.getStringList("usable_catalysts_categorys");
            // リストに追加
            add(new AlchemyRecipe(key, result, amount, reqMaterials, req_alchemylevel, defaultIngredients, req_bar, effects, levels, catalystCategorys));
        });
    }

    private StarEffect getStarEffect(ConfigurationSection starEffect) {
        StarEffect se = null;
        if (starEffect.contains("ingredient")) {
            se = new StarEffect(AlchemyIngredients.searchName(starEffect.getString("ingredient")));
        } else if (starEffect.contains("category")) {
            se = new StarEffect(Category.searchName(starEffect.getString("category")));
        } else if (starEffect.contains("name")) {
            se = new StarEffect(starEffect.getString("name"));
        }
        return se;
    }
}
