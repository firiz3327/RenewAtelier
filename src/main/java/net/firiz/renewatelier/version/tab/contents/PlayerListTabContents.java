package net.firiz.renewatelier.version.tab.contents;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.version.tab.TabListItem;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerListTabContents implements TabContents {

    @Override
    public void update(int slot, TabListItem item, Char player) {
        final List<Player> onlinePlayers = new ObjectArrayList<>(Bukkit.getOnlinePlayers());
        if (slot < onlinePlayers.size()) {
            final Player slotPlayer = onlinePlayers.get(slot);
            final EntityPlayer slotEntityPlayer = ((CraftPlayer) slotPlayer).getHandle();
            item.modifyListName(slotEntityPlayer.getName());
            item.modifyPing(slotEntityPlayer.ping);
            item.modifyGameMode(slotPlayer.getGameMode());
            item.modifyTextures(slotEntityPlayer.getProfile());
        } else {
            item.resetTabItem();
        }
    }

    @Override
    public boolean isUpdater() {
        return false;
    }
}
