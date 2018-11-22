/*
 * AlchemyRecipeManager.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyAttribute;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Category;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Ingredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.RecipeLevelEffect.RecipeLEType;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.CustomConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author firiz
 */
public final class AlchemyRecipeManager {

    private final static AlchemyRecipeManager INSTANCE = new AlchemyRecipeManager();
    private final List<AlchemyRecipe> recipes = new ArrayList<>();

    private AlchemyRecipeManager() {
    }

    public static AlchemyRecipeManager getInstance() {
        return INSTANCE;
    }

    public AlchemyRecipe search(final String id) {
        for (final AlchemyRecipe recipe : recipes) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    public void loadConfig() {
        final AtelierPlugin plugin = AtelierPlugin.getPlugin();
        final File[] files = new File(plugin.getDataFolder(), "recipes").listFiles();
        if (files != null) {
            recipes.clear();
            for (final File f : files) {
                final FileConfiguration config = new CustomConfig(plugin, "recipes", f.getName()).getConfig();
                config.getKeys(false).forEach((key) -> {
                    final ConfigurationSection item = config.getConfigurationSection(key);
                    // リザルトマテリアル (material: AlchemyMaterial, なし: Material)
                    final String result = item.getString("result");
                    // リザルト・初期アイテム数
                    final int amount = item.getInt("amount");
                    // 必要素材
                    final List<String> req_materials = item.getStringList("req_materials");
                    // 初期錬金属性
                    final List<String> default_ingredients_str = item.getStringList("default_ingredients");
                    final List<Ingredients> default_ingredients = new ArrayList<>();
                    default_ingredients_str.forEach((str) -> {
                        default_ingredients.add(AlchemyIngredients.valueOf(str));
                    });
                    // 効果-必要ゲージ数
                    final int req_bar = item.getInt("req_bar");
                    // 効果
                    final List<RecipeEffect> effects = new ArrayList<>();
                    if (item.contains("effects")) {
                        final ConfigurationSection effects_item = item.getConfigurationSection("effects");
                        effects_item.getKeys(false).stream().map((ekey) -> effects_item.getConfigurationSection(ekey)).forEachOrdered((esec) -> {
                            final AlchemyAttribute attribute = AlchemyAttribute.valueOf(esec.getString("attribute"));
                            final List<Integer> star = esec.getIntegerList("star");
                            final List<StarEffect> starEffects = new ArrayList<>();
                            star.stream().filter((s) -> (s != 0)).map((s) -> esec.getConfigurationSection("star_effect_" + s)).map((star_effect) -> {
                                StarEffect se = null;
                                if (star_effect.contains("ingredient")) {
                                    se = new StarEffect(AlchemyIngredients.valueOf(star_effect.getString("ingredient")));
                                } else if (star_effect.contains("category")) {
                                    se = new StarEffect(Category.valueOf(star_effect.getString("category")));
                                } else if (star_effect.contains("name")) {
                                    se = new StarEffect(star_effect.getString("name"));
                                }
                                return se;
                            }).filter((se) -> (se != null)).forEachOrdered((se) -> {
                                starEffects.add(se);
                            });
                            effects.add(new RecipeEffect(attribute, star, starEffects));
                        });
                    }
                    // 熟練度
                    final Map<Integer, List<RecipeLevelEffect>> levels = new HashMap<>();
                    final ConfigurationSection levelsec = item.getConfigurationSection("levels");
                    for (int i = 1; i <= 4; i++) {
                        final List<String> level_effect_str = levelsec.getStringList("level_" + i);
                        final List<RecipeLevelEffect> level_effect = new ArrayList<>();
                        level_effect_str.stream()
                                .map((effect) -> effect.split(","))
                                .forEachOrdered((effect_split) -> {
                                    level_effect.add(new RecipeLevelEffect(
                                            RecipeLEType.valueOf(effect_split[0].trim()),
                                            Integer.parseInt(effect_split[1].trim())
                                    ));
                                });
                        levels.put(i, level_effect);
                    }
                    // 使用可能触媒
                    final List<String> catalyst_categorys = item.getStringList("usable_catalysts_categorys");
                    System.out.println(result + " " + req_materials + " " + effects + " " + levels);
                    // リストに追加
                    recipes.add(new AlchemyRecipe(key, result, amount, req_materials, default_ingredients, req_bar, effects, levels, catalyst_categorys));
                });
            }
        }
    }
}
