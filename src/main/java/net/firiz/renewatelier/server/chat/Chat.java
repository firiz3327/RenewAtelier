package net.firiz.renewatelier.server.chat;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.server.discord.DiscordManager;
import net.firiz.renewatelier.server.discord.DiscordStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author firiz
 */
public final class Chat {

    private Chat() {
    }

    private static final PlayerSaveManager playerSaveManager = PlayerSaveManager.INSTANCE;
    private static final Pattern itemPattern = Pattern.compile("(&item)");
    private static final TextReplacementConfig config;
    private static final String[] words = {
            "死ね", "しね", "シネ", "sine", "shine",
            "死ねよ", "しねよ", "シネヨ", "sineyo", "shineyo",
            "かす", "カス", "kasu",
            "ちんこ", "チンコ", "tinko",
            "ちんぽ", "チンポ", "tinpo",
            "まんこ", "マンコ", "manko",
            "おっぱい", "oppai",
            "せっくす", "せっくす", "sekkusu", "sex",
            "fuck", "fuck you", "fk", "fuk", "fuckyou"
    };
    private static final Component warningMessage1 = Component.text("少し時間をおいてチャットを行ってみてください。").color(NamedTextColor.YELLOW);
    private static final Component warningMessage2 = Component.text("同じ発言の連投はできません。").color(NamedTextColor.YELLOW);
    private static final DiscordManager discord = DiscordManager.INSTANCE;

    static {
        final StringBuilder sb = new StringBuilder();
        for (final String word : words) {
            if (!sb.isEmpty()) {
                sb.append("|");
            }
            sb.append(word);
        }
        config = TextReplacementConfig.builder().match(Pattern.compile(sb.toString())).replacement(Component.text("***")).build();
    }

    public static void chat(final Player player, final Component message) {
        final Char character = playerSaveManager.getChar(player);
        if (character.getChatCount() < 3 && (character.getChatBanTime() == 0 || System.currentTimeMillis() - character.getChatBanTime() > 5000)) {
            if (!Objects.equals(character.getLastChatComponent(), message)) {
                character.incrementChatCount();
                character.setLastChatComponent(message);
                final Text text = new Text();
                final Component replacedComponent = replace(player, message);
                final DiscordStatus status = discord.getDiscordStatus(player.getUniqueId());
                if (status != null && status.getDiscordId() != 0) {
                    discord.findMember(status.getDiscordId(), member -> {
                        Component name;
                        if (member == null) {
                            name = player.displayName();
                        } else {
                            name = Text.of(member.getEffectiveName() + "(" + Text.plain(player.displayName()) + ")");
                        }
                        chat(player, text, name, replacedComponent);
                    });
                    return;
                }
                chat(player, text, player.displayName(), replacedComponent);
            } else {
                player.sendMessage(warningMessage2);
            }
        } else {
            character.chatBan();
            player.sendMessage(warningMessage1);
        }
    }

    private static void chat(Player player, Text text, Component name, Component replacedComponent) {
        text.append("<").append(name).color(C.AQUA).clickEvent(ClickEvent.suggestCommand(
                "/tell " + player.getName() + " "
        )).hoverEvent(
                HoverEvent.showText(Component.text().append(player.displayName(), Component.text(" さんにメッセージを送信する")))
        ).append("> ");
        sendPlayers(text.append(replacedComponent));
        discord.sendMessage(player, Text.plain(replacedComponent));
    }

    public static void discordChat(final String name, final Component message) {
        sendPlayers(Text.of("<").append(name).color(C.AQUA).append("⒬> ").append(replace(null, message)));
    }

    public static void tell(final Player sender, final String[] args) {
        if (args.length > 2) {
            final Optional<Char> target = playerSaveManager.searchChar(args[1]);
            if (target.isPresent()) {
                target.ifPresent(player -> tell(sender, player, Component.text(args[2])));
            }
        }
    }

    public static void tell(final Player sender, final Char target, final Component message) {
        final Component msg = replace(sender, message);
        sender.sendMessage(createTellPlayerName(sender, target.getPlayer(), true).append(msg));
        if (target.getSettings().isShowPlayerChat()) {
            target.getPlayer().sendMessage(createTellPlayerName(sender, target.getPlayer(), false).append(msg));
        }
    }

    private static Component replace(final Player player, final Component message) {
        Component msg = message;
        if (player != null) {
            final ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.AIR) {
                msg = message.replaceText(
                        TextReplacementConfig.builder().match(itemPattern).replacement(item.displayName().hoverEvent(item.asHoverEvent())).build()
                );
            }
        }
        return msg.replaceText(config);
    }

    private static Text createTellPlayerName(final Player sender, final Player target, boolean senderMessage) {
        final Text text;
        if (senderMessage) {
            text = new Text(target.getName()).append(" にささやきました ").color(C.FLAT_LIGHT_GREEN1);
        } else {
            final HoverEvent<Component> hoverEvent = HoverEvent.showText(Component.text().append(sender.displayName(), Component.text(" さんにメッセージを返信する")));
            final ClickEvent clickEvent = ClickEvent.suggestCommand("/tell " + sender.getName() + " ");
            text = new Text(sender.getName()).hoverEvent(hoverEvent).clickEvent(clickEvent)
                    .append(" にささやかれました ").color(C.FLAT_LIGHT_GREEN1).hoverEvent(hoverEvent).clickEvent(clickEvent);
        }
        return text;
    }

    private static void sendPlayers(final Component component) {
        PlayerSaveManager.INSTANCE.getChars().stream()
                .filter(player -> player.getSettings().isShowPlayerChat())
                .forEach(player -> player.getPlayer().sendMessage(component));
    }

}
