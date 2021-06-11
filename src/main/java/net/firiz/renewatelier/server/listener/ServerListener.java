package net.firiz.renewatelier.server.listener;

import net.firiz.renewatelier.server.chat.Chat;
import net.firiz.renewatelier.notification.HelpBook;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.LootGenerateEvent;

public final class ServerListener implements Listener {

    @EventHandler
    private void listPing(ServerListPingEvent e) {
        e.motd(Component.text("lol").color(NamedTextColor.RED));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void commandPreprocess(PlayerCommandPreprocessEvent e) {
        final String[] args = e.getMessage().split(" ");
        switch (args[0].toLowerCase()) {
            case "/help":
                e.setCancelled(true);
                HelpBook.help(e.getPlayer());
                break;
            case "/msg", "/tell", "/w":
                e.setCancelled(true);
                Chat.tell(e.getPlayer(), args);
                break;
            case "/trigger":
                if (!e.getPlayer().isOp()) {
                    e.setCancelled(true);
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    private void lootGenerate(LootGenerateEvent e) {
        e.setLoot(ReplaceVanillaItems.loot(e.getLoot()));
    }

}
