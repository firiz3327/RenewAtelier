package net.firiz.renewatelier.server.chat.command;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.server.discord.DiscordManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof final Player player && strings.length >= 1) {
            final Char character = PlayerSaveManager.INSTANCE.getChar(player);
            switch (strings[0]) {
                case "token":
                    if (character.getDiscordStatus().getDiscordId() == 0) {
                        DiscordManager.INSTANCE.sendDiscordToken(player);
                    } else {
                        player.sendMessage("既にDiscordアカウントが紐付けされています");
                    }
                    break;
            }
            return true;
        }
        commandSender.sendMessage("player only.");
        return false;
    }
}
