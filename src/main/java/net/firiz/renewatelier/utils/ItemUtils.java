package net.firiz.renewatelier.utils;

import java.util.*;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.version.VersionUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public final class ItemUtils {

    private ItemUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ItemStack setSetting(final ItemStack itemStack, final NamespacedKey key, final String value) {
        final ItemMeta meta = itemStack.getItemMeta();
        CommonUtils.setSetting(meta, key, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setSetting(final ItemStack itemStack, final NamespacedKey key, final int value) {
        final ItemMeta meta = itemStack.getItemMeta();
        CommonUtils.setSettingInt(meta, key, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static String getSetting(final ItemStack itemStack, final NamespacedKey key) {
        return CommonUtils.getSetting(itemStack.getItemMeta(), key);
    }

    public static int getSettingInt(final ItemStack itemStack, final NamespacedKey key) {
        return CommonUtils.getSettingInt(itemStack.getItemMeta(), key);
    }

    public static ItemStack createCustomModelItem(final Material material, int amount, int value) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(value);
        item.setItemMeta(meta);
        return item;
    }

    public static int getCustomModelData(final ItemStack item) {
        return item.getItemMeta().getCustomModelData();
    }

    public static int getCustomModelData(final ItemMeta meta) {
        return meta.getCustomModelData();
    }

    public static void setCustomModelData(final ItemMeta meta, final int value) {
        meta.setCustomModelData(value);
    }

    /**
     * 文字列から特定のマテリアルを検索し、返します
     * <p>
     * マテリアル名で使用可能な名前
     * ・SpigotのMaterialクラスのEnum名
     * ・マインクラフトのNamespace名
     * ・SpigotのMaterialクラスの古いEnum名 - 非推奨
     * <p>
     * ダメージ値、CustomModelDataなどを示す：を用いたマテリアル名は自動的に：からが除去されます。
     *
     * @param str String -> マテリアル名
     * @return
     */
    @NotNull
    public static Material getMaterial(final String str) {
        if (str.equalsIgnoreCase("XXX")) {
            return Material.IRON_NUGGET;
        }

        Material result = Material.getMaterial(str.toUpperCase());
        if (result == null) {
            if (str.contains(":")) {
                final String[] kv = str.split(":");
                for (final Material val : Material.values()) {
                    final NamespacedKey key = val.getKey();
                    if (key.getNamespace().equalsIgnoreCase(kv[0]) && key.getKey().equalsIgnoreCase(kv[1])) {
                        return val;
                    }
                }
            } else {
                for (final Material val : Material.values()) {
                    if (val.getKey().getKey().equalsIgnoreCase(str)) {
                        return val;
                    }
                }
            }
        } else {
            return result;
        }
        result = Material.getMaterial(str.toUpperCase(), true);
        if (result == null) {
            throw new IllegalStateException("material not found for " + str + ".");
        }
        CommonUtils.logWarning("Chore.getMaterial: material " + str + " is legacy name.");
        return result;
    }

    public static boolean checkMaterial(final ItemStack content, RequireAmountMaterial material) {
        switch (material.getType()) {
            case MATERIAL:
                return material.getMaterial().equals(AlchemyMaterial.getMaterial(content));
            case CATEGORY:
                return AlchemyItemStatus.getCategories(content).contains(material.getCategory());
            default:
                return false;
        }
    }

    public static boolean hasMaterial(final Inventory inv, final List<RequireAmountMaterial> materials) {
        return hasMaterial(inv.getContents(), materials);
    }

    public static boolean hasMaterial(final ItemStack[] contents, final List<RequireAmountMaterial> materials) {
        if (contents.length != 0) {
            final Object2IntMap<RequireAmountMaterial> check = new Object2IntOpenHashMap<>();
            for (final RequireAmountMaterial data : materials) {
                if (!check.containsKey(data)) {
                    check.put(data, 0);
                }
                switch (data.getType()) {
                    case MATERIAL:
                        final AlchemyMaterial alchemyMaterial = data.getMaterial();
                        for (final ItemStack item : contents) {
                            if (item != null && alchemyMaterial.equals(AlchemyMaterial.getMaterialOrNull(item))) {
                                check.put(data, check.getInt(data) + item.getAmount());
                            }
                        }
                        break;
                    case CATEGORY:
                        final Category category = data.getCategory();
                        for (final ItemStack item : contents) {
                            if (item != null && AlchemyItemStatus.getCategories(item).contains(category)) {
                                check.put(data, check.getInt(data) + item.getAmount());
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("[hasMaterial] not support recipe material.");
                }
            }
            return !check.isEmpty() && check.object2IntEntrySet().stream().noneMatch(entry -> (entry.getIntValue() < entry.getKey().getAmount()));
        }
        return false;
    }

    public static boolean hasMaterial(final Inventory inv, final AlchemyMaterial material, int reqAmount) {
        return hasMaterial(inv.getContents(), material, reqAmount);
    }

    public static boolean hasMaterial(final ItemStack[] contents, final AlchemyMaterial material, int reqAmount) {
        if (contents.length != 0) {
            int amount = 0;
            for (final ItemStack item : contents) {
                if (item != null && material.equals(AlchemyMaterial.getMaterial(item))) {
                    amount += item.getAmount();
                }
            }
            return reqAmount <= amount;
        }
        return false;
    }

    public static boolean hasMaterial(final Inventory inv, final ItemStack item, int reqAmount) {
        return hasMaterial(inv.getContents(), item, reqAmount);
    }

    public static boolean hasMaterial(final ItemStack[] contents, final ItemStack item, int reqAmount) {
        if (contents.length != 0) {
            int amount = 0;
            for (final ItemStack i : contents) {
                if (i != null && item.isSimilar(i)) {
                    amount += i.getAmount();
                }
            }
            return reqAmount <= amount;
        }
        return false;
    }

    public static void warp(final Player player, final Location loc) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> player.teleport(loc));
    }

//    public static String setLocXYZ(Location loc) {
//        return createStridColor(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
//    }
//
//    public static int[] getXYZString(String str) {
//        final String[] split = getStridColor(str).split(",");
//        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
//    }

    public static void addItem(@NotNull HumanEntity player, ItemStack item) {
        if (item != null) {
            addItem(player.getInventory(), item, player.getLocation());
        }
    }

    public static void addItem(@NotNull Inventory inv, ItemStack item, @NotNull Location loc) {
        if (item == null) {
            return;
        }
        final ItemStack dropItem = addItemNotDrop(inv, item);
        if (dropItem != null) {
            VersionUtils.drop(loc, item, CreatureSpawnEvent.SpawnReason.DEFAULT);
        }
    }

    private static int getAmount(ItemStack item, int amount, ItemStack v) {
        if (v != null) {
            final int max_stack = v.getType().getMaxStackSize();
            if (v.isSimilar(item) && v.getAmount() < max_stack) {
                final int da = v.getAmount() + amount;
                amount = da - max_stack;
                v.setAmount(Math.min(max_stack, da));
            }
        }
        return amount;
    }

    public static ItemStack addItemNotDrop(@NotNull Inventory inv, @NotNull ItemStack item) {
        int amount = item.getAmount();
        final ItemStack[] contents = inv.getStorageContents();
        for (final ItemStack v : contents) {
            amount = getAmount(item, amount, v);
        }
        inv.setStorageContents(contents);
        if (amount > 0) {
            if (Arrays.asList(inv.getStorageContents()).contains(null)) {
                item.setAmount(amount);
                inv.addItem(item);
            } else {
                final ItemStack remainder = item.clone();
                remainder.setAmount(amount);
                return remainder;
            }
        }
        return null;
    }

    public static void gainItem(@NotNull Inventory inv, @NotNull Material material, @NotNull int reduceAmount) {
        for (final ItemStack i : inv.getStorageContents()) {
            if (i != null && i.getType() == material) {
                int v = Math.max(i.getAmount() - reduceAmount, 0);
                reduceAmount = -(i.getAmount() - reduceAmount);
                i.setAmount(v);
                if (reduceAmount <= 0) {
                    break;
                }
            }
        }
    }

    public static void gainItem(@NotNull Inventory inv, @NotNull ItemStack item, @NotNull int reduceAmount) {
        for (final ItemStack i : inv.getStorageContents()) {
            if (i != null && item.isSimilar(i)) {
                int v = Math.max(i.getAmount() - reduceAmount, 0);
                reduceAmount = -(i.getAmount() - reduceAmount);
                i.setAmount(v);
                if (reduceAmount <= 0) {
                    break;
                }
            }
        }
    }

    public static void gainItem(@NotNull Inventory inv, @NotNull AlchemyMaterial material, @NotNull int reduceAmount) {
        for (final ItemStack i : inv.getStorageContents()) {
            if (i != null && material.equals(AlchemyMaterial.getMaterial(i))) {
                int v = Math.max(i.getAmount() - reduceAmount, 0);
                reduceAmount = -(i.getAmount() - reduceAmount);
                i.setAmount(v);
                if (reduceAmount <= 0) {
                    break;
                }
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
            default:
                return false;
        }
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

    @NotNull
    public static ItemStack ci(Material m, int d, String name, List<String> lore) {
        final ItemStack item = createCustomModelItem(m, 1, d);
        final ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name.isEmpty() ? ChatColor.RESET.toString() : name);
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

    @NotNull
    public static ItemStack loreItem(Material m, List<String> lore) {
        final ItemStack item = new ItemStack(m, 1);
        final ItemMeta meta = item.getItemMeta();
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void addHideFlags(ItemMeta meta, AlchemyMaterial am) {
        meta.setUnbreakable(am.isUnbreaking());
        if (am.isHideAttribute()) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        if (am.isHideDestroy()) {
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        }
        if (am.isHideEnchant()) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (am.isHidePlacedOn()) {
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        }
        if (am.isHidePotionEffect()) {
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        }
        if (am.isHideUnbreaking()) {
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
    }

}
