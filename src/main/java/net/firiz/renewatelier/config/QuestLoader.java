package net.firiz.renewatelier.config;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.quest.result.*;

import java.io.File;
import java.util.List;
import java.util.Objects;

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestItem;
import net.kyori.adventure.text.Component;
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
        final List<Quest> importantQuests = new ObjectArrayList<>();
        config.getKeys(false).forEach(key -> {
            final ConfigurationSection item = config.getConfigurationSection(key);
            assert item != null;
            // クエスト名
            final Component questName = Text.translateColor(item.getString("name"));
            // 詳細説明
            final String description = item.getString("description");
            // 次のクエストID
            final String nextQuestId = item.contains("nextQuestId") ? item.getString("nextQuestId") : null;
            // 重要
            final boolean important = item.contains("important") && item.getBoolean("important");

            // 報酬
            final List<QuestResult> results = new ObjectArrayList<>();
            final ConfigurationSection result = item.getConfigurationSection("result");
            // 報酬 - レシピ
            assert result != null;
            final List<String> recipeIds = result.contains("recipes") ? result.getStringList("recipes") : null;
            if (recipeIds != null) {
                recipeIds.forEach(recipeId -> results.add(new RecipeQuestResult(AlchemyRecipe.search(recipeId))));
            }
            // 報酬 - アイテム - 錬金素材以外想定しない
            if (result.contains("items")) {
                final ConfigurationSection items = result.getConfigurationSection("items");
                assert items != null;
                items.getKeys(false).stream().map(items::getConfigurationSection).filter(Objects::nonNull).forEachOrdered(isec -> {
                    final String material = isec.getString("material");
                    final Component itemName = isec.contains("name") ? Text.translateColor(isec.getString("name")) : null;
                    final int amount = isec.contains("amount") ? isec.getInt("amount") : 1;
                    final int quality = isec.contains("quality") ? isec.getInt("quality") : -1;
                    // 使用回数の概念が存在しないので現在まだ使用不可
                    final int usecount = isec.contains("usecount") ? isec.getInt("usecount") : -1;

                    // 錬金成分
                    final List<String> ingredientStrings = isec.contains("ingredients") ? isec.getStringList("ingredients") : null;
                    List<AlchemyIngredients> ingredients = null;
                    if (ingredientStrings != null) {
                        ingredients = new ObjectArrayList<>();
                        for (final String iid : ingredientStrings) {
                            ingredients.add(AlchemyIngredients.valueOf(iid));
                        }
                    }
                    // 特性
                    final List<String> characteristicStrings = isec.contains("characteristics") ? isec.getStringList("characteristics") : new ObjectArrayList<>();
                    final List<Characteristic> characteristics = new ObjectArrayList<>();
                    characteristicStrings.forEach(cid -> characteristics.add(Characteristic.getCharacteristic(cid)));

                    assert material != null;
                    results.add(new ItemQuestResult(new QuestItem(
                            AlchemyMaterial.getMaterial(material),
                            itemName,
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
                    questName,
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
