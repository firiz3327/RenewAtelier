/*
 * AlchemyItemStatus.java
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
package net.firiz.renewatelier.item;

import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicTemplate;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.doubledata.DoubleData;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.Strings;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * @author firiz
 */
public enum AlchemyItemStatus {
    ID("§l§d"),
    CATALYST_SIZE("§c§a§f§2§1§d§e"), // 触媒・サイズ
    CATEGORY("§c§a§f§e§d§o§r§d"), // 錬金アイテム・カテゴリ
    SIZE("§2§1§d§e"), // 錬金アイテム・サイズ
    QUALITY("§c§d§a§1§1§f§f"), // 品質
    ALCHEMY_INGREDIENTS("§a§1§c§1§n§4§r§e§d§1§e§n§f§b"), // 錬金成分
    CHARACTERISTIC("§c§f§a§r§a§c§f§e§r§1§b§f§1§c"), // 特性
    MATERIAL("§l§a§f§e§r§l§a§l"), // マテリアル-レシピ用
    EFFECT("§e§t§t§e§c§t"),
    BAG("§b§a§e"), // バッグ用
    BAG_ITEMS("§b§a§e§l§f§e§m§r"), // バッグ-アイテム用
    LORE_END("§e§e§b§e§e§b");

    private final String check;

    AlchemyItemStatus(final String check) {
        this.check = check;
    }

    public String getCheck() {
        return check;
    }

    public static List<String> getLores(final AlchemyItemStatus type, final ItemStack item) {
        return getLores(type.check, item);
    }

    public static List<String> getLores(final String check, final ItemStack item) {
        final List<String> list = new ArrayList<>();
        if (check != null && item != null && item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                Objects.requireNonNull(meta.getLore()).stream()
                        .filter(lore -> (lore.startsWith(check)))
                        .forEachOrdered(list::add);
            }
        }
        return list;
    }

    public static String getId(final ItemStack item) {
        final List<String> lores = getLores(ID, item);
        if (!lores.isEmpty()) {
            return Chore.getStridColor(lores.get(0).substring(ID.check.length()));
        }
        return null;
    }

    public static int getQuality(final ItemStack item) {
        final List<String> lores = getLores(QUALITY, item);
        if (!lores.isEmpty()) {
            return Integer.parseInt(lores.get(0).substring(QUALITY.check.length() + 8));  // (QUALITY.check + "§7品質: §r").length
        }
        return -1;
    }

    public static List<Ingredients> getIngredients(final ItemStack item) {
        final List<Ingredients> result = new ArrayList<>();
        final List<String> lores = getLores(ALCHEMY_INGREDIENTS, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i);
            final String name = lore.substring(ALCHEMY_INGREDIENTS.check.length() + 4, lore.indexOf(" : "));
            final AlchemyIngredients ing = AlchemyIngredients.searchName(name);
            result.add(ing);
        }
        return result;
    }

    public static List<Characteristic> getCharacteristics(final ItemStack item) {
        final List<Characteristic> result = new ArrayList<>();
        final List<String> lores = AlchemyItemStatus.getLores(CHARACTERISTIC, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i).substring(CHARACTERISTIC.check.length() + 4);
            final Characteristic c = Characteristic.search(lore);
            result.add(c);
        }
        return result;
    }

    public static List<Category> getCategorys(final ItemStack item) {
        final List<Category> result = new ArrayList<>();
        final List<String> lores = getLores(CATEGORY, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i);
            final Category category = Category.valueOf(Chore.getStridColor(lore.substring(lore.indexOf("§0") + 2)));
            result.add(category);
        }
        return result;
    }

    public static ItemStack getItem(final String id) {
        return getItem(id, null, null, -1, null, null);
    }

    public static ItemStack getItem(final String id, final List<Ingredients> ings) {
        return getItem(id, ings, null, -1, null, null);
    }

    public static ItemStack getItem(final String id, final ItemStack item) {
        return getItem(id, null, item, -1, null, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am) {
        return getItem(am, null, null, -1, null, null, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> ings) {
        return getItem(am, ings, null, -1, null, null, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final ItemStack item) {
        return getItem(am, null, item, -1, null, null, null);
    }

    public static ItemStack getItem(final String id, final List<Ingredients> overIngs, ItemStack item, final int over_quality, final int[] overSize, final List<Characteristic> overCharacteristics) {
        return getItem(AlchemyMaterial.getMaterial(id), overIngs, item, over_quality, overSize, overCharacteristics, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> overIngs, ItemStack item, final int over_quality, final int[] overSize, final List<Characteristic> overCharacteristics, final List<Category> overCategory) {
        return getItem(am, overIngs, item, over_quality, overSize, overCharacteristics, overCategory, false);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> overIngs, ItemStack item, final int over_quality, final int[] overSize, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final boolean notVisibleCatalyst) {
        return getItem(am, overIngs, item, over_quality, overSize, null, overCharacteristics, overCategory, notVisibleCatalyst);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<String> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final boolean notVisibleCatalyst) {
        return getItem(am, overIngs, item, overQuality, overSize, activeEffects, overCharacteristics, overCategory, new boolean[]{false, false, false, false, notVisibleCatalyst, false, false});
    }

    /**
     * notVisibles {id, quality, ings, size, catalyst, category, end}
     * @param am
     * @param overIngs
     * @param item
     * @param overQuality
     * @param overSize
     * @param activeEffects
     * @param overCharacteristics
     * @param overCategory
     * @param notVisibles
     * @return
     */
    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<String> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final boolean[] notVisibles) {
        if (am == null || (am.getIngredients().isEmpty() && (overIngs == null || overIngs.isEmpty()))) {
            return null;
        }
        if (item == null || item.getType() == Material.AIR) {
            final FinalDoubleData<Material, Integer> matdata = am.getMaterial();
            item = Chore.createCustomModelItem(matdata.getLeft(), 1, matdata.getRight());
        }
        final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        if (!am.isDefaultName()) {
            meta.setDisplayName(am.getName());
        }

        final List<String> lore = new ArrayList<>();
        if (!notVisibles[0]) {
            lore.add(ID.check + Chore.createStridColor(am.getId()));
        }
        if (!notVisibles[1]) {
            lore.add(QUALITY.check + "§7品質: §r" + (overQuality != -1 ? overQuality : Randomizer.nextInt(am.getQualityMax() - am.getQualityMin()) + am.getQualityMin()));
        }

        // 錬金成分
        if (!notVisibles[2]) {
            int allLevel = 0;
            final Map<AlchemyAttribute, Integer> levels = new HashMap<>();
            final List<Ingredients> acls = new ArrayList<>();
            // 錬金成分・確定取得
            if (overIngs != null) {
                for (final Ingredients ingredients : overIngs) {
                    allLevel += getLevel(levels, acls, ingredients);
                }
            } else {
                for (final FinalDoubleData<Ingredients, Integer> dd : am.getIngredients()) {
                    if (dd.getRight() == 100) {
                        final Ingredients ingredients = dd.getLeft();
                        allLevel += getLevel(levels, acls, ingredients);
                    }
                }
                // 錬金成分・ランダム取得
                for (int i = 0; i < Math.min(6, Randomizer.nextInt(am.getIngredients().size()) + 1); i++) {
                    final FinalDoubleData<Ingredients, Integer> dd = am.getIngredients().get(Randomizer.nextInt(am.getIngredients().size()));
                    if (Randomizer.nextInt(100) <= dd.getRight()) {
                        final Ingredients ingredients = dd.getLeft();
                        if (!acls.contains(ingredients)) {
                            acls.add(ingredients);
                            int level = ingredients.getLevel();
                            final AlchemyAttribute type = ingredients.getType();
                            if (levels.containsKey(type)) {
                                levels.put(type, levels.get(type) + level);
                            } else {
                                levels.put(type, level);
                            }
                            allLevel += level;
                        }
                    }
                }
                // 錬金成分・非マイナス化
                final List<Ingredients> ingacls = new ArrayList<>();
                am.getIngredients().forEach(dd -> ingacls.add(dd.getLeft()));
                while (allLevel <= 0) {
                    final List<Ingredients> list = new ArrayList<>(ingacls);
                    Collections.shuffle(list);
                    for (final Ingredients ingredients : list) {
                        if (ingredients.getLevel() > 0) {
                            allLevel += getLevel(levels, acls, ingredients);
                            break;
                        }
                    }
                }
            }
            // 錬金成分・属性最大取得
            int maxLevel = 0;
            final List<AlchemyAttribute> maxtype = new ArrayList<>();
            for (final AlchemyAttribute type : levels.keySet()) {
                int level = levels.get(type);
                if (level > maxLevel) {
                    maxLevel = level;
                    maxtype.clear();
                    maxtype.add(type);
                } else if (level == maxLevel) {
                    maxtype.add(type);
                }
            }
            // 錬金成分・設定
            final StringBuilder ingsb = new StringBuilder();
            ingsb.append(ALCHEMY_INGREDIENTS.check).append("§7錬金成分: §r").append(allLevel).append(" ");
            maxtype.forEach(type -> ingsb.append(type.getColor()).append("●"));
            lore.add(ingsb.toString());
            acls.forEach(i -> lore.add(ALCHEMY_INGREDIENTS.check + "§r- " + i.getName() + " : " + i.getType().getColor() + i.getLevel()));
        }
        // サイズ
        if (!notVisibles[3]) {
            lore.add(SIZE.check + "§7サイズ:");
            StringBuilder str = new StringBuilder();
            int cSize = 0;
            final int[] ss = overSize == null ? am.getSizeTemplate().getSize(Randomizer.nextInt(9)) : overSize;
            for (final int i : ss) {
                if (i == 0) {
                    str.append(Chore.intCcolor(i)).append(Strings.W_W);
                } else {
                    str.append(Chore.intCcolor(i)).append(Strings.W_B);
                }
                if (cSize >= 2) {
                    lore.add(str.toString());
                    cSize = 0;
                    str = new StringBuilder();
                } else {
                    cSize++;
                }
            }
        }
        // 触媒
        if (!notVisibles[4]) {
            final Catalyst catalyst = am.getCatalyst();
            if (catalyst != null) {
                lore.add("§7触媒:");
                final List<Integer> allcs = new ArrayList<>();
                for (final CatalystBonus bonus : catalyst.getBonus()) {
                    if (allcs.isEmpty()) {
                        for (final int i : bonus.getCS()) {
                            allcs.add(i);
                        }
                    } else {
                        int c = 0;
                        for (final int i : bonus.getCS()) {
                            if (i != 0) {
                                allcs.set(c, i);
                            }
                            c++;
                        }
                    }
                }
                final int size = allcs.size();
                final int rotate_value = size == 36 ? 6 : (size == 25 ? 5 : 4);
                final StringBuilder sb = new StringBuilder();
                int count = 0;
                for (int i = 0; i < rotate_value; i++) {
                    for (int j = 1; j <= rotate_value; j++) {
                        final int value = allcs.get(count);
                        if (value == 0) {
                            sb.append(Chore.intCcolor(value)).append(Strings.W_W);
                        } else {
                            sb.append(Chore.intCcolor(value)).append(Strings.W_B);
                        }
                        count++;
                    }
                    lore.add(CATALYST_SIZE.check + sb.toString());
                    sb.delete(0, sb.length());
                }
            }
        }
        // 効果
        if (activeEffects != null && !activeEffects.isEmpty()) {
            lore.add(EFFECT.check + "§7効果: ");
            activeEffects.forEach(effect -> lore.add(EFFECT.check + "§r- " + effect));
        }
        // 特性
        final List<Characteristic> characteristics = new ArrayList<>();
        if (overCharacteristics == null) {
            if (am.getCharas() != null) {
                final List<FinalDoubleData<Characteristic, Integer>> cs = new ArrayList<>();
                am.getCharas().forEach(obj -> {
                    if (obj instanceof CharacteristicTemplate) {
                        cs.addAll(((CharacteristicTemplate) obj).getCs());
                    } else {
                        cs.add(DoubleData.castList(obj));
                    }
                });
                Collections.shuffle(cs);
                for (FinalDoubleData<Characteristic, Integer> dd : cs) {
                    if (characteristics.size() < 3) {
                        if (Randomizer.nextInt(1000 /* - Math.max(500, (錬金レベル * 10)) */) <= dd.getRight()) {
                            characteristics.add(dd.getLeft());
                        }
                        continue;
                    }
                    break;
                }
            }
        } else {
            characteristics.addAll(overCharacteristics);
        }
        if (!characteristics.isEmpty()) {
            lore.add(CHARACTERISTIC.check + "§7特性:");
            characteristics.forEach(c -> lore.add(CHARACTERISTIC.check + "§r- " + c.getName()));
        }
        // カテゴリ
        if (!notVisibles[5]) {
            final List<Category> categorys = overCategory == null ? am.getCategorys() : overCategory;
            if (!categorys.isEmpty()) {
                lore.add(CATEGORY.check + "§7カテゴリ:");
                categorys.forEach(category -> lore.add(CATEGORY.check + "§r- " + category.getName() + "§0" + Chore.createStridColor(category.toString())));
            }
        }

        // Lore終了
        if (!notVisibles[6]) {
            lore.add(LORE_END.check);
        }
        meta.setLore(lore);

        // unbreaking & flag系
        Chore.addHideFlags(meta, am);

        // meta設定
        item.setItemMeta(meta);
        return item;
    }

    private static int getLevel(Map<AlchemyAttribute, Integer> levels, List<Ingredients> acls, Ingredients i) {
        acls.add(i);
        final int level = i.getLevel();
        final AlchemyAttribute type = i.getType();
        if (levels.containsKey(type)) {
            levels.put(type, levels.get(type) + level);
        } else {
            levels.put(type, level);
        }
        return level;
    }

}
