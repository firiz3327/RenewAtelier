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
package net.firiz.renewatelier.config;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.quest.result.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author firiz
 */
public class QuestLoader extends ConfigLoader<Quest> {

    QuestLoader() {
        super(new File(AtelierPlugin.getPlugin().getDataFolder(), "quests"), true);
    }

    @Override
    protected void loadConfig(final FileConfiguration config) {
        final List<Quest> importantQuests = new ArrayList<>();
        config.getKeys(false).forEach(key -> {
            final ConfigurationSection item = config.getConfigurationSection(key);
            // クエスト名
            final String name = ChatColor.translateAlternateColorCodes('&', item.getString("name"));
            // 詳細説明
            final String description = ChatColor.translateAlternateColorCodes('&', item.getString("description"));
            // 次のクエストID
            final String nextQuestId = item.contains("nextQuestId") ? item.getString("nextQuestId") : null;
            // 重要
            final boolean important = item.contains("important") && item.getBoolean("important");

            // 報酬
            final List<QuestResult> results = new ArrayList<>();
            final ConfigurationSection result = item.getConfigurationSection("result");
            // 報酬 - レシピ
            final List<String> recipeIds = result.contains("recipes") ? result.getStringList("recipes") : null;
            if (recipeIds != null) {
                recipeIds.forEach(recipeId -> results.add(new RecipeQuestResult(AlchemyRecipe.search(recipeId))));
            }
            // 報酬 - アイテム - 錬金素材以外想定しない
            if (result.contains("items")) {
                final ConfigurationSection items = result.getConfigurationSection("items");
                items.getKeys(false).stream().map(items::getConfigurationSection).forEachOrdered(isec -> {
                    final String material = isec.getString("material");
                    final String item_name = isec.contains("name") ? ChatColor.translateAlternateColorCodes('&', isec.getString("name")) : null;
                    final int amount = isec.contains("amount") ? isec.getInt("amount") : 1;
                    final int quality = isec.contains("quality") ? isec.getInt("quality") : -1;
                    // 使用回数の概念が存在しないので現在まだ使用不可
                    final int usecount = isec.contains("usecount") ? isec.getInt("usecount") : -1;

                    // 錬金成分
                    final List<String> ingredientStrs = isec.contains("ingredients") ? isec.getStringList("ingredients") : null;
                    List<AlchemyIngredients> ingredients = null;
                    if (ingredientStrs != null) {
                        ingredients = new ArrayList<>();
                        for (final String iid : ingredientStrs) {
                            ingredients.add(AlchemyIngredients.valueOf(iid));
                        }
                    }
                    // 特性
                    final List<String> characteristicStrs = isec.contains("characteristics") ? isec.getStringList("characteristics") : new ArrayList<>();
                    final List<Characteristic> characteristics = new ArrayList<>();
                    characteristicStrs.forEach(cid -> characteristics.add(Characteristic.getCharacteristic(cid)));

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
            if (money > 0) {
                results.add(new MoneyQuestResult(money));
            }

            final Quest quest = new Quest(
                    key,
                    name,
                    description.split("\n"),
                    nextQuestId,
                    important,
                    results
            );
            add(quest);
            if (important) {
                importantQuests.add(quest);
            }
        });
        Quest.setImportantQuests(importantQuests);
    }

}
