package net.firiz.renewatelier.utils;

import net.firiz.renewatelier.version.VersionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class TellrawUtils {

    private TellrawUtils() {
    }

    public static ClickEvent createClickEvent(final ClickEvent.Action action, final String value) {
        return new ClickEvent(
                action,
                value
        );
    }

    public static HoverEvent createHoverEvent(final String text) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(text).create()
        );
    }

    public static HoverEvent createHoverEvent(final ItemStack item) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                parseItemComponents(item)
        );
    }

    private static BaseComponent[] parseItemComponents(final ItemStack item) {
        return new BaseComponent[]{
            new TextComponent(VersionUtils.asVItemCopy(item).getMinecraftJson())
        };
    }
}
