package net.firiz.renewatelier.notification;

import net.firiz.ateliercommonapi.nms.item.NMSItemStack;
import net.firiz.ateliercommonapi.nms.packet.EntityPacket;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.version.nms.NMSEntityUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public final class Notification {

    private Notification() {
    }

    @NotNull
    protected static ItemStack createBook(final String title, final String author, Component... text) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(title);
        meta.setAuthor(author);
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        meta.addPages(text);
        book.setItemMeta(meta);
        return book;
    }

    public static void loginNotification(final Player player) {
        final ItemStack book = createBook("notification", "atelier", createNotice());
        Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> {
            if (player.isOnline()) {
                player.openBook(book);
            }
        }, 20); // 1 sec
    }

    protected static Component createNotice() {
        return Component.text("アップデート\n").color(NamedTextColor.GREEN)
                .append(Component.text("オープンURL\n").clickEvent(ClickEvent.openUrl("https://www.firiz.net/")))
                .append(Component.text("ホバーテキスト\n").hoverEvent(HoverEvent.showText(Component.text("あいうえお"))))
                .append(Component.text("ホバーアイテム\n").hoverEvent(new ItemStack(Material.ACACIA_BOAT).asHoverEvent()))
                .append(Component.text("ホバーエンティティ\n").hoverEvent(Bukkit.getOnlinePlayers().stream().findFirst().get().asHoverEvent()));
    }

    public static void recipeNotification(final Player player, final Material material) {
        recipeNotification(player, new ItemStack(material));
    }

    public static void recipeNotification(final Player player, final ItemStack item) {
        final String itemId = NMSItemStack.getMinecraftId(item);
        PacketUtils.sendPacket(player, EntityPacket.recipePacket(itemId, false)); // add
        if (!NMSEntityUtils.hasRecipe(player, itemId)) {
            Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> PacketUtils.sendPacket(player, EntityPacket.recipePacket(itemId, true)), 2); // 0.1 sec
        }
    }

}
