/*
 * Chore.java
 * 
 * Copyright (c) 2018 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package jp.gr.java_conf.zakuramomiji.renewatelier.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyAttribute;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterialManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Category;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author firiz
 */
public final class Chore {
    
    public static ItemStack createDamageableItem(final Material material, int amount, int damage) {
        final ItemStack item = new ItemStack(material, amount);
        setDamage(item, damage);
        return item;
    }
    
    private static void setDamage(final ItemStack item, final int damage) {
        final ItemMeta meta = item.getItemMeta();
        setDamage(meta, damage);
        item.setItemMeta(meta);
    }

    public static void setDamage(final ItemMeta meta, final int damage) {
        ((Damageable) meta).setDamage(damage);
    }

    public static int getDamage(final ItemStack item) {
        return getDamage(item.getItemMeta());
    }

    public static int getDamage(final ItemMeta meta) {
        return ((Damageable) meta).getDamage();
    }

    public static int[] parseInts(final List<Integer> list) {
        final int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static int colorCint(String str) {
        if (str == null) {
            return 0;
        }

        if (!str.contains("§")) {
            str = "§".concat(str);
        }
        if (str.equals(ChatColor.GRAY.toString())) {
            return 0;
        } else if (str.equals(ChatColor.WHITE.toString())) {
            return 1;
        } else if (str.equals(AlchemyAttribute.RED.getColor())) {
            return 2;
        } else if (str.equals(AlchemyAttribute.BLUE.getColor())) {
            return 3;
        } else if (str.equals(AlchemyAttribute.GREEN.getColor())) {
            return 4;
        } else if (str.equals(AlchemyAttribute.YELLOW.getColor())) {
            return 5;
        } else if (str.equals(AlchemyAttribute.PURPLE.getColor())) {
            return 6;
        }
        return 0;
    }

    public static String intCcolor(int i) {
        switch (i) {
            case 0:
                return ChatColor.GRAY.toString();
            case 1:
                return ChatColor.WHITE.toString();
            case 2:
                return AlchemyAttribute.RED.getColor();
            case 3:
                return AlchemyAttribute.BLUE.getColor();
            case 4:
                return AlchemyAttribute.GREEN.getColor();
            case 5:
                return AlchemyAttribute.YELLOW.getColor();
            case 6:
                return AlchemyAttribute.PURPLE.getColor();
            default:
                return "";
        }
    }

    public static int getAlchemyMaterialAmount(final ItemStack item, final AlchemyMaterial material) {
        int amount = 0;
        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                final List<String> idLores = AlchemyItemStatus.getLores(AlchemyItemStatus.ID, item);
                if (!idLores.isEmpty() && material.getId().equals(getStridColor(
                        idLores.get(0).replaceAll(AlchemyItemStatus.ID.getCheck(), "")
                ))) {
                    amount += item.getAmount();
                }
            }
        }
        return amount == 0 ? -1 : amount;
    }

    public static List<Category> getCategory(final ItemStack item) {
        final List<Category> result = new ArrayList<>();
        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                AlchemyItemStatus.getLores(AlchemyItemStatus.CATEGORY, item).stream()
                        .filter((cate_str) -> (!cate_str.contains("カテゴリ"))).forEachOrdered((cate_str) -> {
                    result.add(Category.valueOf(Chore.getStridColor(cate_str.substring(cate_str.indexOf("§0") + 2))));
                });
            }
        }
        return result;
    }

    public static boolean hasCategory(final ItemStack item, Category category) {
        return getCategoryMaterialAmount(item, category) != -1;
    }

    public static int getCategoryMaterialAmount(final ItemStack item, final Category category) {
        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.CATEGORY, item);
                for (int i = 1; i < lores.size(); i++) {
                    final String cate_str = lores.get(i);
                    if (category == Category.valueOf(Chore.getStridColor(cate_str.substring(cate_str.indexOf("§0") + 2)))) {
                        return item.getAmount();
                    }
                }
            }
        }
        return -1;
    }

    public static int hasMaterial(final ItemStack item, final String material) {
        if (material.startsWith("material:")) {
            return getAlchemyMaterialAmount(item, AlchemyMaterialManager.getInstance().getMaterial(material.substring(9)));
        } else if (material.startsWith("category:")) {
            return getCategoryMaterialAmount(item, Category.valueOf(material.substring(9)));
        }
        return -1;
    }

    public static boolean hasMaterial(final Inventory inv, final List<String> materials) {
        return hasMaterial(inv.getContents(), materials);
    }

    public static boolean hasMaterial(final ItemStack[] contents, final List<String> materials) {
        if (contents.length != 0) {
            final Map<String, DoubleData<Integer, Integer>> check = new HashMap<>();
            for (final ItemStack item : contents) {
                if (item != null) {
                    materials.stream().map((req) -> req.split(",")).forEachOrdered((data) -> {
                        final int amount = hasMaterial(item, data[0]);
                        if (amount != -1) {
                            if (check.containsKey(data[0])) {
                                final DoubleData<Integer, Integer> dd = check.get(data[0]);
                                dd.setLeft(dd.getLeft() + amount);
                            } else {
                                final int req_amount = Integer.parseInt(data[1]);
                                check.put(data[0], new DoubleData<>(amount, Integer.parseInt(data[1])));
                            }
                        }
                    });
                }
            }
            return check.isEmpty() ? false : check.values().stream().noneMatch((dd) -> (dd.getLeft() < dd.getRight()));
        }
        return false;
    }

    public static void warp(final Player player, final Location loc) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
            player.teleport(loc);
        });
    }

    public static String createStridColor(final String id) {
        final StringBuilder sb = new StringBuilder();
        for (final char c : id.toCharArray()) {
            sb.append(setIntColor((int) c));
        }
        return sb.toString();
    }

    public static String getStridColor(final String idcolor) {
        final StringBuilder sb = new StringBuilder();
        for (final String str : idcolor.replaceAll("§", "").split("r")) {
            sb.append(Character.toChars(Integer.parseInt(str)));
        }
        return sb.toString();
    }

    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {
        List<Location> circleBlocks = new ArrayList<>();
        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location l = new Location(centerBlock.getWorld(), x, y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }

        return circleBlocks;
    }

    public static int near(int val, int truncation) {
        TreeSet<Integer> tree = new TreeSet<>();
        for (int i = 0; i < val; i++) {
            int value = i * truncation;
            tree.add(value);
            if (value >= val) {
                break;
            }
        }
        return near(tree, val);
    }

    public static int near(TreeSet<Integer> truncation, int val) {
        int floor = truncation.floor(val);
        int ceiling = truncation.ceiling(val);
        return Math.abs(floor - val) <= Math.abs(ceiling - val) ? floor : ceiling;
    }

    public static boolean isWand(ItemStack item) {
        return item.getType() == Material.DIAMOND_HOE && Chore.getDamage(item) == 1524;
    }

    public static String setLocXYZ(Location loc) {
//        String result = setIntColor(loc.getBlockX()) + setIntColor(loc.getBlockY()) + setIntColor(loc.getBlockZ());
        return createStridColor(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
    }

    public static int[] getXYZString(String str) {
//        String memo = str;
//        int[] result = new int[3];
//        for (int i = 0; i < result.length; i++) {
//            result[i] = getColorId(memo);
//            if (i + 1 < result.length) {
//                memo = memo.substring(memo.indexOf("r") + 1);
//            }
//        }
//        return result;
        final String[] split = Chore.getStridColor(str).split(",");
        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
    }

    public static String setIntColor(int i) {
        String result = "";
        for (char ic : String.valueOf(i).toCharArray()) {
            result += "§" + ic;
        }
        return result + ChatColor.RESET;
//        return result;
    }

    public static int getColorId(String str) {
        return Integer.parseInt(str.substring(0, str.indexOf("r")).replaceAll("§", ""));
    }

    public static void addItem(Player player, ItemStack item) {
        addItem(player.getInventory(), item, player.getLocation());
    }

    public static void addItem(Inventory inv, ItemStack item, Location loc) {
        if (inv != null && item != null) {
            if (Arrays.asList(inv.getStorageContents()).contains(null)) {
                inv.addItem(item);
            } else if (loc != null) {
                Chore.drop(loc, item);
            }
        }
    }

    public static boolean isFuncBlock(Block block) {
        if (block == null) {
            return false;
        }
        return FuncBlock.searth(block.getType().toString());
    }

    public static boolean isDoor(Block block) {
        switch (block.getType()) {
            case OAK_DOOR:
            case IRON_DOOR:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
                return true;
        }
        return false;
    }

    public static boolean isRight(Action a) {
        return a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK;
    }

    public static boolean isRightOnly(Action a, boolean block) {
        return block ? a == Action.RIGHT_CLICK_BLOCK : a == Action.RIGHT_CLICK_AIR;
    }

    public static boolean isLeft(Action a) {
        return a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK;
    }

    public static boolean isLeftOnly(Action a, boolean block) {
        return block ? a == Action.LEFT_CLICK_BLOCK : a == Action.LEFT_CLICK_AIR;
    }

    public static String getName(ItemStack i) {
        if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
            return i.getItemMeta().getDisplayName();
        }
        return null;
    }

    public static Material getType(ItemStack i) {
        if (i == null) {
            return Material.AIR;
        }
        return i.getType();
    }

    public static Item drop(Location loc, ItemStack item) {
        loc.setX(loc.getX() + 0.5);
        loc.setZ(loc.getZ() + 0.5);
        return loc.getWorld().dropItem(loc, item);
    }

    public static ItemStack ci(Material m, int d, String name, List<String> lore) {
        ItemStack item = new ItemStack(m, 1, (short) d);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name.isEmpty() ? "§r" : name);
        }
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore);
        }
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
