/*
 * QuestBook.java
 *
 * Copyright (c) 2019 firiz.
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
package net.firiz.renewatelier.quest.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestItem;
import net.firiz.renewatelier.quest.result.ItemQuestResult;
import net.firiz.renewatelier.quest.result.MoneyQuestResult;
import net.firiz.renewatelier.quest.result.QuestResult;
import net.firiz.renewatelier.quest.result.RecipeQuestResult;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author firiz
 */
public class QuestBook {

    private QuestBook() {
    }

    public static void openQuestBook(final Player player, final ItemStack book) {
        final Char status = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId());
        final List<Quest> progressQuests = new ArrayList<>();
        final List<Quest> clearQuests = new ArrayList<>();
        final List<Quest> importantQuests = Quest.getImportantQuests();
        status.getQuestStatusList().forEach(qs -> {
            final Quest quest = Quest.getQuest(qs.getId());
            importantQuests.remove(quest);

            if (qs.isClear()) {
                clearQuests.add(quest);
            } else {
                progressQuests.add(quest);
            }
        });

        final List<BaseComponent[]> pages = new ArrayList<>();
        // 進行中クエスト
        progressQuests.forEach(quest -> addSpigotPage(pages, quest, 0, player));
        // 重要クエスト
        importantQuests.forEach(quest -> addSpigotPage(pages, quest, 2, player));
        // クリア済みクエスト
        clearQuests.forEach(quest -> addSpigotPage(pages, quest, 1, player));

//        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();
        if (!meta.hasAuthor()) {
            meta.setAuthor(player.getName());
        }
        if (!meta.hasTitle()) {
            meta.setTitle("クエストブック");
        }
        if (!meta.hasGeneration()) {
            meta.setGeneration(BookMeta.Generation.ORIGINAL);
        }
        meta.spigot().setPages(pages);
        book.setItemMeta(meta);

        System.out.println("opened quest_book");
        // 本を開くパケット
        //Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> PayloadPacket.openBook(player, hand), 5); // 0.25 sec
        Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> player.openBook(book), 5);
    }

    private static void addSpigotPage(final List<BaseComponent[]> pages, final Quest quest, final int type, final Player player) {
        final String flag_text;
        final ChatColor color;
        switch (type) {
            case 0:
                flag_text = "進行中";
                color = ChatColor.BLUE;
                break;
            case 1:
                flag_text = "クリア済み";
                color = ChatColor.GREEN;
                break;
            default:
                flag_text = "未受注";
                color = ChatColor.RED;
                break;
        }
        final ComponentBuilder builder = new ComponentBuilder("【")
                .append(quest.getName())
                .color(color)
                .event(TellrawUtils.createHoverEvent(flag_text))
                .append("】\n\n")
                .reset();
        for (final String line : quest.getDescription()) {
            builder.append(line + "\n");
        }
        builder.append("\n【報酬】\n");
        for (final QuestResult qr : quest.getResults()) {
            if (qr instanceof RecipeQuestResult) {
                final RecipeQuestResult result = (RecipeQuestResult) qr;
                final AlchemyRecipe recipe = result.getResult();
                final String result_str = recipe.getResult();
                builder.append("レシピ: ");

                final List<ItemFlag> flags = new ArrayList<>();
                String name;
                Material material;
                final int cmd;
                if (result_str.startsWith("material:")) {
                    final AlchemyMaterial am = AlchemyMaterial.getMaterial(result_str.substring(9));
                    name = am.getName();
                    material = am.getMaterial().getLeft();
                    cmd = am.getMaterial().getRight();
                    if (am.isHideAttribute()) {
                        flags.add(ItemFlag.HIDE_ATTRIBUTES);
                    }
                    if (am.isHideDestroy()) {
                        flags.add(ItemFlag.HIDE_DESTROYS);
                    }
                    if (am.isHideEnchant()) {
                        flags.add(ItemFlag.HIDE_ENCHANTS);
                    }
                    if (am.isHidePlacedOn()) {
                        flags.add(ItemFlag.HIDE_PLACED_ON);
                    }
                    if (am.isHidePotionEffect()) {
                        flags.add(ItemFlag.HIDE_POTION_EFFECTS);
                    }
                    if (am.isHideUnbreaking()) {
                        flags.add(ItemFlag.HIDE_UNBREAKABLE);
                    }
                } else if (result_str.startsWith("minecraft:")) { // 基本想定しない
                    material = Material.matchMaterial(result_str);
                    if (material == null) {
                        material = Material.matchMaterial(result_str, true);
                    }
                    name = null;
                    cmd = 0;
                } else {
                    continue;
                }
                final ItemStack viewItem = Chore.createCustomModelItem(material, 1, cmd);
                final ItemMeta viewMeta = Objects.requireNonNull(viewItem.getItemMeta());
                if (name != null) {
                    viewMeta.setDisplayName(name);
                } else {
                    name = LanguageItemUtil.getLocalizeName(viewItem, player);
                }
                if (!flags.isEmpty()) {
                    viewMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
                }
                final List<String> viewLore = new ArrayList<>();
                viewLore.add(ChatColor.GRAY + "作成量: " + ChatColor.RESET + recipe.getAmount());
                viewLore.add(ChatColor.GRAY + "必要素材:");
                for (final String req : recipe.getReqMaterial()) {
                    final String[] data = req.split(",");
                    if (data[0].startsWith("category:")) {
                        viewLore.add(AlchemyItemStatus.Type.CATEGORY.getCheck() + ChatColor.RESET + "- " + ChatColor.stripColor(Category.valueOf(data[0].substring(9)).getName()) + " × " + data[1]);
                    } else if (data[0].startsWith("material:")) {
                        viewLore.add(AlchemyItemStatus.Type.MATERIAL.getCheck() + ChatColor.RESET + "- " + ChatColor.stripColor(AlchemyMaterial.getMaterial(data[0].substring(9)).getName()) + " × " + data[1]);
                    }
                }
                viewMeta.setLore(viewLore);
                viewItem.setItemMeta(viewMeta);
                builder.append(name).event(
                        TellrawUtils.createHoverEvent(viewItem)
                );
            } else if (qr instanceof ItemQuestResult) {
                final ItemQuestResult result = (ItemQuestResult) qr;
                final QuestItem questItem = result.getResult();
                final ItemStack viewItem = questItem.getItem(new AlchemyItemStatus.VisibleFlags(
                        false, // id
                        true, // quality - default min
                        (questItem.getIngredients() != null), // ings
                        false, // size
                        false, // catalyst
                        false, // category
                        false,
                        true
                ));
                final String name = viewItem.hasItemMeta() && viewItem.getItemMeta().hasDisplayName()
                        ? viewItem.getItemMeta().getDisplayName()
                        : LanguageItemUtil.getLocalizeName(viewItem, player);
                builder.append("アイテム: ").append(
                        questItem.getName() == null ? name : questItem.getName()
                ).event(TellrawUtils.createHoverEvent(viewItem));
            } else if (qr instanceof MoneyQuestResult) {
                final MoneyQuestResult result = (MoneyQuestResult) qr;
                final int money = result.getResult();
                builder.append("エメラルド: x" + money);
            }
            builder.append("\n").reset();
        }
        pages.add(builder.create());
    }

}
