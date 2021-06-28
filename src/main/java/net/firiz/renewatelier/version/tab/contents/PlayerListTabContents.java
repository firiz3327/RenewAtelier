package net.firiz.renewatelier.version.tab.contents;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.nms.MinecraftConverter;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.version.tab.TabListItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerListTabContents implements TabContents {

    @Override
    public void update(int slot, TabListItem item, Char player) {
        final List<Player> onlinePlayers = new ObjectArrayList<>(Bukkit.getOnlinePlayers());
        if (slot < onlinePlayers.size()) {
            final Player slotPlayer = onlinePlayers.get(slot);
            final var slotEntityPlayer = MinecraftConverter.convert(slotPlayer);
            item.modifyListName(slotEntityPlayer.displayName());
            item.modifyPing(slotEntityPlayer.ping());
            item.modifyGameMode(slotPlayer.getGameMode());
            item.modifyTextures(slotEntityPlayer.profile());
        } else {
            item.resetTabItem();
        }
    }

    @Override
    public boolean isUpdater() {
        return false;
    }
}
