package net.firiz.renewatelier.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public final class ServerListener implements Listener {

    @EventHandler
    private void listPing(ServerListPingEvent e) {
        e.setMotd(ChatColor.RED + " lol ");
    }

}
