package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.notification.HelpBook;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.LootGenerateEvent;

public final class ServerListener implements Listener {

    @EventHandler
    private void listPing(ServerListPingEvent e) {
        e.setMotd(ChatColor.RED + " lol ");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void commandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("/help")) {
            e.setCancelled(true);
            HelpBook.help(e.getPlayer());
        }
    }

    @EventHandler
    private void lootGenerate(LootGenerateEvent e) {
        e.setLoot(ReplaceVanillaItems.loot(e.getLoot()));
    }

}
