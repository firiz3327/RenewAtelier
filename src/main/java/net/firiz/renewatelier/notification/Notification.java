package net.firiz.renewatelier.notification;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.version.nms.NMSEntityUtils;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.packet.NotificationPacket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author firiz
 */
public final class Notification {

    private Notification() {
    }

    @NotNull
    protected static ItemStack createBook(final String title, final String author, Consumer<BookMeta> supplier) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(title);
        meta.setAuthor(author);
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        supplier.accept(meta);
        book.setItemMeta(meta);
        return book;
    }

    public static void loginNotification(final Player player) {
        final ItemStack book = createBook("notification", "atelier", meta -> meta.spigot().setPages(createNotice()));
        Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> {
            if (player.isOnline()) {
                player.openBook(book);
            }
        }, 20); // 1 sec
    }

    protected static BaseComponent[] createNotice() {
        final ComponentBuilder builder = new ComponentBuilder("");
        builder.append("アップデート\n").color(ChatColor.GREEN);
        builder.append("オープンURL\n").event(
                TellrawUtils.createClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        "https://www.google.com/"
                )
        );
        builder.append("ホバーテキスト\n").reset().event(
                TellrawUtils.createHoverEvent("テキスト")
        ).append("appendをつなげて書けるよ\n").reset().event(
                TellrawUtils.createHoverEvent(new ItemStack(Material.ACACIA_BOAT))
        );
        return builder.create();
    }

    public static void recipeNotification(final Player player, final Material material) {
        recipeNotification(player, new ItemStack(material));
    }

    public static void recipeNotification(final Player player, final ItemStack item) {
        final String itemId = VersionUtils.asVItemCopy(item).getMinecraftId();
        NotificationPacket.sendRecipe(player, itemId, false); // add
        if (!NMSEntityUtils.hasRecipe(player, itemId)) {
            Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> NotificationPacket.sendRecipe(player, itemId, true), 2); // 0.1 sec
        }
    }

    @Deprecated
    public static void advancementNotification(final Player player, final String id) {
        NotificationPacket.sendAdvancement(player, id, false); // add

        // java.lang.IllegalArgumentException: advancement
        if (!player.getAdvancementProgress(
                Objects.requireNonNull(Bukkit.getAdvancement(new NamespacedKey(
                        AtelierPlugin.getPlugin(),
                        id.replace("minecraft:", "")
                )))
        ).isDone()) {
            Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> NotificationPacket.sendAdvancement(player, id, true), 2); // 0.1 sec
        }
    }

}
