package net.firiz.renewatelier.inventory.alchemykettle;

import net.firiz.renewatelier.utils.Chore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlchemyChore {

    private AlchemyChore() {
    }

    static void setSetting(final ItemMeta meta, final int line, final int data, final String desc) {
        final List<String> lore = meta.hasLore() ? new ArrayList<>(Objects.requireNonNull(meta.getLore())) : new ArrayList<>();
        if (lore.size() > line) {
            lore.set(line, Chore.setIntColor(data) + desc);
        } else while (true) {
            if (lore.size() >= line) {
                lore.add(Chore.setIntColor(data) + desc);
                break;
            }
            lore.add("");
        }
        meta.setLore(lore);
    }

    static int getSetting(final ItemMeta meta, final int line) {
        return Chore.getIntColor(Objects.requireNonNull(meta.getLore()).get(line));
    }

    static int getSetting(final ItemStack item, final int line) {
        return getSetting(Objects.requireNonNull(item.getItemMeta()), line);
    }

    static void setSettingStr(final ItemMeta meta, final int line, final String data, final String desc) {
        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        assert lore != null;
        if (lore.size() > line) {
            lore.set(line, Chore.createStridColor(data) + desc);
        } else while (true) {
            if (lore.size() >= line) {
                lore.add(Chore.createStridColor(data) + desc);
                break;
            }
            lore.add("");
        }
        meta.setLore(lore);
    }

    static String getSettingStr(final ItemMeta meta, final int line) {
        final String lore = Objects.requireNonNull(meta.getLore()).get(line);
        return Chore.getStridColor(lore.substring(0, lore.lastIndexOf(ChatColor.RESET.toString())));
    }

    protected static String getSettingStr(final ItemStack item, final int line) {
        return getSettingStr(Objects.requireNonNull(item.getItemMeta()), line);
    }

}
