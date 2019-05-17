/*
 * AlchemyKettle.java
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
package net.firiz.renewatelier.inventory.alchemykettle;

import net.firiz.renewatelier.alchemy.kettle.KettleItemManager;

import java.util.*;

import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.firiz.renewatelier.alchemy.kettle.AlchemyCircle;
import net.firiz.renewatelier.alchemy.kettle.BonusItem;
import net.firiz.renewatelier.alchemy.kettle.KettleBonusManager;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBox;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.material.Ingredients;
import net.firiz.renewatelier.alchemy.material.MaterialSize;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeLevelEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.StarEffect;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.item.drop.AlchemyResultDrop;
import net.firiz.renewatelier.player.PlayerSaveManager;
import net.firiz.renewatelier.player.PlayerStatus;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.DoubleData;
import net.firiz.renewatelier.utils.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author firiz
 */
public class AlchemyKettle {

    private final static KettleItemManager KETTLE = KettleItemManager.INSTANCE;
    private final static KettleBonusManager BONUSMANAGER = KettleBonusManager.INSTANCE;

    public static boolean isAlchemyKettle(final InventoryView view) {
        return view.getTitle().equals(AlchemyInventoryType.KETTLE_MAIN_MENU.getCheck());
    }

    public static void openKettle(final Player player, final AlchemyRecipe recipe, final Inventory catalystInv) {
        final Inventory inv = Bukkit.createInventory(player, 54, AlchemyInventoryType.KETTLE_MAIN_MENU.getCheck());
        final UUID uuid = player.getUniqueId();
        final PlayerInventory playerInv = player.getInventory();

        //プレイヤーコンテンツの設定
        final ItemStack[] contents = playerInv.getContents();
        KETTLE.setDefaultContents(uuid, contents); //contents.cloneの必要があるかも
        playerInv.setContents(new ItemStack[contents.length]);
        int pos = 3;
        for (int i = 0; i < 4; i++) {
            final List<ItemStack> pageItems = KETTLE.getPageItems(uuid, i);
            if (pageItems != null) {
                int item_pos = pos + 9;
                if (item_pos >= 36) {
                    item_pos = 3;
                }
                for (final ItemStack item : pageItems) {
                    final List<String> clores = AlchemyItemStatus.getLores(AlchemyItemStatus.CHARACTERISTIC, item);
                    if (clores != null) {
                        for (int j = 1; j < clores.size(); j++) {
                            final String clore = clores.get(j).substring(AlchemyItemStatus.CHARACTERISTIC.getCheck().length() + 4);
                            final Characteristic c = Characteristic.search(clore);
                            if (c != null) {
                                KETTLE.addCharacteristic(uuid, c, false);
                            }
                        }
                    }
                    playerInv.setItem(item_pos, item.clone());
                    item_pos++;
                }
                pos += 9;
                continue;
            }
            break;
        }

        // レシピレベルに基く回転ボタンの設定
        /*
        final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(uuid);
        final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());
        final List<RecipeLevelEffect> rles = recipe.getLevels().get(recipeStatus.getLevel());
        if (rles != null) {
            int damage = 0;
            search_rotations:
            for (final RecipeLevelEffect rle : rles) {
                switch (rle.getType()) {
                    case ADD_HORIZONTAL_ROTATION:
                        damage = 1504;
                        break search_rotations;
                    case ADD_VERTICAL_ROTATION:
                        damage = 1503;
                        break search_rotations;
                    case ADD_ROTATION:
                        damage = 1502;
                        break search_rotations;
                }
            }
            if (damage != 0) {
                final ItemStack rc_setting = Chore.ci(
                        Material.DIAMOND_AXE,
                        damage,
                        "",
                        null
                );
                inv.setItem(47, rc_setting);
            }
        }
         */
        // 回転ボタンをレシピレベルに関係なく使用可能に変更
        final ItemStack rc_setting = Chore.ci(
                Material.DIAMOND_AXE,
                1502,
                "",
                null
        );
        inv.setItem(47, rc_setting);

        final ItemStack settingItem = catalystInv.getItem(1);
        final ItemMeta setting = settingItem.getItemMeta();
        setting.addEnchant(Enchantment.ARROW_FIRE, 4, true); // center data
        setting.addEnchant(Enchantment.ARROW_INFINITE, 0, true); // right left turn data
        setting.addEnchant(Enchantment.ARROW_KNOCKBACK, 0, true); // characteristic page data
        settingItem.setItemMeta(setting);
        inv.setItem(1, settingItem);

        final int[] ss = new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0};
        int l = 0;
        StringBuilder sb = new StringBuilder();
        final List<String> lore = new ArrayList<>();
        for (final int n : ss) {
            l++;
            sb.append(String.valueOf(n)
                    .replaceAll("0", ChatColor.GRAY + Strings.W_W)
                    .replaceAll("1", ChatColor.WHITE + Strings.W_B)
            );
            if (l % 3 == 0) {
                lore.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        final ItemStack center_display = Chore.ci(Material.BARRIER, 0, ChatColor.WHITE + "中心点", lore);
        player.getInventory().setItem(19, center_display);
        inv.setItem(2, catalystInv.getItem(2).clone());
        setResultSlot(inv, player);
        player.openInventory(inv);
    }

    private static void setResultSlot(final Inventory inv, final Player player) {
        final UUID uuid = player.getUniqueId();
        final ItemMeta setting = inv.getItem(1).getItemMeta();
        final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(setting.getLore().get(0)));
        final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(uuid);
        final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());

        setKettleItems(inv, player, recipe);

        // レシピレベル追加効果 評価
        final Map<RecipeLevelEffect.RecipeLEType, Integer> recipe_effects = new LinkedHashMap<>();
        final Map<Integer, List<RecipeLevelEffect>> levels = recipe.getLevels();
        if (levels != null && !levels.isEmpty()) {
            final List<RecipeLevelEffect> effects = levels.get(recipeStatus.getLevel());
            if (effects != null && !effects.isEmpty()) {
                effects.forEach((e) -> recipe_effects.put(e.getType(), e.getCount()));
            }
        }

        // 触媒追加効果 評価
        // 確認・設定
        final ItemStack catalyst_item = KETTLE.getCatalyst(uuid);
        final List<CatalystBonus> old_CatalystBonusList = KETTLE.getCatalystBonusList(uuid);
        final Catalyst catalyst = catalyst_item != null ? AlchemyMaterial.getMaterial(catalyst_item).getCatalyst() : Catalyst.DEFAULT;
        final List<CatalystBonus> bonusList = catalyst.getBonus();
        final int size = bonusList.get(0).getCS().length;
        final int defslot = (size == 36 || size == 25 ? 3 : 13);
        resultBonus:
        for (final CatalystBonus bonus : bonusList) {
            final int[] cs = bonus.getCS();
            final CatalystBonusData data = bonus.getData();
            int slot = defslot;
            for (final int c : cs) {
                if (c != 0) {
                    final ItemStack item = inv.getItem(slot);
                    if (item != null) {
                        final int durability = Chore.getDamage(item);
                        final int defdamage = Catalyst.getDamage(c);
                        boolean stop = false;
                        if (durability == defdamage) {
                            stop = true;
                        } else {
                            final AlchemyCircle circle = AlchemyCircle.sertchData(defdamage);
                            if (circle != AlchemyCircle.WHITE && circle.getCircleType() != AlchemyCircle.sertchData(durability).getCircleType()) {
                                stop = true;
                            }
                        }
                        if (stop) {
                            final boolean remove = KETTLE.removeCatalystBonus(uuid, bonus);
                            if (remove) {
                                switch (data.getType()) {
                                    case CHARACTERISTIC: {
                                        final Characteristic cdata = (Characteristic) data.getY();
                                        KETTLE.removeCatalystCharacteristic(uuid, cdata);
                                        KETTLE.removeSelectCharacteristic(uuid, cdata);
                                        break;
                                    }
                                    case INHERITING: {
                                        KETTLE.resetSelectCharacteristic(uuid);
                                        break;
                                    }
                                }
                            }
                            continue resultBonus;
                        }
                    }
                }
                slot = Catalyst.nextSlot(slot, size);
            }
            if (!KETTLE.hasCatalystBonus(uuid, bonus)) {
                KETTLE.addCatalystBonus(uuid, bonus);
            }
        }
        final List<CatalystBonus> bonusDatas = KETTLE.getCatalystBonusList(uuid);
        int bonus_quality = 0;
        int bonus_quality_percent = 0;
        int bonus_amount = 0;
        int bonus_inheriting = 0;
        int bonus_size = 0;
        if (bonusDatas != null) {
            for (final CatalystBonus bonus : bonusDatas) {
                final CatalystBonusData bonusData = bonus.getData();
                switch (bonusData.getType()) {
                    case QUALITY:
                        bonus_quality += bonusData.getX();
                        break;
                    case QUALITY_PERCENT:
                        bonus_quality_percent += bonusData.getX();
                        break;
                    case AMOUNT:
                        bonus_amount += bonusData.getX();
                        break;
                    case CHARACTERISTIC:
                        KETTLE.addCharacteristic(uuid, (Characteristic) bonusData.getY(), true);
                        break;
                    case INHERITING:
                        bonus_inheriting += bonusData.getX();
                        break;
                    case SIZE:
                        bonus_size += bonusData.getX();
                        break;
                }
            }
        }

        // 品質 評価
        final KettleBox kettleBox = KETTLE.getKettleData(uuid);
        int all_quality = 0;
        if (kettleBox != null) {
            final List<ItemStack> kettleItems = kettleBox.getItemStacks();
            if (kettleItems != null && !kettleItems.isEmpty()) {
                all_quality = kettleItems.stream().map(AlchemyItemStatus::getQuality).reduce(all_quality, Integer::sum);
                if (all_quality != 0) {
                    all_quality /= kettleItems.size();
                }
            }
        }
        int item_count = 0;
        int item_size = 0;
        for (int i = 0; i < recipe.getReqMaterial().size(); i++) {
            for (final ItemStack item : KETTLE.getPageItems(uuid, i)) {
                item_size += MaterialSize.getSizeCount(item);
                item_count++;
            }
        }
        item_size = Math.round((float) item_size / item_count);
        item_size += bonus_size;

        final Integer add_quality = recipe_effects.get(RecipeLevelEffect.RecipeLEType.ADD_QUALITY);
        all_quality += (add_quality != null ? add_quality : 0) + bonus_quality; // +品質固定値
        all_quality += Math.round(all_quality * (bonus_quality_percent * 0.01)); // +品質％値

        ItemStack resultItem = null;
        final String result_str = recipe.getResult();
        final Integer add_amount = recipe_effects.get(RecipeLevelEffect.RecipeLEType.ADD_AMOUNT);
        if (result_str.startsWith("material:")) {
            final AlchemyMaterial result = AlchemyMaterial.getMaterial(result_str.substring(9));

            // カテゴリ 評価 - カテゴリ追加の触媒効果などを実装後
            final List<Category> categorys = new ArrayList<>(result.getCategorys());
            // 錬金成分 評価
            final List<Ingredients> ings = recipe.getDefaultIngredients();
            for (final RecipeEffect effect : recipe.getEffects()) {
                final StarEffect activeEffect = effect.getActiveEffect(uuid);
                if (activeEffect != null) {
                    switch (activeEffect.getType()) {
                        case INGREDIENT:
                            final AlchemyIngredients ingredient = activeEffect.getIngredient();
                            if (ingredient != null && !ings.contains(ingredient)) {
                                ings.add(ingredient);
                            }
                            break;
                        case CATEGORY:
                            final Category category = activeEffect.getCategory();
                            if (category != null && !categorys.contains(category)) {
                                categorys.add(category);
                            }
                            break;
                    }
                }
            }

            // 錬金成分・非マイナス化・1番目100%+星効果ならいらないかもしれない
            // サイズ 評価 サイズ+xがない限り必要ない
            // 特性 評価 +特性がない限り必要ない
            final List<Characteristic> characteristics = new ArrayList<>();

            // 触媒設定 - 触媒の設定がなされているアイテムだった場合 - いらんかも
            // アイテムの作成
            resultItem = AlchemyItemStatus.getItem(
                    result,
                    ings, // 錬金属性 書き換え
                    Chore.createDamageableItem(
                            result.getMaterial().getLeft(),
                            recipe.getAmount() + (add_amount != null ? add_amount : 0) + bonus_amount,
                            result.getMaterial().getRight()
                    ),
                    all_quality, // 品質 書き換え
                    item_size <= 1
                            ? MaterialSize.S1_1.getSize(0)
                            : (item_size >= 9 ? MaterialSize.S9_1.getSize(0) : recipe.getSizes().get(item_size - 2).getSize()), // サイズ 書き換え
                    characteristics, // 特性 書き換え
                    result.getCategorys().equals(categorys) ? new ArrayList<>() : categorys, // カテゴリ 書き換え
                    true
            );
        } else if (result_str.startsWith("minecraft:")) { // 基本想定しない
            Material material = Material.matchMaterial(result_str);
            if (material == null) {
                material = Material.matchMaterial(result_str, true);
            }
            resultItem = new ItemStack(
                    material,
                    recipe.getAmount() + (add_amount != null ? add_amount : 0)
            );
        }

        if (resultItem != null) {
            final ItemMeta meta = resultItem.getItemMeta();
            final List<String> lore = meta.getLore();

            // ボーナス項目
            final StringBuilder sb_bonus = new StringBuilder();
            for (final AlchemyAttribute aa : AlchemyAttribute.values()) {
                sb_bonus.append(aa.getColor()).append(BONUSMANAGER.getBonus(player, aa)).append("% ");
            }
            lore.add(2, sb_bonus.toString());
            lore.add(3, BONUSMANAGER.getBar(player.getUniqueId(), recipe.getReqbar()));

            // 効果項目
            lore.add(4, ChatColor.GRAY + "効果:");
            int loreslot = 5;
            if (recipe.getEffects().isEmpty()) {
                lore.add(loreslot, ChatColor.RESET + "なし");
                loreslot++;
            } else {
                for (final RecipeEffect effect : recipe.getEffects()) {
                    final String name = effect.getName(uuid);
                    lore.add(loreslot, effect.getAttribute().getColor().concat("・") + (name == null ? ChatColor.RESET + "なし" : name));
                    lore.add(loreslot + 1, "  ".concat(effect.getStar(uuid)));
                    loreslot += 2;
                }
            }

            // 触媒効果項目
            lore.add(loreslot, ChatColor.GRAY + "触媒効果:");
            if (bonusDatas != null && !bonusDatas.isEmpty()) {
                for (final CatalystBonus bonus : bonusDatas) {
                    loreslot++;
                    lore.add(loreslot, ChatColor.GREEN + "- ".concat(bonus.getData().getName()));
                }
            } else {
                loreslot++;
                lore.add(loreslot, ChatColor.RESET + "なし");
            }

            // 特性項目
            final Integer inheriting = recipe_effects.get(RecipeLevelEffect.RecipeLEType.ADD_INHERITING);
            lore.add(lore.size() - 1, ChatColor.GRAY + "特性:");
            final int cslot = Math.min(3, (inheriting == null ? 0 : inheriting) + bonus_inheriting);
            if (cslot == 0) {
                lore.add(lore.size() - 1, ChatColor.RESET + "特性引継ぎスロットなし");
            } else {
                final List<Characteristic> cs = KETTLE.getCharacteristics(uuid);
                int count = 0;
                if (cs != null) {
                    for (final Characteristic c : cs) {
                        if (KETTLE.isSelectCharacteristic(uuid, c)) {
                            lore.add(lore.size() - 1, ChatColor.RESET + "- " + c.getName());
                            count++;
                        }
                    }
                }
                for (int i = count; i < cslot; i++) {
                    lore.add(lore.size() - 1, ChatColor.RESET + "- なし");
                }
            }

            meta.setLore(lore);
            resultItem.setItemMeta(meta);
            inv.setItem(46, resultItem);
            setCharacteristicPage(inv, player, 0);
        }
    }

    private static void setKettleItems(final Inventory inv, final Player player, final AlchemyRecipe recipe) {
        // 触媒の設置
        final ItemStack citem = KETTLE.getCatalyst(player.getUniqueId());
        Catalyst catalyst;
        if (citem == null) {
            catalyst = Catalyst.DEFAULT;
        } else {
            catalyst = AlchemyMaterial.getMaterial(citem).getCatalyst();
        }
        catalyst.setInv(inv, recipe, true);

        // CSアイテムの配置
        final UUID uuid = player.getUniqueId();
        final KettleBox box = KETTLE.getKettleData(uuid);
        if (box != null) {
            int j = (box.getCSize() == 36 || box.getCSize() == 25 ? 3 : 13);

            final List<CatalystBonus> bonusList = catalyst.getBonus();
            final List<CatalystBonus> ignores = new ArrayList<>();
            for (final CatalystBonus cbonus : bonusList) {
                if (cbonus.getData().getType().isOnce() && box.usedBonus(cbonus)) {
                    ignores.add(cbonus);
                }
            }
            for (int i = 0; i < bonusList.get(0).getCS().length; i++) {
                ItemStack slotItem = null;
                final int size = bonusList.get(0).getCS().length;
                final int defslot = (size == 36 || size == 25 ? 3 : 13);
                getSlotItem:
                for (final CatalystBonus b : bonusList) {
                    int slot = defslot;
                    for (int c2 : b.getCS()) {
                        if (j == slot) {
                            short itemDamage = Catalyst.getDamage(c2);
                            if (itemDamage != -1) {
                                slotItem = ignores.contains(b) ? null : Chore.ci(
                                        Material.DIAMOND_AXE,
                                        itemDamage,
                                        ChatColor.RESET + b.getData().getName(),
                                        b.getData().getDesc()
                                );
                                break getSlotItem;
                            }
                        }
                        slot = Catalyst.nextSlot(slot, size);
                    }
                }
                inv.setItem(j, slotItem);
                j = Catalyst.nextSlot(j, box.getCSize());
            }

            final Map<DoubleData<Integer, BonusItem>, Integer> resultCS = box.getResultCS();
            resultCS.keySet().forEach((slotData) -> {
                final String color = AlchemyIngredients.getAllLevel(slotData.getRight().getItem()).getRight()[0].getColor();
                inv.setItem(slotData.getLeft(), AlchemyCircle.getCircle(color, inv.getItem(slotData.getLeft())));
            });
        }
    }

    private static void setCharacteristicPage(final Inventory inv, final Player player, final int move) {
        final ItemStack setting = inv.getItem(1);
        final ItemMeta meta = setting.getItemMeta();
        final int page = meta.getEnchantLevel(Enchantment.ARROW_KNOCKBACK);
        final int new_page = Math.max(1, page + move);
        final UUID uuid = player.getUniqueId();
        final List<Characteristic> cs = KETTLE.getCharacteristics(uuid);
        if (cs == null) {
            return;
        }
        final List<List<Characteristic>> lists = new ArrayList<>();
        final List<Characteristic> list = new ArrayList<>();
        int i = 0;
        for (final Characteristic c : cs) {
            if (i % 6 == 0) {
                lists.add(new ArrayList<>(list));
                list.clear();
            }
            list.add(c);
            i++;
        }
        if (!list.isEmpty()) {
            lists.add(list);
        }
        if (!lists.isEmpty()) {
            boolean check = false;
            if (lists.size() > new_page) {
                check = true;
                meta.addEnchant(Enchantment.ARROW_KNOCKBACK, new_page, true);
                setting.setItemMeta(meta);
            }

            final int startslot = 8;
            int del_slot = 1;
            for (int j = 0; j < 3; j++) {
                for (int l = 0; l < 2; l++) {
                    inv.setItem(startslot + del_slot, null);
                    del_slot++;
                }
                del_slot += startslot - 1;
            }

            int slot = 1;
            int count = 0;
            for (final Characteristic c : lists.get(check ? new_page : page)) {
                final List<String> booklore = new ArrayList<>();
                final boolean on = KETTLE.isSelectCharacteristic(uuid, c);
                booklore.add(ChatColor.GRAY + (on ? "削除" : "追加"));
                booklore.add(ChatColor.GRAY + c.getMsg());
                inv.setItem(startslot + slot, Chore.ci(on ? Material.ENCHANTED_BOOK : Material.BOOK, 0, ChatColor.RESET + c.getName(), booklore));

                if (count > 0) {
                    count = 0;
                    slot += startslot;
                } else {
                    slot++;
                    count++;
                }
            }
        }
    }

    public static void click(InventoryClickEvent e) {
        if (e.isShiftClick()) {
            e.setCancelled(true);
        }

        final Inventory inv = e.getInventory();
        final int raw = e.getRawSlot();
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        switch (e.getSlotType()) {
            //<editor-fold defaultstate="collapsed" desc="case QUICKBAR">
            case QUICKBAR: {
                if (raw >= 81 && raw <= 83) {
                    e.setCancelled(true);
                    final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                    final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1, xyz[2]);
                    player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    final KettleBox kettleBox = KETTLE.getKettleData(uuid);
                    if (kettleBox != null) {
                        final ItemMeta setting = inv.getItem(1).getItemMeta();
                        final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(setting.getLore().get(0)));
                        int req_count = 0;
                        for (int i = 0; i < recipe.getReqMaterial().size(); i++) {
                            req_count += KETTLE.getPageItems(uuid, i).size();
                        }
                        if (kettleBox.getItems() != null && kettleBox.getItems().size() == req_count) {
                            final ItemStack result_slot_item = inv.getItem(46);
                            final int quality = AlchemyItemStatus.getQuality(result_slot_item);

                            ItemStack resultItem = null;
                            final String result_str = recipe.getResult();
                            if (result_str.startsWith("material:")) {
                                final AlchemyMaterial result = AlchemyMaterial.getMaterial(result_str.substring(9));

                                final List<Characteristic> characteristics = KETTLE.getSelectCharacteristics(uuid);
                                final List<Ingredients> ings = AlchemyItemStatus.getIngredients(result_slot_item);
                                final List<Category> categorys = AlchemyItemStatus.getCategorys(result_slot_item);

                                final List<String> activeEffects = new ArrayList<>();
                                recipe.getEffects().stream().map((effect) -> effect.getActiveEffect(uuid)).filter(Objects::nonNull).filter((activeEffect) -> (activeEffect.getType() == StarEffect.StarEffectType.NAME)).forEachOrdered((activeEffect) -> activeEffects.add(activeEffect.getName()));

                                // アイテムの作成
                                resultItem = AlchemyItemStatus.getItem(
                                        result,
                                        ings, // 錬金属性 書き換え
                                        Chore.createDamageableItem(
                                                result.getMaterial().getLeft(),
                                                result_slot_item.getAmount(),
                                                result.getMaterial().getRight()
                                        ),
                                        quality, // 品質 書き換え
                                        MaterialSize.getSize(result_slot_item), // サイズ 書き換え
                                        activeEffects, // 発現効果
                                        characteristics == null ? new ArrayList<>() : characteristics, // 特性 書き換え
                                        categorys.isEmpty() ? null : categorys, // カテゴリ 書き換え
                                        false
                                );
                            } else if (result_str.startsWith("minecraft:")) { // 基本想定しない
                                Material material = Material.matchMaterial(result_str);
                                if (material == null) {
                                    material = Material.matchMaterial(result_str, true);
                                }
                                resultItem = new ItemStack(
                                        material,
                                        result_slot_item.getAmount()
                                );
                            }
                            if (resultItem != null) {
                                KETTLE.reset(player);
                                player.closeInventory();
                                final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(uuid);
                                status.addRecipeExp(player, false, recipe, status.getRecipeStatus(recipe.getId()).getLevel() != 0 ? GameConstants.RECIPE_EXP : 0);
                                new AlchemyResultDrop(loc, resultItem).start();
                            }
                        }
                    }
                }
                break;
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="case CONTAINER">
            case CONTAINER: {
                switch (raw) {
                    case 54:
                    case 55:
                    case 56: {
                        //<editor-fold defaultstate="collapsed" desc="左右反転・上下反転・回転">
                        Chore.log("左右反転・上下反転・回転");
                        e.setCancelled(true);
                        final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);

                        final ItemStack settingItem = inv.getItem(1);
                        final ItemStack button = inv.getItem(47);
                        if (settingItem != null && button != null) {
                            final ItemMeta setting = settingItem.getItemMeta();
                            final int sl = setting.getEnchantLevel(Enchantment.ARROW_INFINITE);
                            final int damage = Chore.getDamage(button);
                            switch (damage) {
                                case 1504: // 左右
                                    setting.addEnchant(
                                            Enchantment.ARROW_INFINITE,
                                            sl == 0 ? 1 : 0,
                                            true
                                    );
                                    break;
                                case 1503: // 上下
                                    setting.addEnchant(
                                            Enchantment.ARROW_INFINITE,
                                            sl == 0 ? 1 : 0,
                                            true
                                    );
                                    break;
                                case 1502: // 回転
                                    setting.addEnchant(
                                            Enchantment.ARROW_INFINITE,
                                            (sl + 1 >= 4) ? 0 : sl + 1,
                                            true
                                    );
                                    break;
                            }
                            settingItem.setItemMeta(setting);
                        }
                        break;
                        //</editor-fold>
                    }
                    case 63:
                    case 64:
                    case 65: {
                        //<editor-fold defaultstate="collapsed" desc="中心点移動">
                        Chore.log("中心点移動");
                        e.setCancelled(true);
                        final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                        final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1, xyz[2]);
                        player.playSound(loc, Sound.UI_BUTTON_CLICK, 0.1f, 1);

                        final ItemMeta setting = inv.getItem(1).getItemMeta();
                        int new_center = setting.getEnchantLevel(Enchantment.ARROW_FIRE) + (e.isRightClick() ? -1 : 1);
                        if (new_center <= -1 || new_center >= 9) {
                            new_center = e.isRightClick() ? 8 : 0;
                        }
                        setting.addEnchant(Enchantment.ARROW_FIRE, new_center, true);
                        inv.getItem(1).setItemMeta(setting);

                        final int[] ss = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                        ss[new_center] = 1;
                        int l = 0;
                        StringBuilder sb = new StringBuilder();
                        final List<String> lore = new ArrayList<>();
                        for (int n : ss) {
                            l++;
                            sb.append(String.valueOf(n)
                                    .replaceAll("0", ChatColor.GRAY + Strings.W_W)
                                    .replaceAll("1", ChatColor.WHITE + Strings.W_B)
                            );
                            if (l % 3 == 0) {
                                lore.add(sb.toString());
                                sb = new StringBuilder();
                            }
                        }
                        final ItemMeta center_display = player.getInventory().getItem(19).getItemMeta();
                        center_display.setLore(lore);
                        player.getInventory().getItem(19).setItemMeta(center_display);
                        break;
                        //</editor-fold>
                    }
                    case 72:
                    case 73:
                    case 74: {
                        //<editor-fold defaultstate="collapsed" desc="戻す">
                        Chore.log("戻す");
                        e.setCancelled(true);
                        final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());

                        final KettleBox kettleBox = KETTLE.getKettleData(uuid);
                        if (kettleBox != null) {
                            final DoubleData<BonusItem, Map<Integer, Integer>> backData = kettleBox.backData();
                            if (backData != null) {
                                final PlayerInventory pinv = player.getInventory();
                                boolean check = false;
                                int slot = 12;
                                setitemloop:
                                for (int i = 0; i < 3; i++) {
                                    for (int j = 0; j < 6; j++) {
                                        if (pinv.getItem(slot) == null) {
                                            pinv.setItem(slot, backData.getLeft().getItem());
                                            check = true;
                                            break setitemloop;
                                        }
                                        slot++;
                                    }
                                    slot += 4;
                                }
                                if (!check) {
                                    for (int i = 3; i < 9; i++) { // 3~8 slots
                                        if (pinv.getItem(i) == null) {
                                            pinv.setItem(i, backData.getLeft().getItem());
                                            break;
                                        }
                                    }
                                }
                                BONUSMANAGER.back(uuid);
                                setResultSlot(inv, player);
                                player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                                break;
                            }
                        }
                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                        break;
                        //</editor-fold>
                    }
                    case 11: {
                        //<editor-fold defaultstate="collapsed" desc="特性一覧 ページ移動-上">
                        e.setCancelled(true);
                        final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                        final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1, xyz[2]);
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        setCharacteristicPage(inv, player, -1);
                        Chore.log("特性一覧 ページ移動-上");
                        break;
                        //</editor-fold>
                    }
                    case 29: {
                        //<editor-fold defaultstate="collapsed" desc="特性一覧 ページ移動-下">
                        e.setCancelled(true);
                        final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                        final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1, xyz[2]);
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        setCharacteristicPage(inv, player, 1);
                        Chore.log("特性一覧 ページ移動-下");
                        break;
                        //</editor-fold>
                    }
                    default: {
                        //<editor-fold defaultstate="collapsed" desc="default">
                        if (raw < 54) {
                            if ((raw >= 9 && raw <= 10)
                                    || (raw >= 18 && raw <= 19)
                                    || (raw >= 27 && raw <= 28)) {
                                //<editor-fold defaultstate="collapsed" desc="特性 追加・削除">
                                e.setCancelled(true);
                                final ItemStack item = inv.getItem(raw);
                                final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                                final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1, xyz[2]);
                                if (item != null && (item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK)) {
                                    Chore.log("特性 追加・削除");
                                    final ItemMeta setting = inv.getItem(1).getItemMeta();
                                    final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(setting.getLore().get(0)));
                                    final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(uuid);
                                    final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());
                                    final List<RecipeLevelEffect> effects = recipe.getLevels().get(recipeStatus.getLevel());
                                    if (effects != null) {
                                        final Characteristic c = Characteristic.search(item.getItemMeta().getDisplayName().substring(2));
                                        final boolean change_on = !KETTLE.isSelectCharacteristic(uuid, c);
                                        if (change_on) {
                                            int inheriting = 0;
                                            inheriting = effects.stream().filter((effect) -> (effect.getType() == RecipeLevelEffect.RecipeLEType.ADD_INHERITING)).map(RecipeLevelEffect::getCount).reduce(inheriting, Integer::sum);
                                            final List<CatalystBonus> catalystBonusList = KETTLE.getCatalystBonusList(uuid);
                                            if (catalystBonusList != null) {
                                                inheriting = catalystBonusList.stream().filter((cbd) -> (cbd.getData().getType() == CatalystBonusData.BonusType.INHERITING)).map((cbd) -> cbd.getData().getX()).reduce(inheriting, Integer::sum);
                                            }
                                            final List<Characteristic> scs = KETTLE.getSelectCharacteristics(uuid);
                                            final int count = scs != null ? scs.size() : 0;
                                            if (count < Math.min(3, inheriting)) {
                                                Chore.log("特性 追加");
                                                player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, change_on ? 0.5f : 1);
                                                KETTLE.addSelectCharacteristic(uuid, c);
                                                Chore.log(KETTLE.getCharacteristics(uuid));
                                            } else {
                                                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                                return;
                                            }
                                        } else {
                                            Chore.log("特性 削除");
                                            KETTLE.removeSelectCharacteristic(uuid, c);
                                        }
                                        setResultSlot(inv, player);
                                        return;
                                    }
                                }
                                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                //</editor-fold>
                            } else {
                                //<editor-fold defaultstate="collapsed" desc="アイテムを錬金釜に投入">
                                e.setCancelled(true);
                                final ItemStack cursor = e.getCursor();
                                final AlchemyMaterial material = AlchemyMaterial.getMaterial(cursor);
                                if (material != null) {
                                    int[] size = MaterialSize.getSize(cursor);
                                    if (size != null) {
                                        final int center = inv.getItem(1).getEnchantmentLevel(Enchantment.ARROW_FIRE);
                                        final ItemStack rc_setting = inv.getItem(47);
                                        if (rc_setting != null) {
                                            final int damage = Chore.getDamage(rc_setting);
                                            switch (damage) {
                                                case 1504: { // 左右
                                                    if (1 == inv.getItem(1).getEnchantmentLevel(Enchantment.ARROW_INFINITE)) {
                                                        size = MaterialSize.right_left_turn(size);
                                                    }
                                                    break;
                                                }
                                                case 1503: { // 上下
                                                    if (1 == inv.getItem(1).getEnchantmentLevel(Enchantment.ARROW_INFINITE)) {
                                                        size = MaterialSize.up_down_turn(size);
                                                    }
                                                    break;
                                                }
                                                case 1502: { // 回転
                                                    int loop = inv.getItem(1).getEnchantmentLevel(Enchantment.ARROW_INFINITE);
                                                    for (int i = 0; i < loop; i++) {
                                                        size = MaterialSize.right_rotation(size);
                                                    }
                                                    break;
                                                }
                                            }
                                        }

                                        final int[] sets = {
                                            -10, -9, -8,
                                            -1, 0, 1,
                                            8, 9, 10
                                        };
                                        final int[] center_sets = {
                                            10, 9, 8,
                                            1, 0, -1,
                                            -8, -9, -10
                                        };

                                        final ItemStack catalyst_item = KETTLE.getCatalyst(uuid);
                                        final Catalyst catalyst = catalyst_item != null ? AlchemyMaterial.getMaterial(catalyst_item).getCatalyst() : Catalyst.DEFAULT;
                                        final int csize = catalyst.getBonus().get(0).getCS().length;
                                        Map<Integer, Integer> rslots = new HashMap<>();
                                        for (int i = 0; i < size.length; i++) {
                                            final int value = size[i];
                                            final int slot = raw + sets[i] + center_sets[center];
                                            if (value != 0) {
                                                boolean check = false;
                                                switch (csize) {
                                                    case 36: // 6x6
                                                        check = (slot >= 3 && slot < 9)
                                                                || (slot >= 12 && slot < 18)
                                                                || (slot >= 21 && slot < 27)
                                                                || (slot >= 30 && slot < 36)
                                                                || (slot >= 39 && slot < 45)
                                                                || (slot >= 48 && slot < 54);
                                                        break;
                                                    case 25: // 5x5
                                                        check = (slot >= 3 && slot < 8)
                                                                || (slot >= 12 && slot < 17)
                                                                || (slot >= 21 && slot < 26)
                                                                || (slot >= 30 && slot < 35)
                                                                || (slot >= 39 && slot < 44);
                                                        break;
                                                    case 16: // 4x4
                                                        check = (slot >= 13 && slot < 17)
                                                                || (slot >= 22 && slot < 26)
                                                                || (slot >= 31 && slot < 35)
                                                                || (slot >= 40 && slot < 44);
                                                        break;
                                                }

                                                if (check) {
                                                    rslots.put(slot, value);
                                                } else {
                                                    rslots = null;
                                                    break;
                                                }
                                            }
                                        }

                                        final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                                        final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1, xyz[2]);
                                        if (rslots == null) {
                                            Chore.log("アイテムを錬金釜に投入できない");
                                            player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                        } else {
                                            Chore.log("アイテムを錬金釜に投入できる");
                                            final ItemMeta setting = inv.getItem(1).getItemMeta();
                                            final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(setting.getLore().get(0)));
                                            final ItemStack clone = cursor.clone();
                                            clone.setAmount(1);
                                            final DoubleData<Integer, AlchemyAttribute[]> allLevel = AlchemyIngredients.getAllLevel(clone);
                                            BONUSMANAGER.addBar(
                                                    uuid,
                                                    recipe.getReqbar(),
                                                    allLevel.getLeft(),
                                                    allLevel.getRight()
                                            );
                                            cursor.setAmount(cursor.getAmount() - 1);
                                            KETTLE.addKettleData(uuid, clone, csize, rslots); // 配置いる
                                            setResultSlot(inv, player);
                                        }
                                    }
                                }
                                //</editor-fold>
                            }
                        } else {
                            Chore.log(raw);
                            // always falses
                            if (/*(raw >= 54 && raw <= 56)
                                    || */(raw >= 63 && raw <= 65)
                                    /*|| (raw >= 72 && raw <= 74)*/
                                    || (raw >= 81 && raw <= 83)) {
                                Chore.log("キャンセル");
                                e.setCancelled(true);
                            }
                        }
                        break;
                        //</editor-fold>
                    }
                }
                break;
            }
            //</editor-fold>
        }
    }

    public static void drag(final InventoryDragEvent e) {
        e.setCancelled(true);
    }

    public static void close(final InventoryCloseEvent e) {
        KETTLE.allBack((Player) e.getPlayer());
    }

    public static void pickup(final EntityPickupItemEvent e) {
        final Player player = (Player) e.getEntity();
        if (KETTLE.isOpenKettle(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
