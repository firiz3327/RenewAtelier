package net.firiz.renewatelier.notification;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class HelpBook {

    private HelpBook() {
    }

    public static void help(Player player) {
        final ItemStack book = Notification.createBook(
                "help",
                "atelier",
                Component.text("おしらせ\n\n")
                        .clickEvent(ClickEvent.changePage(2))
                        .hoverEvent(HoverEvent.showText(Component.text("おしらせを表示")))
                        .append(
                                Component.text("不具合の報告\n\n")
                                        .clickEvent(ClickEvent.openUrl("https://www.firiz.net/"))
                                        .hoverEvent(Component.text("公式サイト内の不具合報告用ページを開きます"))
                        ),
                Notification.createNotice()
        );
        player.openBook(book);
    }

}
