package net.firiz.renewatelier.megaphone;

import net.firiz.renewatelier.utils.TellrawUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author firiz
 */
public class Megaphone {

    private Megaphone() {
    }

    public static void megaPhone(final Player player, final String text) {
        final ComponentBuilder builder = new ComponentBuilder("");
        init(player, builder);
        builder.append(text);
        sendPlayers(builder.create());
    }

    public static void itemMegaPhone(final Player player, final String text, final ItemStack item) {
        final ComponentBuilder builder = new ComponentBuilder("");
        init(player, builder);

        String temp = text;
        for (int i = 0; i < 10; i++) {
            if (temp.contains("%item%")) {
                final int index = temp.indexOf("%item%");
                builder.append(temp.substring(0, index));
                builder.append("【");
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    builder.append(item.getItemMeta().getDisplayName()).color(ChatColor.AQUA);
                } else {
                    builder.append(LanguageItemUtil.getLocalizeName(item)).color(ChatColor.AQUA);
                }
                builder.event(TellrawUtils.createHoverEvent(item));
                builder.append("】").reset();
                temp = temp.substring(index + 6);
            }
        }
        builder.append(temp);
        sendPlayers(builder.create());
    }

    private static void init(final Player player, final ComponentBuilder builder) {
        builder.append("<").append(player.getDisplayName()).color(ChatColor.AQUA).event(
                TellrawUtils.createClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "/tell " + player.getDisplayName() + " "
                )
        ).event(
                TellrawUtils.createHoverEvent(player.getDisplayName() + " さんにメッセージを送信する")
        ).append("> ").reset();
    }

    private static void sendPlayers(final BaseComponent[] components) {
        Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(p -> p.spigot().sendMessage(components)));
    }

}
