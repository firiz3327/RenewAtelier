package net.firiz.renewatelier.item.tool;

import net.firiz.renewatelier.utils.Chore;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ToolDamage {

    private static final String DURABILITY = "§d§n§r§a§b§r§l§l§l§f§r耐久値: ";

//        final int[] damages = {
//                1, 61, 181, 301, 421,
//                541, 661, 781, 901, 1021,
//                1141, 1261, 1381, 1501
//        };

    public static boolean hasDamagedItem(final ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String lore : item.getItemMeta().getLore()) {
                if (lore.startsWith(DURABILITY)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getDamage(final ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String lore : item.getItemMeta().getLore()) {
                if (lore.startsWith(DURABILITY)) {
                    return item.getType().getMaxDurability() - Integer.parseInt(lore.substring(DURABILITY.length(), lore.indexOf(" / ")));
                }
            }
        }
        return Chore.getDamage(item);
    }

    public static void damage(PlayerItemDamageEvent e) {
        e.setDamage(damage(e.getDamage(), e.getItem(), true));
    }

    public static int damage(int damage, ItemStack item, boolean replaceDurability) {
        final int maxDurability = item.getType().getMaxDurability();
        final ItemMeta meta = item.getItemMeta();
        final List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        boolean first = true;
        int damageValue = (maxDurability - Chore.getDamage(meta));
        for (int i = 0; i < lores.size(); i++) {
            final String lore = lores.get(i);
            if (lore.startsWith(DURABILITY)) {
                first = false;
                if (replaceDurability) {
                    damageValue = Integer.parseInt(lore.substring(DURABILITY.length(), lore.indexOf(" / "))) - damage;
                    if (damageValue < 0) {
                        return 10000;
                    }
                }
                lores.set(i, DURABILITY + damageValue + " / " + maxDurability);
                break;
            }
        }
        if (first) {
            lores.add(DURABILITY + damageValue + " / " + maxDurability);
            Chore.setDamage(meta, 1);
        }

        final int nowDurability = maxDurability - damageValue;
        final int interval = maxDurability / 13;
        final int minInterval = interval / 2;
        if (minInterval < nowDurability && nowDurability < minInterval + interval) {
            Chore.setDamage(meta, minInterval + 1);
        } else {
            for (int i = 12; i >= 1; i--) {
                if (minInterval + (interval * i) < nowDurability) {
                    Chore.setDamage(meta, minInterval + (interval * i) + 1);
                    break;
                }
            }
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return 0;
    }

}
