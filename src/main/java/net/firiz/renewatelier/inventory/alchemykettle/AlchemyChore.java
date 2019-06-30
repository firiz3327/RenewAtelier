package net.firiz.renewatelier.inventory.alchemykettle;

import net.firiz.renewatelier.utils.Chore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AlchemyChore {

    protected static void setSetting(final ItemMeta meta, final int line, final int data, final String desc) {
        final List<String> lores = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        if (lores.size() > line) {
            lores.set(line, Chore.setIntColor(data) + desc);
        } else {
            while(true) {
                if(lores.size() >= line) {
                    lores.add(Chore.setIntColor(data) + desc);
                } else {
                    lores.add("");
                    continue;
                }
                break;
            }
        }
        meta.setLore(lores);
    }

    protected static int getSetting(final ItemMeta meta, final int line) {
        return Chore.getIntColor(meta.getLore().get(line));
    }

    protected static int getSetting(final ItemStack item, final int line) {
        return getSetting(item.getItemMeta(), line);
    }

    protected static void setSettingStr(final ItemMeta meta, final int line, final String data, final String desc) {
        final List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        if (lores.size() > line) {
            lores.set(line, Chore.createStridColor(data) + desc);
        } else {
            while(true) {
                if(lores.size() >= line) {
                    lores.add(Chore.createStridColor(data) + desc);
                } else {
                    lores.add("");
                    continue;
                }
                break;
            }
        }
        meta.setLore(lores);
    }

    protected static String getSettingStr(final ItemMeta meta, final int line) {
        final String lore = meta.getLore().get(line);
        return Chore.getStridColor(lore.substring(0, lore.lastIndexOf(ChatColor.RESET.toString())));
    }

    protected static String getSettingStr(final ItemStack item, final int line) {
        return getSettingStr(item.getItemMeta(), line);
    }

}
