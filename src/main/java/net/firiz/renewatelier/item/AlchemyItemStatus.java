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
import net.firiz.renewatelier.alchemy.kettle.AlchemyCircle;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicTemplate;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.chores.CollectionUtils;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.Strings;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author firiz
 */
public class AlchemyItemStatus {

    public enum Type {
        ID("§l§d"),
        CATALYST_SIZE("§c§a§f§2§1§d§e"), // 触媒・サイズ
        CATEGORY("§c§a§f§e§d§o§r§d"), // 錬金アイテム・カテゴリ
        SIZE("§2§1§d§e"), // 錬金アイテム・サイズ
        QUALITY("§c§d§a§1§1§f§f"), // 品質
        ALCHEMY_INGREDIENTS("§a§1§c§1§n§4§r§e§d§1§e§n§f§b"), // 錬金成分
        CHARACTERISTIC("§c§f§a§r§a§c§f§e§r§1§b§f§1§c"), // 特性
        MATERIAL("§l§a§f§e§r§l§a§l"), // マテリアル-レシピ用
        HP("§1§d§l§d"), // 装備・武器用
        MP("§2§d§l§d"), // 装備・武器用
        ATK("§3§d§l§d"), // 装備・武器用
        DEF("§4§d§l§d"), // 装備・武器用
        SPEED("§5§d§l§d"), // 装備・武器用
        EFFECT("§e§t§t§e§c§t"), // 効果
        BAG("§b§a§e"), // バッグ用
        BAG_ITEMS("§b§a§e§l§f§e§m§r"), // バッグ-アイテム用
        LORE_END("§e§e§b§e§e§b");

        private final String check;

        Type(final String check) {
            this.check = check;
        }

        public String getCheck() {
            return check;
        }
    }

    private final AlchemyMaterial alchemyMaterial;
    private final FinalDoubleData<Material, Integer> customModel;
    private int[] size;
    private final List<Category> categories;
    private int quality;
    private final List<AlchemyIngredients> ingredients;
    private final List<Characteristic> characteristics;
    private final List<String> activeEffects;
    private int hp;
    private int mp;
    private int atk;
    private int def;
    private int speed;

    public AlchemyItemStatus(AlchemyMaterial alchemyMaterial, int[] size, List<Category> categories, int quality, List<AlchemyIngredients> ingredients, List<Characteristic> characteristics, List<String> activeEffects, int hp, int mp, int atk, int def, int speed) {
        this.alchemyMaterial = alchemyMaterial;
        this.customModel = null;
        this.size = size;
        this.categories = categories;
        this.quality = quality;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
        this.activeEffects = activeEffects;
        this.hp = hp;
        this.mp = mp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
    }

    public AlchemyItemStatus(AlchemyMaterial alchemyMaterial, FinalDoubleData<Material, Integer> customModel, int[] size, List<Category> categories, int quality, List<AlchemyIngredients> ingredients, List<Characteristic> characteristics, List<String> activeEffects, int hp, int mp, int atk, int def, int speed) {
        this.alchemyMaterial = alchemyMaterial;
        this.customModel = customModel;
        this.size = size;
        this.categories = categories;
        this.quality = quality;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
        this.activeEffects = activeEffects;
        this.hp = hp;
        this.mp = mp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
    }

    @NotNull
    public AlchemyMaterial getAlchemyMaterial() {
        return alchemyMaterial;
    }

    public int[] getSize() {
        return size;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public int getQuality() {
        return quality;
    }

    public List<AlchemyIngredients> getIngredients() {
        return ingredients;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public List<String> getActiveEffects() {
        return activeEffects;
    }

    public void setSize(int[] size) {
        this.size = size;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @NotNull
    public ItemStack create() {
        final ItemStack item;
        if (customModel == null) {
            item = null;
        } else {
            item = new ItemStack(customModel.getLeft());
            if (customModel.getRight() != -1) {
                final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                meta.setCustomModelData(customModel.getRight());
                item.setItemMeta(meta);
            }
        }
        return AlchemyItemStatus.getItem(
                alchemyMaterial,
                ingredients,
                item,
                quality,
                size,
                activeEffects,
                characteristics,
                categories,
                new VisibleFlags(true, true, true, true, true, true, true, true),
                hp,
                mp,
                atk,
                def,
                speed
        );
    }

    @Nullable
    public static AlchemyItemStatus load(@Nullable ItemStack item) {
        if (item != null) {
            final AlchemyMaterial alchemyMaterial = AlchemyMaterial.getMaterialOrNull(item);
            if (alchemyMaterial != null) {
                final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                return new AlchemyItemStatus(
                        alchemyMaterial,
                        new FinalDoubleData<>(item.getType(), meta.hasCustomModelData() ? meta.getCustomModelData() : -1),
                        getSize(item),
                        getCategorys(item),
                        getQuality(item),
                        getIngredients(item),
                        getCharacteristics(item),
                        getActiveEffects(item),
                        getEquipStats(Type.HP, item),
                        getEquipStats(Type.MP, item),
                        getEquipStats(Type.ATK, item),
                        getEquipStats(Type.DEF, item),
                        getEquipStats(Type.SPEED, item)
                );
            }
        }
        return null;
    }

    private static int getEquipStats(Type type, ItemStack item) {
        switch (type) {
            case HP:
            case MP:
            case ATK:
            case DEF:
            case SPEED:
                final List<String> lores = getLores(type, item);
                if (!lores.isEmpty()) {
                    return Integer.parseInt(
                            lores.get(0).substring(lores.get(0).indexOf(ChatColor.RESET.toString()) + 2)
                    );
                }
                return 0;
            default:
                throw new IllegalArgumentException("not supported type.");
        }
    }

    @NotNull
    public static int[] getSize(final ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
            if (meta.hasLore()) {
                final List<Integer> size = new ArrayList<>();
                boolean start = false;
                for (final String lore : Objects.requireNonNull(meta.getLore())) {
                    if (lore.contains(Strings.W_W) || lore.contains(Strings.W_B)) {
                        start = true;
                        String b = null;
                        for (char l : lore.toCharArray()) {
                            String j = String.valueOf(l);
                            switch (j) {
                                case Strings.W_W:
                                case Strings.W_B:
                                    size.add(AlchemyCircle.colorCint(b));
                                    break;
                                default:
                                    b = j;
                                    break;
                            }
                        }
                    } else if (start) {
                        break;
                    }
                }
                if (size.size() == 9) {
                    return CollectionUtils.parseInts(size);
                }
            }
        }
        return new int[0];
    }

    public static int getSizeCount(final ItemStack item) {
        final int[] size = getSize(item);
        int i = 0;
        for (int j : size) {
            if (j != 0) {
                i++;
            }
        }
        return i;
    }

    public static ItemMeta setSize(final ItemStack item, final int[] size) {
        final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        final List<String> lores = Objects.requireNonNull(meta.getLore());
        int sc = -1;
        int l = 0;
        while (l < lores.size()) {
            if (sc >= 0) {
                StringBuilder str = new StringBuilder();
                int cSize = 0;
                int c = 0;
                for (final int k : size) {
                    if (k == 0) {
                        str.append(Chore.intCcolor(k)).append(Strings.W_W);
                    } else {
                        str.append(Chore.intCcolor(k)).append(Strings.W_B);
                    }
                    if (cSize >= 2) {
                        lores.set(l + c, str.toString());
                        cSize = 0;
                        c++;
                        str = new StringBuilder();
                    } else {
                        cSize++;
                    }
                }
                break;
            } else if (lores.get(l).startsWith(AlchemyItemStatus.Type.SIZE.getCheck())) {
                sc = 0;
                l++;
                continue;
            }
            l++;
        }
        meta.setLore(lores);
        return meta;
    }

    public static List<String> getLores(final AlchemyItemStatus.Type type, final ItemStack item) {
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
        final List<String> lores = getLores(Type.ID, item);
        if (!lores.isEmpty()) {
            return Chore.getStridColor(lores.get(0).substring(Type.ID.check.length()));
        }
        return null;
    }

    public static int getQuality(final ItemStack item) {
        final List<String> lores = getLores(Type.QUALITY, item);
        if (!lores.isEmpty()) {
            return Integer.parseInt(lores.get(0).substring(Type.QUALITY.check.length() + 8));  // (QUALITY.check + "§7品質: §r").length
        }
        return -1;
    }

    public static List<String> getActiveEffects(final ItemStack item) {
        final List<String> result = new ArrayList<>();
        final List<String> lores = getLores(Type.EFFECT, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i);
            final String name = lore.substring(Type.EFFECT.check.length() + 4);
            result.add(name);
        }
        return result;
    }

    public static List<AlchemyIngredients> getIngredients(final ItemStack item) {
        final List<AlchemyIngredients> result = new ArrayList<>();
        final List<String> lores = getLores(Type.ALCHEMY_INGREDIENTS, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i);
            final String name = lore.substring(Type.ALCHEMY_INGREDIENTS.check.length() + 4, lore.indexOf(" : "));
            final AlchemyIngredients ing = AlchemyIngredients.searchName(name);
            result.add(ing);
        }
        return result;
    }

    public static List<Characteristic> getCharacteristics(final ItemStack item) {
        final List<Characteristic> result = new ArrayList<>();
        final List<String> lores = AlchemyItemStatus.getLores(Type.CHARACTERISTIC, item);
        for (int i = 1; i < lores.size(); i++) {
            final String lore = lores.get(i).substring(Type.CHARACTERISTIC.check.length() + 4);
            final Characteristic c = Characteristic.search(lore);
            result.add(c);
        }
        return result;
    }

    public static List<Category> getCategorys(final ItemStack item) {
        final List<Category> result = new ArrayList<>();
        final List<String> lores = getLores(Type.CATEGORY, item);
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

    public static ItemStack getItem(final String id, final List<AlchemyIngredients> ings) {
        return getItem(id, ings, null, -1, null, null);
    }

    public static ItemStack getItem(final String id, final ItemStack item) {
        return getItem(id, null, item, -1, null, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am) {
        return getItem(am, null, null, -1, null, null, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> ings) {
        return getItem(am, ings, null, -1, null, null, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final ItemStack item) {
        return getItem(am, null, item, -1, null, null, null);
    }

    public static ItemStack getItem(final String id, final List<AlchemyIngredients> overIngs, ItemStack item, final int over_quality, final int[] overSize, final List<Characteristic> overCharacteristics) {
        return getItem(AlchemyMaterial.getMaterial(id), overIngs, item, over_quality, overSize, overCharacteristics, null);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int over_quality, final int[] overSize, final List<Characteristic> overCharacteristics, final List<Category> overCategory) {
        return getItem(am, overIngs, item, over_quality, overSize, overCharacteristics, overCategory, false);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int over_quality, final int[] overSize, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final boolean notVisibleCatalyst) {
        return getItem(am, overIngs, item, over_quality, overSize, null, overCharacteristics, overCategory, notVisibleCatalyst);
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<String> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final boolean notVisibleCatalyst) {
        return getItem(am, overIngs, item, overQuality, overSize, activeEffects, overCharacteristics, overCategory, new VisibleFlags(true, true, true, true, !notVisibleCatalyst, true, true, true));
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<String> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final VisibleFlags visibles) {
        return getItem(am, overIngs, item, overQuality, overSize, activeEffects, overCharacteristics, overCategory, visibles, am.getHp(), am.getMp(), am.getAtk(), am.getDef(), am.getSpeed());
    }

    public static class VisibleFlags {
        private final boolean id;
        private final boolean quality;
        private final boolean ingredients;
        private final boolean size;
        private final boolean catalyst;
        private final boolean category;
        private final boolean end;
        private final boolean status;

        public VisibleFlags(boolean id, boolean quality, boolean ingredients, boolean size, boolean catalyst, boolean category, boolean end, boolean status) {
            this.id = id;
            this.quality = quality;
            this.ingredients = ingredients;
            this.size = size;
            this.catalyst = catalyst;
            this.category = category;
            this.end = end;
            this.status = status;
        }
    }

    private static void addLoreStatus(List<String> lore, Type type, String name, int value) {
        if (value != 0) {
            lore.add(type.check + ChatColor.GRAY + name + ": " + ChatColor.RESET + value);
        }
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<String> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final VisibleFlags visibles, final int hp, final int mp, final int atk, final int def, final int speed) {
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
        if (visibles.id) {
            lore.add(Type.ID.check + Chore.createStridColor(am.getId()));
        }
        if (visibles.quality) {
            lore.add(Type.QUALITY.check + "§7品質: §r" + (overQuality != -1 ? overQuality : Randomizer.nextInt(am.getQualityMax() - am.getQualityMin()) + am.getQualityMin()));
        }

        final List<Category> categorys = overCategory == null ? am.getCategorys() : overCategory;
        if (visibles.status) {
            addLoreStatus(lore, Type.HP, "HP", hp);
            addLoreStatus(lore, Type.MP, "MP", mp);
            addLoreStatus(lore, Type.ATK, "攻撃力", atk);
            addLoreStatus(lore, Type.DEF, "防御力", def);
            addLoreStatus(lore, Type.SPEED, "素早さ", speed);

            if (categorys.contains(Category.WEAPON)) {
                lore.add(ChatColor.GRAY + "ダメージ: " + ChatColor.RESET + am.getBaseDamageMin() + " - " + am.getBaseDamageMax());
            }
        }

        // 錬金成分
        if (visibles.ingredients) {
            int allLevel = 0;
            final Map<AlchemyAttribute, Integer> levels = new EnumMap<>(AlchemyAttribute.class);
            final List<AlchemyIngredients> acls = new ArrayList<>();
            // 錬金成分・確定取得
            if (overIngs != null) {
                for (final AlchemyIngredients ingredients : overIngs) {
                    allLevel += getLevel(levels, acls, ingredients);
                }
            } else {
                for (final FinalDoubleData<AlchemyIngredients, Integer> dd : am.getIngredients()) {
                    if (dd.getRight() == 100) {
                        final AlchemyIngredients ingredients = dd.getLeft();
                        allLevel += getLevel(levels, acls, ingredients);
                    }
                }
                // 錬金成分・ランダム取得
                for (int i = 0; i < Math.min(6, Randomizer.nextInt(am.getIngredients().size()) + 1); i++) {
                    final FinalDoubleData<AlchemyIngredients, Integer> dd = am.getIngredients().get(Randomizer.nextInt(am.getIngredients().size()));
                    if (Randomizer.nextInt(100) <= dd.getRight()) {
                        final AlchemyIngredients ingredients = dd.getLeft();
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
                final List<AlchemyIngredients> ingacls = new ArrayList<>();
                am.getIngredients().forEach(dd -> ingacls.add(dd.getLeft()));
                while (allLevel <= 0) {
                    final List<AlchemyIngredients> list = new ArrayList<>(ingacls);
                    Collections.shuffle(list);
                    for (final AlchemyIngredients ingredients : list) {
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
            ingsb.append(Type.ALCHEMY_INGREDIENTS.check).append("§7錬金成分: §r").append(allLevel).append(" ");
            maxtype.forEach(type -> ingsb.append(type.getColor()).append("●"));
            lore.add(ingsb.toString());
            acls.forEach(i -> lore.add(Type.ALCHEMY_INGREDIENTS.check + "§r- " + i.getName() + " : " + i.getType().getColor() + i.getLevel()));
        }
        // サイズ
        if (visibles.size) {
            lore.add(Type.SIZE.check + "§7サイズ:");
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
        if (visibles.catalyst) {
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
                    lore.add(Type.CATALYST_SIZE.check + sb.toString());
                    sb.delete(0, sb.length());
                }
            }
        }
        // 効果
        if (activeEffects != null && !activeEffects.isEmpty()) {
            lore.add(Type.EFFECT.check + "§7効果: ");
            activeEffects.forEach(effect -> lore.add(Type.EFFECT.check + "§r- " + effect));
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
                        cs.add(Chore.cast(obj));
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
            lore.add(Type.CHARACTERISTIC.check + "§7特性:");
            characteristics.forEach(c -> lore.add(Type.CHARACTERISTIC.check + "§r- " + c.getName()));
        }
        // カテゴリ
        if (visibles.category) {
            if (!categorys.isEmpty()) {
                lore.add(Type.CATEGORY.check + "§7カテゴリ:");
                categorys.forEach(category -> lore.add(Type.CATEGORY.check + "§r- " + category.getName() + "§0" + Chore.createStridColor(category.toString())));
            }
        }

        // Lore終了
        if (visibles.end) {
            lore.add(Type.LORE_END.check);
        }
        meta.setLore(lore);

        // unbreaking & flag系
        Chore.addHideFlags(meta, am);

        // meta設定
        item.setItemMeta(meta);
        return item;
    }

    private static int getLevel(Map<AlchemyAttribute, Integer> levels, List<AlchemyIngredients> acls, AlchemyIngredients i) {
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
