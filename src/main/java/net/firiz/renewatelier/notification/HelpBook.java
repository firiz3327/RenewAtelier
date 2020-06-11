package net.firiz.renewatelier.notification;

import net.firiz.renewatelier.utils.TellrawUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class HelpBook {

    private HelpBook() {
    }

    public static void help(Player player) {
        final ItemStack book = Notification.createBook("help", "atelier", meta -> {
            final ComponentBuilder indexPage = new ComponentBuilder();
            indexPage.append("おしらせ\n\n")
                    .event(TellrawUtils.createClickEvent(ClickEvent.Action.CHANGE_PAGE, "2"))
                    .event(TellrawUtils.createHoverEvent("おしらせを表示"));
            indexPage.append("不具合の報告\n\n")
                    .event(TellrawUtils.createClickEvent(ClickEvent.Action.OPEN_URL, "https://www.google.com/"))
                    .event(TellrawUtils.createHoverEvent("公式サイト内の不具合報告用ページを開きます"));
            meta.spigot().setPages(
                    indexPage.create(),
                    Notification.createNotice()
            );
        });
        player.openBook(book);
    }

}
