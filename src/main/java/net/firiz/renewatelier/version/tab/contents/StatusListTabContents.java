package net.firiz.renewatelier.version.tab.contents;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.tab.TabListItem;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
                item.modifyListName(whiteSpace("所持金:", formatMoney(player.getMoney())));
                break;
            case 4:
                item.modifyListName(whiteSpace("錬金LV:", stats.getAlchemyLevel() + " "));
                break;
            case 5:
                item.modifyListName(whiteSpace("レベル:", stats.getLevel() + " "));
                break;
            case 6:
                item.modifyListName(whiteSpace("HP:", ((int) stats.getHp()) + "/" + stats.getMaxHp()));
                break;
            case 7:
                item.modifyListName(whiteSpace("MP:", stats.getMp() + "/" + stats.getMaxMp()));
                break;
            case 8:
                item.modifyListName(whiteSpace("攻撃力:", stats.getAtk() + " "));
                break;
            case 9:
                item.modifyListName(whiteSpace("防御力:", stats.getDef() + " "));
                break;
            case 10:
                item.modifyListName(whiteSpace("素早さ:", stats.getSpeed() + " "));
                break;
            case 17:
                item.modifyListName(" 詳細は /check");
                break;
            default:
                break;
        }
    }

    private String formatMoney(long money) {
        int divideValue;
        String unit;
        if (money >= 1000000000) {
            divideValue = 1000000000;
            unit = "B";
        } else if (money >= 1000000) {
            divideValue = 1000000;
            unit = "M";
        } else {
            return CommonUtils.comma(money);
        }
        final BigDecimal decimal = BigDecimal.valueOf(money)
                .divide(BigDecimal.valueOf(divideValue), 2, RoundingMode.DOWN);
        return decimal.stripTrailingZeros().toPlainString() + unit;
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
