package net.firiz.renewatelier.alchemy.kettle.inventory;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.firiz.renewatelier.alchemy.kettle.*;
import net.firiz.renewatelier.alchemy.kettle.bonus.ABonus;
import net.firiz.renewatelier.alchemy.kettle.bonus.BonusItem;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBox;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBoxCircleData;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.alchemy.recipe.*;
import net.firiz.renewatelier.alchemy.recipe.result.ARecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.AlchemyMaterialRecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.MinecraftMaterialRecipeResult;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.inventory.manager.BiParamInventory;
import net.firiz.renewatelier.inventory.item.drop.AlchemyResultDrop;
import net.firiz.renewatelier.inventory.item.json.itemeffect.AlchemyItemEffect;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author firiz
 */
public class AlchemyKettleInventory implements BiParamInventory<AlchemyRecipe, Inventory> {

    private static final KettleManager KETTLE_MANAGER = KettleManager.INSTANCE;
    private static final String TITLE = "KETTLE_MAIN_MENU";
    private static final NamespacedKey centerKey = CommonUtils.createKey("center");
    private static final NamespacedKey turn1Key = CommonUtils.createKey("turn1");
    private static final NamespacedKey turn2Key = CommonUtils.createKey("turn2");
    private static final NamespacedKey characteristicKey = CommonUtils.createKey("characteristic");

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().equals(TITLE);
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final AlchemyRecipe recipe, @NotNull final Inventory catalystInv) {
        final Inventory inv = Bukkit.createInventory(player, 54, TITLE);
        final UUID uuid = player.getUniqueId();
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(uuid);
        final PlayerInventory playerInv = player.getInventory();

        // kettleBoxの生成
        kettleUserData.initializeKettleBox();

        //プレイヤーコンテンツの設定
        final ItemStack[] contents = playerInv.getContents();
        kettleUserData.setContents(contents);
        playerInv.setContents(new ItemStack[contents.length]);
        int pos = 3;
        for (int i = 0; i < Math.min(kettleUserData.getPageItems().size(), 4); i++) {
            final List<ItemStack> pageItems = kettleUserData.getPageItems(i);
            if (!pageItems.isEmpty()) {
                int itemPos = pos + 9;
                if (itemPos >= 36) {
                    itemPos = 3;
                }
                final KettleCharacteristicManager kettleCharacteristicManager = kettleUserData.getKettleCharacteristicManager();
                for (final ItemStack item : pageItems) {
                    AlchemyItemStatus.getCharacteristics(item).forEach(kettleCharacteristicManager::addCharacteristic);
                    playerInv.setItem(itemPos, item.clone());
                    itemPos++;
                }
                pos += 9;
                continue;
            }
            break;
        }

        // 回転の状態Lore
        final Lore rotateLore = new Lore();
        rotateLore.add("現在： " + GameConstants.ROTATION_STR[0]).color(C.GRAY);
        rotateLore.add(GameConstants.TURN_STR[0][0]).color(C.GRAY);
        rotateLore.add(GameConstants.TURN_STR[0][1]).color(C.GRAY);
        playerInv.setItem(10, ItemUtils.ci(Material.BARRIER, 0, Text.itemName("回転", C.WHITE), rotateLore));

        // 設定アイテムの作成
        inv.setItem(1, createSettingItem());

        final int[] ss = new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0};
        int l = 0;
        final Lore lore = new Lore();
        for (final int n : ss) {
            l++;
            lore.add(n == 0 ? Text.of(GameConstants.W_W, C.GRAY) : Text.of(GameConstants.W_B, C.WHITE));
            if (l % 3 == 0) {
                lore.nextLine();
            }
        }
        final ItemStack centerDisplay = ItemUtils.ci(Material.BARRIER, 0, Text.itemName("中心点", C.WHITE), lore);
        player.getInventory().setItem(19, centerDisplay);
        setResultSlot(inv, player, true);
        player.openInventory(inv);
    }

    private ItemStack createSettingItem() {
        final ItemStack item = ItemUtils.ci(Material.BARRIER, 0, Component.empty(), null);
        final ItemMeta meta = item.getItemMeta();
        CommonUtils.setSettingInt(meta, centerKey, 4);
        CommonUtils.setSettingInt(meta, turn1Key, 0);
        CommonUtils.setSettingInt(meta, turn2Key, 0);
        CommonUtils.setSettingInt(meta, KettleConstants.scrollKey, 0);
        item.setItemMeta(meta);
        return item;
    }

    private void setCenter(Inventory inventory, int value) {
        setValue(centerKey, inventory, value);
    }

    private int getCenter(Inventory inventory) {
        return CommonUtils.getSettingInt(Objects.requireNonNull(inventory.getItem(1)).getItemMeta(), centerKey);
    }

    private void setTurn1(Inventory inventory, int value) {
        setValue(turn1Key, inventory, value);
    }

    private int getTurn1(Inventory inventory) {
        return CommonUtils.getSettingInt(Objects.requireNonNull(inventory.getItem(1)).getItemMeta(), turn1Key);
    }

    private void setTurn2(Inventory inventory, int value) {
        setValue(turn2Key, inventory, value);
    }

    private int getTurn2(Inventory inventory) {
        return CommonUtils.getSettingInt(Objects.requireNonNull(inventory.getItem(1)).getItemMeta(), turn2Key);
    }

    private void setCharacteristicPage(Inventory inventory, int value) {
        setValue(KettleConstants.scrollKey, inventory, value);
    }

    private int getCharacteristicPage(Inventory inventory) {
        return CommonUtils.getSettingInt(Objects.requireNonNull(inventory.getItem(1)).getItemMeta(), KettleConstants.scrollKey);
    }

    private void setValue(NamespacedKey key, Inventory inventory, int value) {
        final ItemStack item = Objects.requireNonNull(inventory.getItem(1));
        final ItemMeta meta = item.getItemMeta();
        CommonUtils.setSettingInt(meta, key, value);
        item.setItemMeta(meta);
    }

    private void setResultSlot(final Inventory inv, final Player player, final boolean resetCharacteristic) {
        final UUID uuid = player.getUniqueId();
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(uuid);
        final ABonus bonusManager = kettleUserData.getBonusManager();
        final KettleCharacteristicManager kettleCharacteristicManager = kettleUserData.getKettleCharacteristicManager();
        final AlchemyRecipe recipe = kettleUserData.getRecipe();
        final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
        final RecipeStatus recipeStatus = Objects.requireNonNull(status.getRecipeStatus(recipe));

        setKettleItems(inv, player, recipe);

        // レシピレベル追加効果 評価
        final Object2IntMap<RecipeLevelEffect.RecipeLEType> recipeEffects = new Object2IntLinkedOpenHashMap<>();
        final Int2ObjectMap<List<RecipeLevelEffect>> levels = recipe.getLevels();
        if (!levels.isEmpty()) {
            final List<RecipeLevelEffect> effects = levels.get(recipeStatus.getLevel());
            if (effects != null && !effects.isEmpty()) {
                effects.forEach(e -> recipeEffects.put(e.getType(), e.getCount()));
            }
        }

        // 触媒追加効果 評価
        // 確認・設定
        final ItemStack catalystItem = kettleUserData.getCatalystItem();
        final Catalyst catalyst = catalystItem != null ? AlchemyItemStatus.getMaterialNonNull(catalystItem).catalyst() : Catalyst.getDefaultCatalyst();
        final List<CatalystBonus> catalystBonuses = catalyst.getBonus();
        final int size = catalystBonuses.get(0).getCS().length;
        final int defSlot = (size == 36 || size == 25 ? 3 : 13);
        resultBonus:
        for (final CatalystBonus bonus : catalystBonuses) {
            final int[] cs = bonus.getCS();
            final CatalystBonusData data = bonus.getData();
            int slot = defSlot;
            final IntList activeSlots = new IntArrayList();
            for (final int c : cs) {
                if (c != 0) {
                    final ItemStack item = inv.getItem(slot);
                    if (item != null) {
                        final int customModel = ItemUtils.getCustomModelData(item);
                        final int defCustomModelData = Catalyst.getCustomModelData(c);
                        boolean stop = false;
                        if (customModel == defCustomModelData) {
                            stop = true;
                        } else {
                            final AlchemyCircle circle = AlchemyCircle.searchData(defCustomModelData);
                            if (circle != AlchemyCircle.WHITE && circle.getCircleType() != AlchemyCircle.searchData(customModel).getCircleType()) {
                                stop = true;
                            }
                        }
                        if (stop) {
                            final boolean remove = bonusManager.removeCatalystBonus(bonus);
                            if (remove) {
                                switch (data.getType()) {
                                    case CHARACTERISTIC: {
                                        final Characteristic cdata = (Characteristic) data.getY();
                                        kettleCharacteristicManager.removeCatalystCharacteristic(cdata);
                                        break;
                                    }
                                    case INHERITING: {
                                        kettleCharacteristicManager.clearActiveCharacteristics();
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
            if (!bonusManager.hasCatalystBonus(bonus)) {
                bonusManager.addCatalystBonus(bonus);
            }
            // アクティブ触媒効果にGlowを付与
            activeSlots.forEach(CollectionUtils.intConsumer(
                    activeSlot -> Objects.requireNonNull(inv.getItem(activeSlot)).addUnsafeEnchantment(Enchantment.LUCK, 1)
            ));
        }
        final List<CatalystBonus> catalystBonusesL = bonusManager.getCatalystBonuses();
        int bonusQuality = 0;
        int bonusQualityPercent = 0;
        int bonusAmount = 0;
        int bonusInheriting = 0;
        int bonusSize = 0;
        if (catalystBonusesL != null) {
            for (final CatalystBonus bonus : catalystBonusesL) {
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
                        kettleCharacteristicManager.addCatalystCharacteristic((Characteristic) bonusData.getY());
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
        final KettleBox kettleBox = kettleUserData.getKettleBox();
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
            for (final ItemStack item : kettleUserData.getPageItems(i)) {
                itemSize += AlchemyItemStatus.getSizeCount(item);
                itemCount++;
            }
        }
        itemSize = Math.round((float) itemSize / itemCount);
        itemSize += bonusSize;

        final int add_quality = recipeEffects.getInt(RecipeLevelEffect.RecipeLEType.ADD_QUALITY);
        allQuality += add_quality + bonusQuality; // +品質固定値
        allQuality += Math.round(allQuality * (bonusQualityPercent * 0.01)); // +品質％値

        ItemStack resultItem = null;
        final ARecipeResult<?> resultData = recipe.getResult();
        final int add_amount = recipeEffects.getInt(RecipeLevelEffect.RecipeLEType.ADD_AMOUNT);
        if (resultData instanceof AlchemyMaterialRecipeResult) {
            final AlchemyMaterial result = ((AlchemyMaterialRecipeResult) resultData).getResult();

            // カテゴリ 評価 - カテゴリ追加の触媒効果などを実装後
            final List<Category> categories = new ObjectArrayList<>(result.categories());
            // 錬金成分 評価
            final List<AlchemyIngredients> ingredients = recipe.getDefaultIngredients();
            for (final RecipeEffect effect : recipe.getEffects()) {
                final StarEffect activeEffect = effect.getActiveEffect(kettleUserData);
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
            final List<Characteristic> characteristics = new ObjectArrayList<>();

            // 触媒設定 - 触媒の設定がなされているアイテムだった場合 - いらんかも
            // アイテムの作成
            final int[] resultSize;
            if (itemSize <= 1) {
                resultSize = MaterialSize.S1_1.getSize(0);
            } else {
                if (itemSize >= 9) {
                    resultSize = MaterialSize.S9_1.getSize(0);
                } else {
                    resultSize = result.sizeTemplate().getSize(itemSize - 2);
                }
            }
            resultItem = AlchemyItemStatus.getItem(
                    result,
                    ingredients, // 錬金属性 書き換え
                    result.material().toItemStack(recipe.getAmount() + add_amount + bonusAmount),
                    allQuality, // 品質 書き換え
                    resultSize, // サイズ 書き換え
                    characteristics, // 特性 書き換え
                    result.categories().equals(categories) ? new ObjectArrayList<>() : categories, // カテゴリ 書き換え
                    true
            );
        } else if (resultData instanceof MinecraftMaterialRecipeResult) { // 基本想定しない
            final Material material = ((MinecraftMaterialRecipeResult) resultData).getResult();
            resultItem = new ItemStack(
                    material,
                    recipe.getAmount() + add_amount
            );
        }

        if (resultItem != null) {
            final ItemMeta meta = resultItem.getItemMeta();
            final Lore lore = new Lore(meta.lore());

            // ボーナス項目
            final Text textBonus = new Text();
            for (final AlchemyAttribute aa : AlchemyAttribute.values()) {
                textBonus.append(bonusManager.getBonus(aa) + "% ").color(aa.getColor());
            }
            lore.add(2, textBonus);
            lore.add(3, bonusManager.getBar());

            // 効果項目
            lore.add(4, Text.of("効果:", C.GRAY));
            int loreSlot = 5;
            if (recipe.getEffects().isEmpty()) {
                lore.add(loreSlot, Text.of("なし", C.WHITE));
                loreSlot++;
            } else {
                for (final RecipeEffect effect : recipe.getEffects()) {
                    final Component name = effect.getName(kettleUserData);
                    lore.add(loreSlot, new Text("・").color(C.WHITE).append(name == null ? Component.text("なし").color(C.WHITE) : name));
                    lore.add(loreSlot + 1, new Text("  ").append(effect.getStar(kettleUserData)));
                    loreSlot += 2;
                }
            }

            // 触媒効果項目
            lore.add(loreSlot, "触媒効果:").color(C.GRAY);
            if (catalystBonusesL != null && !catalystBonusesL.isEmpty()) {
                for (final CatalystBonus bonus : catalystBonusesL) {
                    loreSlot++;
                    lore.add(loreSlot, "- ".concat(bonus.getData().getName())).color(C.GREEN);
                }
            } else {
                loreSlot++;
                lore.add(loreSlot, "なし").color(C.WHITE);
            }

            // 特性項目
            final int inheriting = recipeEffects.getInt(RecipeLevelEffect.RecipeLEType.ADD_INHERITING);
            lore.add(lore.size() - 1, "特性:").color(C.GRAY);
            final int cSlot = Math.min(3, inheriting + bonusInheriting);
            if (cSlot == 0) {
                lore.add(lore.size() - 1, "特性引継ぎスロットなし").color(C.WHITE);
            } else {
                final List<Characteristic> cs = kettleCharacteristicManager.getActiveCharacteristics();
                int count = 0;
                if (cs != null) {
                    for (final Characteristic c : cs) {
                        lore.add(lore.size() - 1, "- " + c.getName()).color(C.WHITE);
                        count++;
                    }
                }
                for (int i = count; i < cSlot; i++) {
                    lore.add(lore.size() - 1, "- なし").color(C.WHITE);
                }
            }

            meta.lore(lore);
            resultItem.setItemMeta(meta);
            inv.setItem(46, resultItem);
            if (resetCharacteristic) {
                kettleCharacteristicManager.clearActiveCharacteristics();
            }
            setCharacteristicPage(inv, player, 0);
        }
    }

    private void setKettleItems(final Inventory inv, final Player player, final AlchemyRecipe recipe) {
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(player.getUniqueId());
        // 触媒の設置
        final ItemStack catalystItem = kettleUserData.getCatalystItem();
        Catalyst catalyst;
        if (catalystItem == null) {
            catalyst = Catalyst.getDefaultCatalyst();
        } else {
            catalyst = AlchemyItemStatus.getMaterialNonNull(catalystItem).catalyst();
        }
        catalyst.setInv(inv, true);

        // CSアイテムの配置
        final KettleBox box = kettleUserData.getKettleBox();
        if (box != null) {
            int j = box.getCSize() == 36 || box.getCSize() == 25 ? 3 : 13;

            final List<CatalystBonus> bonusList = catalyst.getBonus();
            final List<CatalystBonus> ignores = new ObjectArrayList<>();
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
                                slotItem = ignores.contains(b) ? null : ItemUtils.ci(
                                        Material.DIAMOND_AXE,
                                        cmd,
                                        b.getData().getName(),
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

            final Object2IntMap<Pair<Integer, BonusItem>> resultCS = box.getResultCS();
            resultCS.keySet().forEach(slotData -> {
                final AlchemyAttribute attribute = AlchemyIngredients.getMaxTypes(slotData.right().getItem()).right()[0];
                final ItemStack item = AlchemyCircle.getCircle(attribute.getValue(), inv.getItem(slotData.left()));
                inv.setItem(slotData.left(), item);
            });
        }
    }

    private void setCharacteristicPage(final Inventory inv, final Player player, final int move) {
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(player.getUniqueId());
        final KettleCharacteristicManager kettleCharacteristicManager = kettleUserData.getKettleCharacteristicManager();
        final List<Characteristic> characteristics = kettleCharacteristicManager.getCharacteristics();
        final ARecipeResult<?> resultData = kettleUserData.getRecipe().getResult();
        if (characteristics == null || !(resultData instanceof AlchemyMaterialRecipeResult)) {
            return;
        }
        final AlchemyMaterialCategory materialCategory = ((AlchemyMaterial) resultData.getResult()).materialCategory();
        final Set<Characteristic> combinedCharacteristics = Characteristic.combine(materialCategory, characteristics);
        final int page = getCharacteristicPage(inv);
        final int nextPage = Math.max(1, page + move);
        final List<List<Characteristic>> lists = new ObjectArrayList<>();
        final List<Characteristic> list = new ObjectArrayList<>();
        int i = 0;
        for (final Characteristic c : combinedCharacteristics) {
            if (i % 6 == 0) {
                lists.add(new ObjectArrayList<>(list));
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
            if (lists.size() > nextPage) {
                check = true;
                setCharacteristicPage(inv, nextPage);
            }

            final int startSlot = 8;
            int delSlot = 1;
            for (int j = 0; j < 3; j++) {
                for (int l = 0; l < 2; l++) {
                    inv.setItem(startSlot + delSlot, null);
                    delSlot++;
                }
                delSlot += startSlot - 1;
            }

            int slot = 1;
            int count = 0;
            for (final Characteristic c : lists.get(check ? nextPage : page)) {
                final Lore bookLore = new Lore();
                final boolean on = kettleCharacteristicManager.hasActiveCharacteristic(c);
                bookLore.add((on ? "削除" : "追加")).color(C.GRAY);
                bookLore.add(c.getDesc()).color(C.GRAY);

                final ItemStack item = ItemUtils.ci(on ? Material.ENCHANTED_BOOK : Material.BOOK, 0, Text.itemName(c.getName(), C.WHITE), bookLore);
                ItemUtils.setSetting(item, characteristicKey, c.getId());
                inv.setItem(startSlot + slot, item);

                if (count > 0) {
                    count = 0;
                    slot += startSlot;
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
            case DROP_ALL_CURSOR, DROP_ONE_CURSOR, DROP_ALL_SLOT, DROP_ONE_SLOT, HOTBAR_SWAP:
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
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(uuid);
        final ABonus bonusManager = kettleUserData.getBonusManager();
        final KettleCharacteristicManager kettleCharacteristicManager = kettleUserData.getKettleCharacteristicManager();
        switch (e.getSlotType()) {
            case QUICKBAR:
                if (raw >= 81 && raw <= 83) {
                    e.setCancelled(true);
                    final Location loc = kettleUserData.getLocation().clone().add(0, 1, 0);
                    player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    final KettleBox kettleBox = kettleUserData.getKettleBox();
                    if (kettleBox != null) {
                        final AlchemyRecipe recipe = kettleUserData.getRecipe();
                        int reqCount = 0;
                        for (int i = 0; i < recipe.getReqMaterial().size(); i++) {
                            reqCount += kettleUserData.getPageItems(i).size();
                        }
                        if (kettleBox.getNonNullItemCount() == reqCount) {
                            final ItemStack resultSlotItem = inv.getItem(46);
                            final AlchemyItemStatus itemStatus = Objects.requireNonNull(AlchemyItemStatus.load(resultSlotItem));
                            final int quality = itemStatus.getQuality();

                            ItemStack resultItem = null;
                            final ARecipeResult<?> resultData = recipe.getResult();
                            if (resultData instanceof AlchemyMaterialRecipeResult) {
                                final AlchemyMaterial result = ((AlchemyMaterialRecipeResult) resultData).getResult();

                                final List<Characteristic> characteristics = kettleCharacteristicManager.getActiveCharacteristics();
                                final List<AlchemyIngredients> ingredients = itemStatus.getIngredients();
                                final List<Category> categories = itemStatus.getCategories();

//                                final List<StarEffect.EnchantEffect> enchantEffects = new ObjectArrayList<>();
                                final List<AlchemyItemEffect> activeEffects = new ObjectArrayList<>();
                                for (final RecipeEffect effect : recipe.getEffects()) {
                                    StarEffect activeEffect = effect.getActiveEffect(kettleUserData);
                                    if (activeEffect != null) {
                                        switch (activeEffect.getType()) {
                                            case ITEM_EFFECT:
                                                activeEffects.add(activeEffect.getEffect());
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
                                        result.material().toItemStack(resultSlotItem.getAmount()),
                                        quality, // 品質 書き換え
                                        itemStatus.getSize(), // サイズ 書き換え
                                        activeEffects, // 発現効果
                                        characteristics == null ? new ObjectArrayList<>() : characteristics, // 特性 書き換え
                                        categories.isEmpty() ? null : categories, // カテゴリ 書き換え
                                        false
                                );
                                for (final AlchemyItemEffect alchemyItemEffect : activeEffects) {
                                    alchemyItemEffect.initialize(resultItem);
                                }
                            } else if (resultData instanceof MinecraftMaterialRecipeResult) { // 基本想定しない
                                final Material material = ((MinecraftMaterialRecipeResult) resultData).getResult();
                                resultItem = new ItemStack(
                                        Objects.requireNonNull(material),
                                        resultSlotItem.getAmount()
                                );
                            }
                            if (resultItem != null) {
                                KETTLE_MANAGER.remove(player, true);
                                player.closeInventory(); // コンテンツを更新してすぐにインベントリを閉じると一部のアイテムが残るバグがある？？
                                final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
                                status.increaseIdea(recipe);
                                status.addRecipeExp(false, recipe, status.getRecipeStatus(recipe).getLevel() != 0 ? GameConstants.RECIPE_EXP : 0);
                                status.getCharStats().addAlchemyExp(Math.max(1, recipe.getReqAlchemyLevel() / 2));
                                new AlchemyResultDrop(loc, resultItem).start();
                            }
                        }
                    }
                }
                break;
            case CONTAINER:
                switch (raw) {
                    case 54, 55, 56 -> {
                        //<editor-fold defaultstate="collapsed" desc="左右反転・上下反転・回転">
                        CommonUtils.log("左右反転・上下反転・回転");
                        e.setCancelled(true);
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);

                        final PlayerInventory playerInv = player.getInventory();
                        final int turn1 = getTurn1(inv);
                        final int turn2 = getTurn2(inv);
                        final int turn3;
                        final int turn4;
                        if (e.isShiftClick()) {
                            turn3 = turn1;
                            int temp = e.isRightClick() ? 1 : 2;
                            temp *= (((turn2 == 1 && e.isRightClick()) || turn2 + temp > 3) ? -1 : 1);
                            turn4 = turn2 + temp;
                        } else {
                            turn3 = e.isRightClick() ? (turn1 + 1 >= 4) ? 0 : turn1 + 1 : (turn1 - 1 < 0) ? 3 : turn1 - 1;
                            turn4 = turn2;
                        }
                        setTurn1(inv, turn3);
                        setTurn2(inv, turn4);
                        final Lore rotateLore = new Lore();
                        rotateLore.add("現在： " + GameConstants.ROTATION_STR[turn3]).color(C.GRAY);
                        rotateLore.add(GameConstants.TURN_STR[turn4][0]).color(C.GRAY);
                        rotateLore.add(GameConstants.TURN_STR[turn4][1]).color(C.GRAY);
                        playerInv.setItem(10, ItemUtils.ci(Material.BARRIER, 0, Text.itemName("回転", C.WHITE), rotateLore));

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
                                    AlchemyItemStatus.setSize(item, size);
                                }
                            }
                        }
                        //</editor-fold>
                    }
                    case 63, 64, 65 -> {
                        //<editor-fold defaultstate="collapsed" desc="中心点移動">
                        CommonUtils.log("中心点移動");
                        e.setCancelled(true);
                        final Location loc = kettleUserData.getLocation().clone().add(0, 1, 0);
                        player.playSound(loc, Sound.UI_BUTTON_CLICK, 0.1f, 1);

                        final int oldCenter = getCenter(inv);
                        int nextCenter = oldCenter + (e.isRightClick() ? -1 : 1);
                        if (nextCenter <= -1 || nextCenter >= 9) {
                            nextCenter = e.isRightClick() ? 8 : 0;
                        }
                        setCenter(inv, nextCenter);

                        final int[] ss = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                        ss[nextCenter] = 1;
                        int l = 0;
                        final Lore lore = new Lore();
                        for (int n : ss) {
                            l++;
                            lore.add(n == 0 ? Component.text(GameConstants.W_W).color(C.GRAY) : Component.text(GameConstants.W_B).color(C.WHITE));
                            if (l % 3 == 0) {
                                lore.nextLine();
                            }
                        }
                        final ItemMeta centerDisplay = player.getInventory().getItem(19).getItemMeta();
                        centerDisplay.lore(lore);
                        player.getInventory().getItem(19).setItemMeta(centerDisplay);
                        //</editor-fold>
                    }
                    case 72, 73, 74 -> {
                        //<editor-fold defaultstate="collapsed" desc="戻す">
                        CommonUtils.log("戻す");
                        e.setCancelled(true);

                        final KettleBox kettleBox = kettleUserData.getKettleBox();
                        if (kettleBox != null) {
                            final int turn1 = getTurn1(inv);
                            final int turn2 = getTurn2(inv);
                            final Pair<BonusItem, KettleBoxCircleData> backData = kettleBox.backData(turn1, turn2);
                            if (backData != null) {
                                final PlayerInventory playerInventory = player.getInventory();
                                boolean check = false;
                                final AtomicInteger slot = new AtomicInteger(12);
                                for (int i = 0; i < 3; i++) {
                                    final boolean checkInv = checkInv(slot, playerInventory);
                                    if (checkInv) {
                                        assert backData.left() != null;
                                        playerInventory.setItem(slot.intValue(), backData.left().getItem());
                                        check = true;
                                        break;
                                    }
                                    slot.addAndGet(4);
                                }
                                if (!check) {
                                    for (int i = 3; i < 9; i++) { // 3~8 slots
                                        if (playerInventory.getItem(i) == null) {
                                            playerInventory.setItem(i, backData.left().getItem());
                                            break;
                                        }
                                    }
                                }
                                bonusManager.back();
                                setResultSlot(inv, player, true);
                                player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                                break;
                            }
                        }
                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                        //</editor-fold>
                    }
                    case 11 -> {
                        //<editor-fold defaultstate="collapsed" desc="特性一覧 ページ移動-上">
                        e.setCancelled(true);
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        setCharacteristicPage(inv, player, -1);
                        CommonUtils.log("特性一覧 ページ移動-上");
                        break;
                        //</editor-fold>
                    }
                    case 29 -> {
                        //<editor-fold defaultstate="collapsed" desc="特性一覧 ページ移動-下">
                        e.setCancelled(true);
                        player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        setCharacteristicPage(inv, player, 1);
                        CommonUtils.log("特性一覧 ページ移動-下");
                        //</editor-fold>
                    }
                    default -> {
                        //<editor-fold defaultstate="collapsed" desc="default">
                        if (raw < 54) {
                            if ((raw >= 9 && raw <= 10)
                                    || (raw >= 18 && raw <= 19)
                                    || (raw >= 27 && raw <= 28)) {
                                //<editor-fold defaultstate="collapsed" desc="特性 追加・削除">
                                e.setCancelled(true);
                                final ItemStack item = inv.getItem(raw);
                                if (item != null && (item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK)) {
                                    CommonUtils.log("特性 追加・削除");
                                    final AlchemyRecipe recipe = kettleUserData.getRecipe();
                                    final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
                                    final RecipeStatus recipeStatus = Objects.requireNonNull(status.getRecipeStatus(recipe));
                                    final List<RecipeLevelEffect> effects = recipe.getLevels().get(recipeStatus.getLevel());
                                    if (effects != null) {
                                        final Characteristic c = Characteristic.getCharacteristic(ItemUtils.getSetting(item, characteristicKey));
                                        final boolean nonActive = !kettleCharacteristicManager.hasActiveCharacteristic(c);
                                        if (nonActive) {
                                            int inheriting = 0;
                                            inheriting = effects.stream().filter(effect -> (effect.getType() == RecipeLevelEffect.RecipeLEType.ADD_INHERITING)).map(RecipeLevelEffect::getCount).reduce(inheriting, Integer::sum);
                                            final List<CatalystBonus> catalystBonusList = bonusManager.getCatalystBonuses();
                                            if (catalystBonusList != null) {
                                                inheriting = catalystBonusList.stream().filter(cbd -> (cbd.getData().getType() == CatalystBonusData.BonusType.INHERITING)).map(cbd -> cbd.getData().getX()).reduce(inheriting, Integer::sum);
                                            }
                                            final List<Characteristic> scs = kettleCharacteristicManager.getActiveCharacteristics();
                                            final int count = scs != null ? scs.size() : 0;
                                            if (count < Math.min(3, inheriting)) {
                                                CommonUtils.log("特性 追加");
                                                player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f);
                                                kettleCharacteristicManager.addActiveCharacteristic(c);
                                                CommonUtils.log(kettleCharacteristicManager.getCharacteristics());
                                            } else {
                                                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                                return;
                                            }
                                        } else {
                                            CommonUtils.log("特性 削除");
                                            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 1f);
                                            kettleCharacteristicManager.removeActiveCharacteristic(c);
                                        }
                                        setResultSlot(inv, player, false);
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
                                    final int center = getCenter(inv);
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

                                    final ItemStack catalystItem = kettleUserData.getCatalystItem();
                                    final Catalyst catalyst = catalystItem != null ? AlchemyItemStatus.getMaterialNonNull(catalystItem).catalyst() : Catalyst.getDefaultCatalyst();
                                    final int cSize = catalyst.getBonus().get(0).getCS().length;
                                    Int2IntMap rslots = new Int2IntOpenHashMap();
                                    for (int i = 0; i < size.length; i++) {
                                        final int value = size[i];
                                        final int slot = raw + sets[i] + centerSets[center];
                                        if (value != 0) {
                                            boolean check = false;
                                            switch (cSize) {
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
                                        CommonUtils.log("アイテムを錬金釜に投入できない");
                                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                    } else {
                                        CommonUtils.log("アイテムを錬金釜に投入できる");
                                        final ItemMeta setting = inv.getItem(1).getItemMeta();
                                        final ItemStack clone = cursor.clone();
                                        clone.setAmount(1);
                                        final IntObjectMutablePair<AlchemyAttribute[]> allLevel = AlchemyIngredients.getMaxTypes(clone);
                                        bonusManager.addBar(
                                                allLevel.leftInt(),
                                                allLevel.right()
                                        );
                                        cursor.setAmount(cursor.getAmount() - 1);
                                        final int turn1 = getTurn1(inv);
                                        final int turn2 = getTurn2(inv);
                                        kettleUserData.getKettleBox().addItem(clone, rslots, turn1, turn2);
                                        setResultSlot(inv, player, true);
                                    }
                                }
                                //</editor-fold>
                            }
                        } else {
                            CommonUtils.log(raw);
                            if (raw >= 81 && raw <= 83) {
                                CommonUtils.log("キャンセル");
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
        KETTLE_MANAGER.remove((Player) e.getPlayer(), false);
    }

    public void pickup(final EntityPickupItemEvent e) {
        final Player player = (Player) e.getEntity();
        if (check(player.getOpenInventory())) {
            e.setCancelled(true);
        }
    }
}
