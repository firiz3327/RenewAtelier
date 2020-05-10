package net.firiz.renewatelier.inventory.alchemykettle;

import net.firiz.renewatelier.alchemy.kettle.KettleItemManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.firiz.renewatelier.alchemy.kettle.AlchemyCircle;
import net.firiz.renewatelier.alchemy.kettle.bonus.BonusItem;
import net.firiz.renewatelier.alchemy.kettle.bonus.KettleBonusManager;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBox;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBoxCircleData;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.material.MaterialSize;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeLevelEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.StarEffect;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.inventory.manager.BiParamInventory;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.item.drop.AlchemyResultDrop;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.pair.Pair;
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
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public class AlchemyKettle implements BiParamInventory<AlchemyRecipe, Inventory> {

    private static final KettleItemManager KETTLE = KettleItemManager.INSTANCE;
    private static final KettleBonusManager BONUS_MANAGER = KettleBonusManager.INSTANCE;

    private static final String STR_CENTER = "アイテムを投入してください";
    private static final String STR_TURN = "投入すると、完成品プレビューが更新されます";
    private static final String STR_CHARACTERISTIC = "入れ方によって様々な状態に変わるので";
    private static final String STR_RECIPE_ID = "色々試してみましょう!";
    private static final String STR_TURN2 = "";

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().equals(AlchemyInventoryType.KETTLE_MAIN_MENU.getCheck());
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final AlchemyRecipe recipe, @NotNull final Inventory catalystInv) {
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
            if (!pageItems.isEmpty()) {
                int itemPos = pos + 9;
                if (itemPos >= 36) {
                    itemPos = 3;
                }
                for (final ItemStack item : pageItems) {
                    AlchemyItemStatus.getCharacteristics(item).forEach(c -> KETTLE.addCharacteristic(uuid, c, false));
                    playerInv.setItem(itemPos, item.clone());
                    itemPos++;
                }
                pos += 9;
                continue;
            }
            break;
        }

        final List<String> rotateLore = new ArrayList<>();
        rotateLore.add(ChatColor.GRAY + "現在： " + GameConstants.ROTATION_STR[0]);
        rotateLore.add(ChatColor.GRAY + GameConstants.TURN_STR[0][0]);
        rotateLore.add(ChatColor.GRAY + GameConstants.TURN_STR[0][1]);
        playerInv.setItem(10, Chore.ci(Material.BARRIER, 0, ChatColor.RESET + "回転", rotateLore));

        final ItemStack settingItem = catalystInv.getItem(1);
        final ItemMeta setting = settingItem.getItemMeta();
        AlchemyChore.setSetting(setting, 0, 4, STR_CENTER); // center data
        AlchemyChore.setSetting(setting, 1, 0, STR_TURN); // rotation data
        AlchemyChore.setSetting(setting, 2, 0, STR_CHARACTERISTIC); // characteristic page data
        AlchemyChore.setSettingStr(setting, 3, recipe.getId(), STR_RECIPE_ID); // recipe id
        AlchemyChore.setSetting(setting, 4, 0, STR_TURN2); // right left up down turn data
//        setting.addEnchant(Enchantment.ARROW_FIRE, 4, true); // center data
//        setting.addEnchant(Enchantment.ARROW_INFINITE, 0, true); // right left turn data
//        setting.addEnchant(Enchantment.ARROW_KNOCKBACK, 0, true); // characteristic page data
        settingItem.setItemMeta(setting);
        inv.setItem(1, settingItem);

        final int[] ss = new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0};
        int l = 0;
        StringBuilder sb = new StringBuilder();
        final List<String> lore = new ArrayList<>();
        for (final int n : ss) {
            l++;
            sb.append(String.valueOf(n)
                    .replace("0", ChatColor.GRAY + GameConstants.W_W)
                    .replace("1", ChatColor.WHITE + GameConstants.W_B)
            );
            if (l % 3 == 0) {
                lore.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        final ItemStack centerDisplay = Chore.ci(Material.BARRIER, 0, ChatColor.WHITE + "中心点", lore);
        player.getInventory().setItem(19, centerDisplay);
        inv.setItem(2, catalystInv.getItem(2).clone());
        setResultSlot(inv, player);
        player.openInventory(inv);
    }

    private void setResultSlot(final Inventory inv, final Player player) {
        final UUID uuid = player.getUniqueId();
        final ItemMeta setting = inv.getItem(1).getItemMeta();
        final AlchemyRecipe recipe = AlchemyRecipe.search(AlchemyChore.getSettingStr(setting, 3));
        final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
        final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());

        setKettleItems(inv, player, recipe);

        // レシピレベル追加効果 評価
        final Map<RecipeLevelEffect.RecipeLEType, Integer> recipeEffects = new LinkedHashMap<>();
        final Map<Integer, List<RecipeLevelEffect>> levels = recipe.getLevels();
        if (levels != null && !levels.isEmpty()) {
            final List<RecipeLevelEffect> effects = levels.get(recipeStatus.getLevel());
            if (effects != null && !effects.isEmpty()) {
                effects.forEach(e -> recipeEffects.put(e.getType(), e.getCount()));
            }
        }

        // 触媒追加効果 評価
        // 確認・設定
        final ItemStack catalystItem = KETTLE.getCatalyst(uuid);
        final Catalyst catalyst = catalystItem != null ? AlchemyMaterial.getMaterial(catalystItem).getCatalyst() : Catalyst.getDefaultCatalyst();
        final List<CatalystBonus> bonusList = catalyst.getBonus();
        final int size = bonusList.get(0).getCS().length;
        final int defslot = (size == 36 || size == 25 ? 3 : 13);
        resultBonus:
        for (final CatalystBonus bonus : bonusList) {
            final int[] cs = bonus.getCS();
            final CatalystBonusData data = bonus.getData();
            int slot = defslot;
            final List<Integer> activeSlots = new ArrayList<>();
            for (final int c : cs) {
                if (c != 0) {
                    final ItemStack item = inv.getItem(slot);
                    if (item != null) {
                        final int customModel = Chore.getCustomModelData(item);
                        final int defcmd = Catalyst.getCustomModelData(c);
                        boolean stop = false;
                        if (customModel == defcmd) {
                            stop = true;
                        } else {
                            final AlchemyCircle circle = AlchemyCircle.sertchData(defcmd);
                            if (circle != AlchemyCircle.WHITE && circle.getCircleType() != AlchemyCircle.sertchData(customModel).getCircleType()) {
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
                                    default:
                                        break;
                                }
                            }
                            activeSlots.clear();
                            continue resultBonus;
                        }
                        activeSlots.add(slot);
                    }
                }
                slot = Catalyst.nextSlot(slot, size);
            }
            if (!KETTLE.hasCatalystBonus(uuid, bonus)) {
                KETTLE.addCatalystBonus(uuid, bonus);
            }
            // アクティブ触媒効果にGlowを付与
            activeSlots.forEach(activeSlot -> Objects.requireNonNull(inv.getItem(activeSlot)).addUnsafeEnchantment(Enchantment.LUCK, 1));
        }
        final List<CatalystBonus> bonusDatas = KETTLE.getCatalystBonusList(uuid);
        int bonusQuality = 0;
        int bonusQualityPercent = 0;
        int bonusAmount = 0;
        int bonusInheriting = 0;
        int bonusSize = 0;
        if (bonusDatas != null) {
            for (final CatalystBonus bonus : bonusDatas) {
                final CatalystBonusData bonusData = bonus.getData();
                switch (bonusData.getType()) {
                    case QUALITY:
                        bonusQuality += bonusData.getX();
                        break;
                    case QUALITY_PERCENT:
                        bonusQualityPercent += bonusData.getX();
                        break;
                    case AMOUNT:
                        bonusAmount += bonusData.getX();
                        break;
                    case CHARACTERISTIC:
                        KETTLE.addCharacteristic(uuid, (Characteristic) bonusData.getY(), true);
                        break;
                    case INHERITING:
                        bonusInheriting += bonusData.getX();
                        break;
                    case SIZE:
                        bonusSize += bonusData.getX();
                        break;
                    default:
                        break;
                }
            }
        }

        // 品質 評価
        final KettleBox kettleBox = KETTLE.getKettleData(uuid);
        int allQuality = 0;
        if (kettleBox != null) {
            final List<ItemStack> kettleItems = kettleBox.getItemStacks();
            if (!kettleItems.isEmpty()) {
                allQuality = kettleItems.stream().map(AlchemyItemStatus::getQuality).reduce(allQuality, Integer::sum);
                if (allQuality != 0) {
                    allQuality /= kettleItems.size();
                }
            }
        }
        int itemCount = 0;
        int itemSize = 0;
        for (int i = 0; i < recipe.getReqMaterial().size(); i++) {
            for (final ItemStack item : KETTLE.getPageItems(uuid, i)) {
                itemSize += AlchemyItemStatus.getSizeCount(item);
                itemCount++;
            }
        }
        itemSize = Math.round((float) itemSize / itemCount);
        itemSize += bonusSize;

        final Integer add_quality = recipeEffects.get(RecipeLevelEffect.RecipeLEType.ADD_QUALITY);
        allQuality += (add_quality != null ? add_quality : 0) + bonusQuality; // +品質固定値
        allQuality += Math.round(allQuality * (bonusQualityPercent * 0.01)); // +品質％値

        ItemStack resultItem = null;
        final String result_str = recipe.getResult();
        final Integer add_amount = recipeEffects.get(RecipeLevelEffect.RecipeLEType.ADD_AMOUNT);
        if (result_str.startsWith("material:")) {
            final AlchemyMaterial result = AlchemyMaterial.getMaterial(result_str.substring(9));

            // カテゴリ 評価 - カテゴリ追加の触媒効果などを実装後
            final List<Category> categories = new ArrayList<>(result.getCategories());
            // 錬金成分 評価
            final List<AlchemyIngredients> ingredients = recipe.getDefaultIngredients();
            for (final RecipeEffect effect : recipe.getEffects()) {
                final StarEffect activeEffect = effect.getActiveEffect(uuid);
                if (activeEffect != null) {
                    switch (activeEffect.getType()) {
                        case INGREDIENT:
                            final AlchemyIngredients ingredient = activeEffect.getIngredient();
                            if (ingredient != null && !ingredients.contains(ingredient)) {
                                ingredients.add(ingredient);
                            }
                            break;
                        case CATEGORY:
                            final Category category = activeEffect.getCategory();
                            if (category != null && !categories.contains(category)) {
                                categories.add(category);
                            }
                            break;
                        default:
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
            final int[] resultSize;
            if (itemSize <= 1) {
                resultSize = MaterialSize.S1_1.getSize(0);
            } else {
                if (itemSize >= 9) {
                    resultSize = MaterialSize.S9_1.getSize(0);
                } else {
                    resultSize = result.getSizeTemplate().getSize(itemSize - 2);
                }
            }
            resultItem = AlchemyItemStatus.getItem(
                    result,
                    ingredients, // 錬金属性 書き換え
                    Chore.createCustomModelItem(
                            result.getMaterial().getLeft(),
                            recipe.getAmount() + (add_amount != null ? add_amount : 0) + bonusAmount,
                            result.getMaterial().getRight()
                    ),
                    allQuality, // 品質 書き換え
                    resultSize, // サイズ 書き換え
                    characteristics, // 特性 書き換え
                    result.getCategories().equals(categories) ? new ArrayList<>() : categories, // カテゴリ 書き換え
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
            final StringBuilder sbBonus = new StringBuilder();
            for (final AlchemyAttribute aa : AlchemyAttribute.values()) {
                sbBonus.append(aa.getColor()).append(BONUS_MANAGER.getBonus(player, aa)).append("% ");
            }
            lore.add(2, sbBonus.toString());
            lore.add(3, BONUS_MANAGER.getBar(player.getUniqueId(), recipe.getReqBar()));

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
            final Integer inheriting = recipeEffects.get(RecipeLevelEffect.RecipeLEType.ADD_INHERITING);
            lore.add(lore.size() - 1, ChatColor.GRAY + "特性:");
            final int cslot = Math.min(3, (inheriting == null ? 0 : inheriting) + bonusInheriting);
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

    private void setKettleItems(final Inventory inv, final Player player, final AlchemyRecipe recipe) {
        // 触媒の設置
        final ItemStack citem = KETTLE.getCatalyst(player.getUniqueId());
        Catalyst catalyst;
        if (citem == null) {
            catalyst = Catalyst.getDefaultCatalyst();
        } else {
            catalyst = AlchemyMaterial.getMaterial(citem).getCatalyst();
        }
        catalyst.setInv(inv, recipe, true);

        // CSアイテムの配置
        final UUID uuid = player.getUniqueId();
        final KettleBox box = KETTLE.getKettleData(uuid);
        if (box != null) {
            int j = box.getCSize() == 36 || box.getCSize() == 25 ? 3 : 13;

            final List<CatalystBonus> bonusList = catalyst.getBonus();
            final List<CatalystBonus> ignores = new ArrayList<>();
            for (final CatalystBonus catalystBonus : bonusList) {
                if (catalystBonus.getData().getType().isOnce() && box.usedBonus(catalystBonus)) {
                    ignores.add(catalystBonus);
                }
            }

            final int size = bonusList.get(0).getCS().length;
            final int defSlot = (size == 36 || size == 25 ? 3 : 13);
            for (int i = 0; i < size; i++) {
                ItemStack slotItem = null;
                getSlotItem:
                for (final CatalystBonus b : bonusList) {
                    int slot = defSlot;
                    for (int c2 : b.getCS()) {
                        if (j == slot) {
                            short cmd = Catalyst.getCustomModelData(c2);
                            if (cmd != -1) {
                                slotItem = ignores.contains(b) ? null : Chore.ci(
                                        Material.DIAMOND_AXE,
                                        cmd,
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

            final Map<Pair<Integer, BonusItem>, Integer> resultCS = box.getResultCS();
            resultCS.keySet().forEach(slotData -> {
                final String color = AlchemyIngredients.getAllLevel(slotData.getRight().getItem()).getRight()[0].getColor();
                final ItemStack item = AlchemyCircle.getCircle(color, inv.getItem(slotData.getLeft()));
                inv.setItem(slotData.getLeft(), item);
            });
        }
    }

    private void setCharacteristicPage(final Inventory inv, final Player player, final int move) {
        final ItemStack setting = inv.getItem(1);
        final ItemMeta meta = setting.getItemMeta();
        final int page = AlchemyChore.getSetting(meta, 2);
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
                AlchemyChore.setSetting(meta, 2, new_page, STR_CHARACTERISTIC);
                setting.setItemMeta(meta);
            }

            final int startslot = 8;
            int delSlot = 1;
            for (int j = 0; j < 3; j++) {
                for (int l = 0; l < 2; l++) {
                    inv.setItem(startslot + delSlot, null);
                    delSlot++;
                }
                delSlot += startslot - 1;
            }

            int slot = 1;
            int count = 0;
            for (final Characteristic c : lists.get(check ? new_page : page)) {
                final List<String> booklore = new ArrayList<>();
                final boolean on = KETTLE.isSelectCharacteristic(uuid, c);
                booklore.add(ChatColor.GRAY + (on ? "削除" : "追加"));
                booklore.add(ChatColor.GRAY + c.getDesc());
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

    @Override
    public void onClick(@NotNull final InventoryClickEvent e) {
        if (e.isShiftClick()) {
            e.setCancelled(true);
        }
        final int raw = e.getRawSlot();
        switch (e.getAction()) {
            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT:
            case HOTBAR_SWAP:
                e.setCancelled(true);
                return;
            case SWAP_WITH_CURSOR:
                e.setCancelled(true);
                if (raw < 54) {
                    break;
                }
                return;
            default:
                break;
        }

        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        switch (e.getSlotType()) {
            case QUICKBAR:
                if (raw >= 81 && raw <= 83) {
                    e.setCancelled(true);
                    final int[] xyz = Chore.getXYZString(inv.getItem(2).getItemMeta().getDisplayName());
                    final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1.0, xyz[2]);
                    player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    final KettleBox kettleBox = KETTLE.getKettleData(uuid);
                    if (kettleBox != null) {
                        final ItemMeta setting = inv.getItem(1).getItemMeta();
                        final AlchemyRecipe recipe = AlchemyRecipe.search(AlchemyChore.getSettingStr(setting, 3));
                        int reqCount = 0;
                        for (int i = 0; i < recipe.getReqMaterial().size(); i++) {
                            reqCount += KETTLE.getPageItems(uuid, i).size();
                        }
                        if (kettleBox.getItems() != null && kettleBox.getItems().size() == reqCount) {
                            final ItemStack resultSlotItem = inv.getItem(46);
                            final int quality = AlchemyItemStatus.getQuality(resultSlotItem);

                            ItemStack resultItem = null;
                            final String result_str = recipe.getResult();
                            if (result_str.startsWith("material:")) {
                                final AlchemyMaterial result = AlchemyMaterial.getMaterial(result_str.substring(9));

                                final List<Characteristic> characteristics = KETTLE.getSelectCharacteristics(uuid);
                                final List<AlchemyIngredients> ingredients = AlchemyItemStatus.getIngredients(resultSlotItem);
                                final List<Category> categories = AlchemyItemStatus.getCategories(resultSlotItem);

                                final List<StarEffect.EnchantEffect> enchantEffects = new ArrayList<>();
                                final List<String> activeEffects = new ArrayList<>();
                                for (final RecipeEffect effect : recipe.getEffects()) {
                                    StarEffect activeEffect = effect.getActiveEffect(uuid);
                                    if (activeEffect != null) {
                                        switch (activeEffect.getType()) {
                                            case NAME:
                                                activeEffects.add(activeEffect.getName());
                                                break;
                                            case ENCHANT:
                                                activeEffects.add(activeEffect.getName());
                                                enchantEffects.add(activeEffect.getEnchantEffect());
                                                break;
                                            default: // activeEffectを変更しない
                                                break;
                                        }
                                    }
                                }

                                // アイテムの作成
                                resultItem = AlchemyItemStatus.getItem(
                                        result,
                                        ingredients, // 錬金属性 書き換え
                                        Chore.createCustomModelItem(
                                                result.getMaterial().getLeft(),
                                                resultSlotItem.getAmount(),
                                                result.getMaterial().getRight()
                                        ),
                                        quality, // 品質 書き換え
                                        AlchemyItemStatus.getSize(resultSlotItem), // サイズ 書き換え
                                        activeEffects, // 発現効果
                                        characteristics == null ? new ArrayList<>() : characteristics, // 特性 書き換え
                                        categories.isEmpty() ? null : categories, // カテゴリ 書き換え
                                        false
                                );
                                if (!enchantEffects.isEmpty()) {
                                    final ItemMeta meta = resultItem.getItemMeta();
                                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                    for (final StarEffect.EnchantEffect effect : enchantEffects) {
                                        meta.addEnchant(effect.getEnchant(), effect.getLevel(), true);
                                    }
                                    resultItem.setItemMeta(meta);
                                }
                            } else if (result_str.startsWith("minecraft:")) { // 基本想定しない
                                Material material = Material.matchMaterial(result_str);
                                if (material == null) {
                                    material = Material.matchMaterial(result_str, true);
                                }
                                resultItem = new ItemStack(
                                        material,
                                        resultSlotItem.getAmount()
                                );
                            }
                            if (resultItem != null) {
                                KETTLE.reset(player);
                                player.closeInventory();
                                final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
                                status.increaseIdea(recipe);
                                status.addRecipeExp(player, false, recipe, status.getRecipeStatus(recipe.getId()).getLevel() != 0 ? GameConstants.RECIPE_EXP : 0);
                                new AlchemyResultDrop(loc, resultItem).start();
                            }
                        }
                    }
                }
                break;
            case CONTAINER:
                switch (raw) {
                    case 54:
                    case 55:
                    case 56: {
                        //<editor-fold defaultstate="collapsed" desc="左右反転・上下反転・回転">
                        Chore.log("左右反転・上下反転・回転");
                        e.setCancelled(true);
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);

                        final ItemStack settingItem = inv.getItem(1);
                        final ItemMeta setting = settingItem.getItemMeta();
                        final int sl = AlchemyChore.getSetting(setting, 1);
                        final int rludsl = AlchemyChore.getSetting(setting, 4);
                        final PlayerInventory playerInv = player.getInventory();
                        final int nsl;
                        final int rludnsl;
                        if (e.isShiftClick()) {
                            nsl = sl;
                            int temp = e.isRightClick() ? 1 : 2;
                            temp *= (((rludsl == 1 && e.isRightClick()) || rludsl + temp > 3) ? -1 : 1);
                            rludnsl = rludsl + temp;
                        } else {
                            nsl = e.isRightClick() ? (sl + 1 >= 4) ? 0 : sl + 1 : (sl - 1 < 0) ? 3 : sl - 1;
                            rludnsl = rludsl;
                        }
                        AlchemyChore.setSetting(setting, 1, nsl, STR_TURN);
                        AlchemyChore.setSetting(setting, 4, rludnsl, STR_TURN2);
                        final List<String> rotateLore = new ArrayList<>();
                        rotateLore.add(ChatColor.GRAY + "現在： " + GameConstants.ROTATION_STR[nsl]);
                        rotateLore.add(ChatColor.GRAY + GameConstants.TURN_STR[rludnsl][0]);
                        rotateLore.add(ChatColor.GRAY + GameConstants.TURN_STR[rludnsl][1]);
                        playerInv.setItem(10, Chore.ci(Material.BARRIER, 0, ChatColor.RESET + "回転", rotateLore));
                        settingItem.setItemMeta(setting);

                        for (int i = 1; i <= 4; i++) {
                            for (int j = 3; j < 9; j++) {
                                final ItemStack item = playerInv.getItem(i * 9 + j);
                                if (item != null) {
                                    final int[] size;
                                    if (e.isShiftClick()) {
                                        size = e.isRightClick()
                                                ? MaterialSize.rightLeftTurn(AlchemyItemStatus.getSize(item))
                                                : MaterialSize.upDownTurn(AlchemyItemStatus.getSize(item));
                                    } else {
                                        size = e.isRightClick()
                                                ? MaterialSize.rightRotation(AlchemyItemStatus.getSize(item))
                                                : MaterialSize.leftRotation(AlchemyItemStatus.getSize(item));
                                    }
                                    item.setItemMeta(AlchemyItemStatus.setSize(item, size));
                                }
                            }
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
                        final Location loc = new Location(player.getWorld(), xyz[0], xyz[1] + 1.0, xyz[2]);
                        player.playSound(loc, Sound.UI_BUTTON_CLICK, 0.1f, 1);

                        final ItemStack settingItem = inv.getItem(1);
                        final ItemMeta setting = settingItem.getItemMeta();
                        final int old_center = AlchemyChore.getSetting(setting, 0);
                        int newCenter = old_center + (e.isRightClick() ? -1 : 1);
                        if (newCenter <= -1 || newCenter >= 9) {
                            newCenter = e.isRightClick() ? 8 : 0;
                        }
                        AlchemyChore.setSetting(setting, 0, newCenter, STR_CENTER);
                        settingItem.setItemMeta(setting);

                        final int[] ss = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                        ss[newCenter] = 1;
                        int l = 0;
                        StringBuilder sb = new StringBuilder();
                        final List<String> lore = new ArrayList<>();
                        for (int n : ss) {
                            l++;
                            sb.append(String.valueOf(n)
                                    .replace("0", ChatColor.GRAY + GameConstants.W_W)
                                    .replace("1", ChatColor.WHITE + GameConstants.W_B)
                            );
                            if (l % 3 == 0) {
                                lore.add(sb.toString());
                                sb = new StringBuilder();
                            }
                        }
                        final ItemMeta centerDisplay = player.getInventory().getItem(19).getItemMeta();
                        centerDisplay.setLore(lore);
                        player.getInventory().getItem(19).setItemMeta(centerDisplay);
                        break;
                        //</editor-fold>
                    }
                    case 72:
                    case 73:
                    case 74: {
                        //<editor-fold defaultstate="collapsed" desc="戻す">
                        Chore.log("戻す");
                        e.setCancelled(true);

                        final KettleBox kettleBox = KETTLE.getKettleData(uuid);
                        if (kettleBox != null) {
                            final ItemMeta setting = inv.getItem(1).getItemMeta();
                            final int sl = AlchemyChore.getSetting(setting, 1);
                            final int rludsl = AlchemyChore.getSetting(setting, 4);
                            final Pair<BonusItem, KettleBoxCircleData> backData = kettleBox.backData(sl, rludsl);
                            if (backData != null) {
                                final PlayerInventory playerInventory = player.getInventory();
                                boolean check = false;
                                final AtomicInteger slot = new AtomicInteger(12);
                                for (int i = 0; i < 3; i++) { // sonarQubeに怒られた為、ラベル未使用に変更
                                    final boolean checkInv = checkInv(slot, playerInventory);
                                    if (checkInv) {
                                        assert backData.getLeft() != null;
                                        playerInventory.setItem(slot.intValue(), backData.getLeft().getItem());
                                        check = true;
                                        break;
                                    }
                                    slot.addAndGet(4);
                                }
                                if (!check) {
                                    for (int i = 3; i < 9; i++) { // 3~8 slots
                                        if (playerInventory.getItem(i) == null) {
                                            playerInventory.setItem(i, backData.getLeft().getItem());
                                            break;
                                        }
                                    }
                                }
                                BONUS_MANAGER.back(uuid);
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
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        setCharacteristicPage(inv, player, -1);
                        Chore.log("特性一覧 ページ移動-上");
                        break;
                        //</editor-fold>
                    }
                    case 29: {
                        //<editor-fold defaultstate="collapsed" desc="特性一覧 ページ移動-下">
                        e.setCancelled(true);
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
                                if (item != null && (item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK)) {
                                    Chore.log("特性 追加・削除");
                                    final ItemMeta setting = inv.getItem(1).getItemMeta();
                                    final AlchemyRecipe recipe = AlchemyRecipe.search(AlchemyChore.getSettingStr(setting, 3));
                                    final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
                                    final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());
                                    final List<RecipeLevelEffect> effects = recipe.getLevels().get(recipeStatus.getLevel());
                                    if (effects != null) {
                                        final Characteristic c = Characteristic.search(item.getItemMeta().getDisplayName().substring(2));
                                        final boolean change_on = !KETTLE.isSelectCharacteristic(uuid, c);
                                        if (change_on) {
                                            int inheriting = 0;
                                            inheriting = effects.stream().filter(effect -> (effect.getType() == RecipeLevelEffect.RecipeLEType.ADD_INHERITING)).map(RecipeLevelEffect::getCount).reduce(inheriting, Integer::sum);
                                            final List<CatalystBonus> catalystBonusList = KETTLE.getCatalystBonusList(uuid);
                                            if (catalystBonusList != null) {
                                                inheriting = catalystBonusList.stream().filter(cbd -> (cbd.getData().getType() == CatalystBonusData.BonusType.INHERITING)).map(cbd -> cbd.getData().getX()).reduce(inheriting, Integer::sum);
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
                                final int[] size = AlchemyItemStatus.getSize(cursor);
                                if (size.length != 0) {
                                    final int center = AlchemyChore.getSetting(inv.getItem(1), 0);
                                    final int[] sets = {
                                            -10, -9, -8,
                                            -1, 0, 1,
                                            8, 9, 10
                                    };
                                    final int[] centerSets = {
                                            10, 9, 8,
                                            1, 0, -1,
                                            -8, -9, -10
                                    };

                                    final ItemStack catalystItem = KETTLE.getCatalyst(uuid);
                                    final Catalyst catalyst = catalystItem != null ? AlchemyMaterial.getMaterial(catalystItem).getCatalyst() : Catalyst.getDefaultCatalyst();
                                    final int csize = catalyst.getBonus().get(0).getCS().length;
                                    Map<Integer, Integer> rslots = new HashMap<>();
                                    for (int i = 0; i < size.length; i++) {
                                        final int value = size[i];
                                        final int slot = raw + sets[i] + centerSets[center];
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
                                                default:
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

                                    if (rslots == null) {
                                        Chore.log("アイテムを錬金釜に投入できない");
                                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                    } else {
                                        Chore.log("アイテムを錬金釜に投入できる");
                                        final ItemMeta setting = inv.getItem(1).getItemMeta();
                                        final AlchemyRecipe recipe = AlchemyRecipe.search(AlchemyChore.getSettingStr(setting, 3));
                                        final ItemStack clone = cursor.clone();
                                        clone.setAmount(1);
                                        final Pair<Integer, AlchemyAttribute[]> allLevel = AlchemyIngredients.getAllLevel(clone);
                                        BONUS_MANAGER.addBar(
                                                uuid,
                                                recipe.getReqBar(),
                                                allLevel.getLeft(),
                                                allLevel.getRight()
                                        );
                                        cursor.setAmount(cursor.getAmount() - 1);
                                        final int sl = AlchemyChore.getSetting(setting, 1);
                                        final int rludsl = AlchemyChore.getSetting(setting, 4);
                                        KETTLE.addKettleData(uuid, clone, csize, rslots, sl, rludsl);
                                        setResultSlot(inv, player);
                                    }
                                }
                                //</editor-fold>
                            }
                        } else {
                            Chore.log(raw);
                            if (raw >= 81 && raw <= 83) {
                                Chore.log("キャンセル");
                                e.setCancelled(true);
                            }
                        }
                        break;
                        //</editor-fold>
                    }
                }
                break;
            default:
                // 想定されていないスロットタイプ
                break;
        }
    }

    private boolean checkInv(AtomicInteger slot, PlayerInventory playerInventory) {
        for (int j = 0; j < 6; j++) {
            if (playerInventory.getItem(slot.intValue()) == null) {
                return true;
            }
            slot.incrementAndGet();
        }
        return false;
    }

    @Override
    public void onDrag(@NotNull final InventoryDragEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(@NotNull final InventoryCloseEvent e) {
        KETTLE.allBack((Player) e.getPlayer());
    }

    public void pickup(final EntityPickupItemEvent e) {
        final Player player = (Player) e.getEntity();
        if (KETTLE.isOpenKettle(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
