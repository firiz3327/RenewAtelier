package net.firiz.renewatelier.quest.book;

import java.util.ArrayList;
import java.util.List;

import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.result.*;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * @author firiz
 */
public class QuestBook {

    private QuestBook() {
    }

    public static void openQuestBook(final Player player) {
        player.sendMessage(ChatColor.GRAY + "[クエストを更新中...]");

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

        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor(player.getName()); // require?
        meta.setTitle("クエストブック"); // require?
        meta.setGeneration(BookMeta.Generation.ORIGINAL); // require?
        meta.spigot().setPages(pages);
        book.setItemMeta(meta);

        // 本を開くパケット
        player.openBook(book);
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
        for (final QuestResult questResult : quest.getResults()) {
            questResult.appendQuestResult(player, builder);
            builder.append("\n").reset();
        }
        pages.add(builder.create());
    }

}
