/*
 * QuestLoader.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.config.loader;

import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.QuestResult;
import java.util.ArrayList;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Ingredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.AlchemyRecipe;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.Quest;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.QuestItem;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.ItemQuestResult;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.MoneyQuestResult;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.RecipeQuestResult;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author firiz
 */
public class QuestLoader extends ConfigLoader<Quest> {

    public QuestLoader() {
        super("quests");
    }

    @Override
    protected void loadConfig(final FileConfiguration config) {
        config.getKeys(false).forEach((key) -> {
            final ConfigurationSection item = config.getConfigurationSection(key);
            // クエスト名
            final String name = ChatColor.translateAlternateColorCodes('&', item.getString("name"));
            // 詳細説明
            final String description = ChatColor.translateAlternateColorCodes('&', item.getString("description"));
            // 次のクエストID
            final String nextQuestId = item.contains("nextQuestId") ? item.getString("nextQuestId") : null;

            // 報酬
            final List<QuestResult> results = new ArrayList<>();
            final ConfigurationSection result = item.getConfigurationSection("result");
            {
                // 報酬 - レシピ
                final List<String> recipeIds = result.contains("recipes") ? result.getStringList("recipes") : null;
                if (recipeIds != null) {
                    recipeIds.forEach((recipeId) -> {
                        results.add(new RecipeQuestResult(AlchemyRecipe.search(recipeId)));
                    });
                }
                // 報酬 - アイテム - 錬金素材以外想定しない
                if (result.contains("items")) {
                    final ConfigurationSection items = result.getConfigurationSection("items");
                    items.getKeys(false).stream().map((ikey) -> items.getConfigurationSection(ikey)).forEachOrdered((isec) -> {
                        final String material = isec.getString("material");
                        final String item_name = isec.contains("name") ? ChatColor.translateAlternateColorCodes('&', isec.getString("name")) : null;
                        final int amount = isec.contains("amount") ? isec.getInt("amount") : 1;
                        final int quality = isec.contains("quality") ? isec.getInt("quality") : -1;
                        // 使用回数の概念が存在しないので現在まだ使用不可
                        final int usecount = isec.contains("usecount") ? isec.getInt("usecount") : -1;

                        // 錬金成分
                        final List<String> ingredient_strs = isec.contains("ingredients") ? isec.getStringList("ingredients") : null;
                        List<Ingredients> ingredients = null;
                        if (ingredient_strs != null) {
                            ingredients = new ArrayList<>();
                            for (final String iid : ingredient_strs) {
                                ingredients.add(AlchemyIngredients.valueOf(iid));
                            }
                        }
                        // 特性
                        final List<String> characteristic_strs = isec.contains("characteristics") ? isec.getStringList("characteristics") : new ArrayList<>();
                        final List<Characteristic> characteristics = new ArrayList<>();
                        characteristic_strs.forEach((cid) -> {
                            characteristics.add(Characteristic.valueOf(cid));
                        });

                        results.add(new ItemQuestResult(new QuestItem(
                                material,
                                item_name,
                                amount,
                                quality,
                                usecount,
                                ingredients,
                                characteristics
                        )));
                    });
                }

                // 報酬 - お金
                final int money = result.contains("money") ? result.getInt("money") : 0;
                if(money > 0) {
                    results.add(new MoneyQuestResult(money));
                }
            }

            add(new Quest(
                    key,
                    name,
                    description.split("\n"),
                    nextQuestId,
                    results
            ));
        });
    }

}
