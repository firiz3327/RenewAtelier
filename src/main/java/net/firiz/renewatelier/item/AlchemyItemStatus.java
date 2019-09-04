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
import net.firiz.renewatelier.inventory.alchemykettle.RecipeSelect;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.DoubleData;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.Strings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
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
                meta.getLore().stream()
                        .filter((lore) -> (lore.startsWith(check)))
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
            if (ing != null) {
                result.add(ing);
            }
        }
        return result;
    }

    public static List<Characteristic> getCharacteristics(final ItemStack item) {
        final List<Characteristic> result = new ArrayList<>();
        final List<String> lores = AlchemyItemStatus.getLores(CHARACTERISTIC, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i).substring(CHARACTERISTIC.check.length() + 4);
            final Characteristic c = Characteristic.search(lore);
            if (c != null) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<Category> getCategorys(final ItemStack item) {
        final List<Category> result = new ArrayList<>();
        final List<String> lores = getLores(CATEGORY, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i);
            final Category category = Category.valueOf(Chore.getStridColor(lore.substring(lore.indexOf("§0") + 2)));
            if (category != null) {
                result.add(category);
            }
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

    public static ItemStack getItem(final String id, final List<Ingredients> over_ings, ItemStack item, final int over_quality, final int[] over_size, final List<Characteristic> over_characteristics) {
        return getItem(AlchemyMaterial.getMaterial(id), over_ings, item, over_quality, over_size, over_characteristics, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> over_ings, ItemStack item, final int over_quality, final int[] over_size, final List<Characteristic> over_characteristics, final List<Category> over_category) {
        return getItem(am, over_ings, item, over_quality, over_size, over_characteristics, over_category, false);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> over_ings, ItemStack item, final int over_quality, final int[] over_size, final List<Characteristic> over_characteristics, final List<Category> over_category, final boolean not_visible_catalyst) {
        return getItem(am, over_ings, item, over_quality, over_size, null, over_characteristics, over_category, not_visible_catalyst);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> over_ings, ItemStack item, final int over_quality, final int[] over_size, final List<String> active_effects, final List<Characteristic> over_characteristics, final List<Category> over_category, final boolean not_visible_catalyst) {
        return getItem(am, over_ings, item, over_quality, over_size, active_effects, over_characteristics, over_category, new boolean[]{false, false, false, false, not_visible_catalyst, false, false});
    }

    // not_visibles {id, quality, ings, size, catalyst, category, end}
    public static ItemStack getItem(final AlchemyMaterial am, final List<Ingredients> over_ings, ItemStack item, final int over_quality, final int[] over_size, final List<String> active_effects, final List<Characteristic> over_characteristics, final List<Category> over_category, final boolean[] not_visibles) {
        if (am == null || (am.getIngredients().isEmpty() && (over_ings == null || over_ings.isEmpty()))) {
            return null;
        }
        if (item == null || item.getType() == Material.AIR) {
            final DoubleData<Material, Integer> matdata = am.getMaterial();
            item = Chore.createCustomModelItem(matdata.getLeft(), 1, matdata.getRight());
        }
        final ItemMeta meta = item.getItemMeta();
        if (!am.isDefaultName()) {
            meta.setDisplayName(am.getName());
        }

        final List<String> lore = new ArrayList<>();
        if (!not_visibles[0]) {
            lore.add(ID.check + Chore.createStridColor(am.getId()));
        }
        if (!not_visibles[1]) {
            lore.add(QUALITY.check + "§7品質: §r" + (over_quality != -1 ? over_quality : Randomizer.nextInt(am.getQualityMax() - am.getQualityMin()) + am.getQualityMin()));
        }

        // 錬金成分
        if (!not_visibles[2]) {
            int all_level = 0;
            final Map<AlchemyAttribute, Integer> levels = new HashMap<>();
            final List<Ingredients> acls = new ArrayList<>();
            // 錬金成分・確定取得
            if (over_ings != null) {
                for (final Ingredients ingredients : over_ings) {
                    acls.add(ingredients);
                    final int level = ingredients.getLevel();
                    final AlchemyAttribute type = ingredients.getType();
                    if (levels.containsKey(type)) {
                        levels.put(type, levels.get(type) + level);
                    } else {
                        levels.put(type, level);
                    }
                    all_level += level;
                }
            } else {
                for (final DoubleData<Ingredients, Integer> dd : am.getIngredients()) {
                    if (dd.getRight() == 100) {
                        final Ingredients ingredients = dd.getLeft();
                        acls.add(ingredients);
                        final int level = ingredients.getLevel();
                        final AlchemyAttribute type = ingredients.getType();
                        if (levels.containsKey(type)) {
                            levels.put(type, levels.get(type) + level);
                        } else {
                            levels.put(type, level);
                        }
                        all_level += level;
                    }
                }
                // 錬金成分・ランダム取得
                for (int i = 0; i < Math.min(6, Randomizer.nextInt(am.getIngredients().size()) + 1); i++) {
                    final DoubleData<Ingredients, Integer> dd = am.getIngredients().get(Randomizer.nextInt(am.getIngredients().size()));
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
                            all_level += level;
                        }
                    }
                }
                // 錬金成分・非マイナス化
                final List<Ingredients> ingacls = new ArrayList<>();
                am.getIngredients().forEach((dd) -> ingacls.add(dd.getLeft()));
                while (all_level <= 0) {
                    final List<Ingredients> list = new ArrayList<>(ingacls);
                    Collections.shuffle(list);
                    for (final Ingredients i : list) {
                        if (i.getLevel() > 0) {
                            acls.add(i);
                            final int level = i.getLevel();
                            final AlchemyAttribute type = i.getType();
                            if (levels.containsKey(type)) {
                                levels.put(type, levels.get(type) + level);
                            } else {
                                levels.put(type, level);
                            }
                            all_level += level;
                            break;
                        }
                    }
                }
            }
            // 錬金成分・属性最大取得
            int max_level = 0;
            final List<AlchemyAttribute> maxtype = new ArrayList<>();
            for (final AlchemyAttribute type : levels.keySet()) {
                int level = levels.get(type);
                if (level > max_level) {
                    max_level = level;
                    maxtype.clear();
                    maxtype.add(type);
                } else if (level == max_level) {
                    maxtype.add(type);
                }
            }
            // 錬金成分・設定
            final StringBuilder ingsb = new StringBuilder();
            ingsb.append(ALCHEMY_INGREDIENTS.check).append("§7錬金成分: §r").append(all_level).append(" ");
            maxtype.forEach((type) -> ingsb.append(type.getColor()).append("●"));
            lore.add(ingsb.toString());
            acls.forEach((i) -> lore.add(ALCHEMY_INGREDIENTS.check + "§r- " + i.getName() + " : " + i.getType().getColor() + i.getLevel()));
        }
        // サイズ
        if (!not_visibles[3]) {
            lore.add(SIZE.check + "§7サイズ:");
            StringBuilder str = new StringBuilder();
            int c_size = 0;
            final int[] ss = over_size == null ? am.getSizes().get(Randomizer.nextInt(am.getSizes().size())).getSize() : over_size;
            for (final int i : ss) {
                if (i == 0) {
                    str.append(Chore.intCcolor(i)).append(Strings.W_W);
                } else {
                    str.append(Chore.intCcolor(i)).append(Strings.W_B);
                }
                if (c_size >= 2) {
                    lore.add(str.toString());
                    c_size = 0;
                    str = new StringBuilder();
                } else {
                    c_size++;
                }
            }
        }
        // 触媒
        if (!not_visibles[4]) {
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
        if (active_effects != null && !active_effects.isEmpty()) {
            lore.add(EFFECT.check + "§7効果: ");
            active_effects.forEach((effect) -> lore.add(EFFECT.check + "§r- " + effect));
        }
        // 特性
        final List<Characteristic> characteristics = new ArrayList<>();
        if (over_characteristics == null) {
            if (am.getCharas() != null) {
                final List<DoubleData<Characteristic, Integer>> cs = new ArrayList<>();
                am.getCharas().forEach((obj) -> {
                    if (obj instanceof CharacteristicTemplate) {
                        cs.addAll(Arrays.asList(((CharacteristicTemplate) obj).getCs()));
                    } else {
                        cs.add((DoubleData<Characteristic, Integer>) obj);
                    }
                });
                Collections.shuffle(cs);
                for (DoubleData<Characteristic, Integer> dd : cs) {
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
            characteristics.addAll(over_characteristics);
        }
        if (!characteristics.isEmpty()) {
            lore.add(CHARACTERISTIC.check + "§7特性:");
            characteristics.forEach((c) -> lore.add(CHARACTERISTIC.check + "§r- " + c.getName()));
        }
        // カテゴリ
        if (!not_visibles[5]) {
            final List<Category> categorys = over_category == null ? am.getCategorys() : over_category;
            if (!categorys.isEmpty()) {
                lore.add(CATEGORY.check + "§7カテゴリ:");
                categorys.forEach((category) -> lore.add(CATEGORY.check + "§r- " + category.getName() + "§0" + Chore.createStridColor(category.toString())));
            }
        }

        // Lore終了
        if (!not_visibles[6]) {
            lore.add(LORE_END.check);
        }
        meta.setLore(lore);

        // unbreaking & flag系
        Chore.addHideFlags(meta, am);

        // meta設定
        item.setItemMeta(meta);
        return item;
    }

}
