package net.firiz.renewatelier.quest.book;

import java.util.List;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.result.*;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LecternInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public final class QuestBook {

    private QuestBook() {
    }

    @Nullable
    public static AlchemyMaterial getQuestBookMaterial(@Nullable final ItemStack item) {
        final Optional<AlchemyMaterial> material = AlchemyItemStatus.getMaterialOptional(item);
        if (material.isPresent() && material.get().getId().equalsIgnoreCase("quest_book")) {
            return material.get();
        }
        return null;
    }

    public static boolean lectern(@NotNull final Cancellable e, @NotNull final Player player, @NotNull final Block block, @Nullable final ItemStack item) {
        final Lectern lectern = (Lectern) block.getState();
        final LecternInventory inv = (LecternInventory) lectern.getInventory();
        final ItemStack book = inv.getBook();
        final AlchemyMaterial bookMaterial = getQuestBookMaterial(book);
//        if (player.isSneaking()) { // takeBook代わりに作ってみたけどhasBookが更新されないから微妙か？
//            e.setCancelled(true);
//            lectern.getSnapshotInventory().setItem(0, new ItemStack(Material.AIR));
//            lectern.update();
//            ItemUtils.addItem(player, book);
//            return true;
//        }
        if (bookMaterial != null) {
            assert book != null;
            e.setCancelled(true);
            openQuestBook(player); // takeBookができない代わりに、プレイヤー間で競合しない
//            final BookMeta meta = (BookMeta) book.getItemMeta();
//            changeMeta(player, meta);
//            book.setItemMeta(meta);
            return true;
        }
        return getQuestBookMaterial(item) != null;
    }

    public static void openQuestBook(@NotNull final Player player) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();
        changeMeta(player, meta);
        book.setItemMeta(meta);
        player.openBook(book);
    }

    public static void changeMeta(@NotNull final Player player, @NotNull final BookMeta meta) {
        player.sendMessage(ChatColor.GRAY + "[クエストを更新中...]");
        meta.setAuthor(player.getName()); // require?
        meta.setTitle("クエストブック"); // require?
        meta.setGeneration(BookMeta.Generation.ORIGINAL); // require?
        meta.spigot().setPages(createPages(player));
    }

    private static List<BaseComponent[]> createPages(@NotNull final Player player) {
        final Char status = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId());
        final List<Quest> progressQuests = new ObjectArrayList<>();
        final List<Quest> clearQuests = new ObjectArrayList<>();
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
        final List<BaseComponent[]> pages = new ObjectArrayList<>();
        // 進行中クエスト
        progressQuests.forEach(quest -> addSpigotPage(pages, quest, 0, player));
        // 重要クエスト
        importantQuests.forEach(quest -> addSpigotPage(pages, quest, 2, player));
        // クリア済みクエスト
        clearQuests.forEach(quest -> addSpigotPage(pages, quest, 1, player));
        return pages;
    }

    private static void addSpigotPage(@NotNull final List<BaseComponent[]> pages, @NotNull final Quest quest, final int type, @NotNull final Player player) {
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
