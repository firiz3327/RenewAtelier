package net.firiz.renewatelier.item.tool;

import net.firiz.renewatelier.utils.Chore;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ToolDamage {

    private static final String DURABILITY = "§d§n§r§a§b§r§l§l§l§f§r§7耐久値: ";

//        final int[] damages = {
//                1, 61, 181, 301, 421,
//                541, 661, 781, 901, 1021,
//                1141, 1261, 1381, 1501
//        };

//    public static boolean hasDamagedItem(final ItemStack item) {
//        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
//            for (String lore : item.getItemMeta().getLore()) {
//                if (lore.startsWith(DURABILITY)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    public static int getDamage(final ItemStack item) {
        return getDamage(item.getItemMeta(), getMaxDurability(item));
    }

    private static int getDamage(final ItemMeta meta, final int maxDurability) {
        if (meta.hasLore()) {
            for (String lore : meta.getLore()) {
                if (lore.startsWith(DURABILITY)) {
                    return maxDurability - Integer.parseInt(lore.substring(DURABILITY.length(), lore.indexOf(" / ")));
                }
            }
        }
        return Chore.getDamage(meta);
    }

    public static int getDurability(final ItemStack item) {
        return getDurability(item.getItemMeta(), getMaxDurability(item));
    }

    private static int getDurability(final ItemMeta meta, final int maxDurability) {
        return maxDurability - getDamage(meta, maxDurability);
    }

    public static void setDamage(final ItemStack item, final int damage) {
        final int maxDurability = getMaxDurability(item);
        final int damageValue = maxDurability - damage;
        if (damage >= maxDurability) {
            Chore.setDamage(item, 10000);
            return;
        }
        final ItemMeta meta = item.getItemMeta();
        final List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        boolean first = true;
        for (int i = 0; i < lores.size(); i++) {
            final String lore = lores.get(i);
            if (lore.startsWith(DURABILITY)) {
                first = false;
                lores.set(i, DURABILITY + damageValue + " / " + maxDurability);
                break;
            }
        }
        if (first) {
            lores.add(DURABILITY + damageValue + " / " + maxDurability);
        }
        final int interval = maxDurability / 13;
        final int minInterval = interval / 2;
        setDamage:
        if (damageValue >= maxDurability) {
            Chore.setDamage(meta, 0);
        } else if (minInterval < damage && damage < minInterval + interval) {
            Chore.setDamage(meta, minInterval + 1);
        } else {
            for (int i = 12; i >= 1; i--) {
                if (minInterval + (interval * i) < damage) {
                    Chore.setDamage(meta, minInterval + (interval * i) + 1);
                    break setDamage;
                }
            }
            Chore.setDamage(meta, 1);
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
    }

    public static void setDurability(final ItemStack item, final int durability) {
        setDamage(item, getMaxDurability(item) - durability);
    }

    public static void damage(PlayerItemDamageEvent e) {
        setDamage(e.getItem(), getDamage(e.getItem()) + e.getDamage());
    }

    /**
     * アイテムの最大耐久値を返す
     * アイテムに独自耐久値が付与される事を想定して作成しておく
     *
     * @param item
     * @return アイテムの耐久値
     */
    public static int getMaxDurability(final ItemStack item) {
        return item.getType().getMaxDurability();
    }

}
