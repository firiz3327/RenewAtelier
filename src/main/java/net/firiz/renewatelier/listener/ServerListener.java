package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.notification.HelpBook;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerListPingEvent;

public final class ServerListener implements Listener {

    @EventHandler
    private void listPing(ServerListPingEvent e) {
        e.setMotd(ChatColor.RED + " lol ");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("/help")) {
            e.setCancelled(true);
            HelpBook.help(e.getPlayer());
        }
    }

}
