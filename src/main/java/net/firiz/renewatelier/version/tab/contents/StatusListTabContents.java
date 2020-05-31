package net.firiz.renewatelier.version.tab.contents;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.version.tab.TabListItem;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class StatusListTabContents implements TabContents {

    @Override
    public void update(int slot, TabListItem item, Char player) {
        final CharStats stats = player.getCharStats();
        switch (slot) {
            case 1:
                final Player bukkitPlayer = player.getCharStats().getPlayer();
                final EntityPlayer entityPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
                item.modifyListName(entityPlayer.getName());
                item.modifyPing(entityPlayer.ping);
                item.modifyGameMode(bukkitPlayer.getGameMode());
                item.modifyTextures(entityPlayer.getProfile());
                break;
            case 2:
                item.modifyListName(whiteSpace("所持金:", player.getMoney() + " "));
                break;
            case 4:
                item.modifyListName(whiteSpace("錬金LV:", stats.getAlchemyLevel() + " "));
                break;
            case 5:
                item.modifyListName(whiteSpace("レベル:", stats.getLevel() + " "));
                break;
            case 6:
                item.modifyListName(whiteSpace("攻撃力:", stats.getAtk() + " "));
                break;
            case 7:
                item.modifyListName(whiteSpace("防御力:", stats.getDef() + " "));
                break;
            case 8:
                item.modifyListName(whiteSpace("素早さ:", stats.getSpeed() + " "));
                break;
            default:
                break;
        }
    }

    public String whiteSpace(String prefix, String suffix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(" ");
        }
        sb.replace(0, prefix.length(), prefix);
        sb.replace(sb.length() - suffix.length(), sb.length(), suffix);
        return sb.toString();
    }

    @Override
    public boolean isUpdater() {
        return true;
    }

}
