package net.firiz.renewatelier.inventory.item.json;

import com.google.gson.annotations.Expose;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.*;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.inventory.item.CustomModelMaterial;
import net.firiz.renewatelier.inventory.item.json.itemeffect.AlchemyItemEffect;
import net.firiz.renewatelier.server.json.JsonFactory;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.utils.java.CObjects;
import net.firiz.renewatelier.utils.Randomizer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author firiz
 */
public class AlchemyItemStatus {

    private static final NamespacedKey persistentDataKey = CommonUtils.createKey("alchemyItemStatus");

    @Expose
    @NotNull
    private final AlchemyMaterial alchemyMaterial;
    @Nullable
    private final CustomModelMaterial customModel;
    @Expose
    private int[] size;
    @Expose
    @NotNull
    private final List<Category> categories;
    @Expose
    private int quality;
    @Expose
    @NotNull
    private final List<AlchemyIngredients> ingredients;
    @Expose
    @NotNull
    private final List<Characteristic> characteristics;
    @Expose
    @NotNull
    private final List<AlchemyItemEffect> activeEffects;
    @Expose
    private int hp;
    @Expose
    private int mp;
    @Expose
    private int atk;
    @Expose
    private int def;
    @Expose
    private int speed;
    @Expose
    private int usableCount;
    @Expose
    private int consumedCount;
    @Expose
    @NotNull
    private final List<String> prefix;
    @Expose
    @NotNull
    private final Map<String, String> dataContainer;

    @Nullable
    private ItemStack itemStack;

    private AlchemyItemStatus(AlchemyMaterial alchemyMaterial, int[] size, List<Category> categories, int quality, List<AlchemyIngredients> ingredients, List<Characteristic> characteristics, List<AlchemyItemEffect> activeEffects, int hp, int mp, int atk, int def, int speed, int usableCount) {
        this(alchemyMaterial, null, size, categories, quality, ingredients, characteristics, activeEffects, hp, mp, atk, def, speed, usableCount, 0, new ObjectArrayList<>(), new Object2ObjectOpenHashMap<>());
    }

    private AlchemyItemStatus(@NotNull AlchemyMaterial alchemyMaterial, @Nullable CustomModelMaterial customModel, int[] size, @NotNull List<Category> categories, int quality, @NotNull List<AlchemyIngredients> ingredients, @NotNull List<Characteristic> characteristics, @NotNull List<AlchemyItemEffect> activeEffects, int hp, int mp, int atk, int def, int speed, int usableCount, int consumedCount, @NotNull List<String> prefix, @NotNull Map<String, String> dataContainer) {
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
        this.usableCount = usableCount;
        this.consumedCount = consumedCount;
        this.prefix = prefix;
        this.dataContainer = dataContainer;
        this.itemStack = null;
    }

    @NotNull
    public AlchemyMaterial getAlchemyMaterial() {
        return alchemyMaterial;
    }

    public int[] getSize() {
        return size;
    }

    public int getSizeCount() {
        return (int) Arrays.stream(size).filter(i -> i != 0).count();
    }

    @NotNull
    public List<Category> getCategories() {
        return categories;
    }

    public int getQuality() {
        return quality;
    }

    @NotNull
    public List<AlchemyIngredients> getIngredients() {
        return ingredients;
    }

    @NotNull
    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    @NotNull
    public List<AlchemyItemEffect> getActiveEffects() {
        return activeEffects;
    }

    public boolean hasActiveEffect(AlchemyItemEffect effect) {
        return activeEffects.contains(effect);
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

    public int getUsableCount() {
        return usableCount;
    }

    public void setUsableCount(int usableCount) {
        this.usableCount = usableCount;
    }

    public int getConsumedCount() {
        return consumedCount;
    }

    public void setConsumedCount(int consumedCount) {
        this.consumedCount = consumedCount;
    }

    public void incrementConsumedCount() {
        if (characteristics.stream().anyMatch(c -> c.hasData(CharacteristicType.USE_ONE))) {
            consumedCount = usableCount;
        } else {
            consumedCount++;
        }
    }

    public void decrementConsumedCount() {
        consumedCount--;
    }

    public boolean canUse() {
        return usableCount > consumedCount;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * 一部のアイテムで使用するscript用メソッド
     *
     * @return prefix
     */
    @NotNull
    public List<String> getPrefix() {
        return prefix;
    }

    /**
     * 一部のアイテムで使用するscript用メソッド
     *
     * @param prefix prefixに追加する文字列
     * @return add
     */
    public boolean addPrefix(String prefix) {
        return this.prefix.add(prefix);
    }

    /**
     * 一部のアイテムで使用するscript用メソッド
     *
     * @param key アイテムのキー
     * @return get
     */
    public String getDataContainerValue(String key) {
        return dataContainer.get(key);
    }

    /**
     * 一部のアイテムで使用するscript用メソッド
     *
     * @param key   登録したいアイテムのキー
     * @param value 登録したい文字列データ
     */
    public void addDataContainer(@NotNull String key, @NotNull String value) {
        dataContainer.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    /**
     * 一部のアイテムで使用するscript用メソッド
     *
     * @param key 　アイテムのキー
     * @return containsKey
     */
    public boolean hasDataContainer(@NotNull String key) {
        return dataContainer.containsKey(key);
    }

    @NotNull
    public ItemStack create() {
        final ItemStack item;
        if (customModel == null) {
            item = null;
        } else {
            item = customModel.toItemStack();
        }
        return Objects.requireNonNull(AlchemyItemStatus.getItem(
                alchemyMaterial,
                ingredients,
                item,
                quality,
                size,
                activeEffects,
                characteristics,
                categories,
                new VisibleFlags(true),
                hp,
                mp,
                atk,
                def,
                speed,
                usableCount,
                consumedCount,
                prefix,
                dataContainer
        ));
    }

    public static boolean has(@Nullable final ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            return container.has(persistentDataKey, PersistentDataType.STRING);
        }
        return false;
    }

    @Nullable
    public static AlchemyItemStatus load(@Nullable final ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            if (container.has(persistentDataKey, PersistentDataType.STRING)) {
                final AlchemyItemStatus result = loadJson(container.get(persistentDataKey, PersistentDataType.STRING));
                result.itemStack = item;
                return result;
            }
        }
        return null;
    }

    @NotNull
    public static AlchemyItemStatus loadJson(@Nullable final String json) {
        final AlchemyItemStatus status = JsonFactory.fromJson(json, AlchemyItemStatus.class);
        status.update();
        return status;
    }

    private void update() {
        // AlchemyItemStatusにフィールド変数を追加した場合json読み込み時にnullになる為、バージョンアップ処理を施す必要性がある？
    }

    public String toJson() {
        return JsonFactory.toJson(this);
    }

    public void writeJson(@NotNull final ItemStack item) {
        Objects.requireNonNull(item);
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(persistentDataKey, PersistentDataType.STRING, toJson());
        item.setItemMeta(meta);
    }

    public static int[] getSize(final ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            final AlchemyItemStatus itemStatus = load(item);
            if (itemStatus != null) {
                return itemStatus.getSize();
            }
        }
        return new int[0];
    }

    public static int getSizeCount(final ItemStack item) {
        return (int) Arrays.stream(getSize(item)).filter(i -> i != 0).count();
    }

    public static void setSize(final ItemStack item, final int[] size) {
        final AlchemyItemStatus itemStatus = load(item);
        if (itemStatus != null) {
            itemStatus.setSize(size);
            itemStatus.updateItem(item);
        }
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

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<AlchemyItemEffect> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final boolean notVisibleCatalyst) {
        return getItem(am, overIngs, item, overQuality, overSize, activeEffects, overCharacteristics, overCategory, new VisibleFlags(true, true, true, true, !notVisibleCatalyst, true, true, true));
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<AlchemyItemEffect> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final VisibleFlags visibleFlags) {
        return getItem(am, overIngs, item, overQuality, overSize, activeEffects, overCharacteristics, overCategory, visibleFlags, am.hp(), am.mp(), am.atk(), am.def(), am.speed(), am.usableCount());
    }

    public static ItemStack getItem(final AlchemyMaterial am, final List<AlchemyIngredients> overIngs, ItemStack item, final int overQuality, final int[] overSize, final List<AlchemyItemEffect> activeEffects, final List<Characteristic> overCharacteristics, final List<Category> overCategory, final int usableCount, final VisibleFlags visibleFlags) {
        return getItem(am, overIngs, item, overQuality, overSize, activeEffects, overCharacteristics, overCategory, visibleFlags, am.hp(), am.mp(), am.atk(), am.def(), am.speed(), am.usableCount());
    }

    private static void addLore(Lore lore, String name, String value) {
        lore.add(name + ": ").color(C.GRAY).append(value).color(C.WHITE);
    }

    private static void addLore(Lore lore, String name, int value) {
        lore.add(name + ": ").color(C.GRAY).append(value).color(C.WHITE);
    }

    private static void addLoreStatus(Lore lore, String name, int value) {
        if (value != 0) {
            lore.add(name + ": ").color(C.GRAY).append(value).color(C.WHITE);
        }
    }

    @Nullable
    public static ItemStack getItem(
            @Nullable final AlchemyMaterial alchemyMaterial,
            @Nullable final List<AlchemyIngredients> overrideIngredients,
            @Nullable ItemStack item,
            final int overQuality,
            final int[] overSize,
            @Nullable final List<AlchemyItemEffect> activeEffects,
            @Nullable final List<Characteristic> overrideCharacteristics,
            @Nullable final List<Category> overrideCategory,
            @NotNull final VisibleFlags visibleFlags,
            final int hp,
            final int mp,
            final int atk,
            final int def,
            final int speed,
            final int usableCount
    ) {
        return getItem(alchemyMaterial, overrideIngredients, item, overQuality, overSize, activeEffects, overrideCharacteristics, overrideCategory, visibleFlags, hp, mp, atk, def, speed, usableCount, 0, new ObjectArrayList<>(), new Object2ObjectOpenHashMap<>());
    }

    @Nullable
    public static ItemStack getItem(
            @Nullable final AlchemyMaterial alchemyMaterial,
            @Nullable final List<AlchemyIngredients> overrideIngredients,
            @Nullable ItemStack item,
            final int overQuality,
            final int[] overSize,
            @Nullable final List<AlchemyItemEffect> activeEffects,
            @Nullable final List<Characteristic> overrideCharacteristics,
            @Nullable final List<Category> overrideCategory,
            @NotNull final VisibleFlags visibleFlags,
            final int hp,
            final int mp,
            final int atk,
            final int def,
            final int speed,
            final int usableCount,
            final int consumedCount,
            final List<String> prefix,
            final Map<String, String> dataContainer
    ) {
        if (alchemyMaterial == null || (alchemyMaterial.ingredients().isEmpty() && (overrideIngredients == null || overrideIngredients.isEmpty()))) {
            return null;
        }
        final AlchemyItemStatus itemStatus = new AlchemyItemStatus(
                alchemyMaterial,
                null,
                overSize == null ? alchemyMaterial.sizeTemplate().getSize(Randomizer.nextInt(9)) : overSize,
                overrideCategory == null ? alchemyMaterial.categories() : overrideCategory,
                overQuality != -1 ? overQuality : Randomizer.nextInt(alchemyMaterial.qualityMax() - alchemyMaterial.qualityMin()) + alchemyMaterial.qualityMin(),
                createIngredientList(alchemyMaterial, overrideIngredients),
                createCharacteristicList(alchemyMaterial, overrideCharacteristics),
                Objects.requireNonNullElse(activeEffects, Collections.emptyList()),
                hp,
                mp,
                atk,
                def,
                speed,
                usableCount,
                consumedCount,
                prefix,
                dataContainer
        );
        return itemStatus.updateItem(item, visibleFlags);
    }

    public ItemStack updateItem(@Nullable ItemStack item) {
        return updateItem(item, new VisibleFlags(true));
    }

    public ItemStack updateItem(@Nullable ItemStack item, @NotNull VisibleFlags visibleFlags) {
        if (item == null || item.getType() == Material.AIR) {
            item = alchemyMaterial.material().toItemStack();
        }
        writeJson(item);
        final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        if (!alchemyMaterial.defaultName()) {
            meta.displayName(alchemyMaterial.name());
        }

        final PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        dataContainer.forEach((key, value) -> persistentDataContainer.set(
                new NamespacedKey(AtelierPlugin.getPlugin(), key),
                PersistentDataType.STRING,
                value
        ));

        final Lore lore = new Lore();
        if (!prefix.isEmpty()) {
            prefix.forEach(str -> lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(str)));
        }
        if (visibleFlags.id) {
            lore.add("");
        }
        if (usableCount != 0) {
            addLore(lore, "回数", usableCount - consumedCount);
        }
        if (visibleFlags.quality) {
            addLore(lore, "品質", quality);
        }
        if (visibleFlags.status) {
            addLoreStatus(lore, "HP", hp);
            addLoreStatus(lore, "MP", mp);
            addLoreStatus(lore, "攻撃力", atk);
            addLoreStatus(lore, "防御力", def);
            addLoreStatus(lore, "素早さ", speed);

            if (categories.contains(Category.WEAPON)) {
                addLore(lore, "ダメージ", alchemyMaterial.baseDamageMin() + " - " + alchemyMaterial.baseDamageMax());
            }
        }

        // 錬金成分
        if (visibleFlags.ingredients) {
            final IntObjectImmutablePair<Object2IntMap<AlchemyAttribute>> levels = getLevels();
            final int allLevel = levels.leftInt();
            final List<AlchemyAttribute> maxTypes = getMaxTypes(levels.right());
            // 錬金成分・設定
            lore.add("錬金成分: ").color(C.GRAY).append(allLevel).color(C.WHITE).append(" ");
            maxTypes.forEach(type -> lore.append("●").color(type.getColor()));
            ingredients.forEach(i -> lore.add("- ").color(C.WHITE).append(i.getName() + " : ").append(i.getLevel()).color(i.getType().getColor()));
        }
        // サイズ
        if (visibleFlags.size) {
            lore.add("サイズ:").color(C.GRAY).nextLine();
            int cSize = 0;
            for (final int i : size) {
                if (i == 0) {
                    lore.append(GameConstants.W_W).color(CommonUtils.intCColor(i));
                } else {
                    lore.append(GameConstants.W_B).color(CommonUtils.intCColor(i));
                }
                if (cSize >= 2) {
                    cSize = 0;
                    lore.nextLine();
                } else {
                    cSize++;
                }
            }
        }
        // 触媒
        if (visibleFlags.catalyst) {
            final Catalyst catalyst = alchemyMaterial.catalyst();
            if (catalyst != null) {
                lore.add("触媒:").color(C.GRAY);
                final IntList allCS = new IntArrayList();
                for (final CatalystBonus bonus : catalyst.getBonus()) {
                    if (allCS.isEmpty()) {
                        allCS.addAll(Arrays.stream(bonus.getCS()).boxed().collect(Collectors.toList()));
                    } else {
                        int c = 0;
                        for (final int i : bonus.getCS()) {
                            if (i != 0) {
                                allCS.set(c, i);
                            }
                            c++;
                        }
                    }
                }
                final int length = allCS.size();
                final int rotate_value = length == 36 ? 6 : (length == 25 ? 5 : 4);
                lore.nextLine();
                int count = 0;
                for (int i = 0; i < rotate_value; i++) {
                    for (int j = 1; j <= rotate_value; j++) {
                        final int value = allCS.getInt(count);
                        if (value == 0) {
                            lore.append(GameConstants.W_W).color(CommonUtils.intCColor(value));
                        } else {
                            lore.append(GameConstants.W_B).color(CommonUtils.intCColor(value));
                        }
                        count++;
                    }
                    lore.nextLine();
                }
            }
        }
        // 効果
        if (activeEffects != null && !activeEffects.isEmpty()) {
            lore.add("効果: ").color(C.GRAY);
            activeEffects.forEach(effect -> lore.add("- " + effect.getName()).color(C.WHITE));
        }
        // 特性
        if (!characteristics.isEmpty()) {
            lore.add("特性:").color(C.GRAY);
            characteristics.forEach(c -> lore.add("- " + c.getName()).color(C.WHITE));
        }
        // カテゴリ
        if (visibleFlags.category && !categories.isEmpty()) {
            lore.add("カテゴリ:").color(C.GRAY);
            categories.forEach(category -> lore.add("- " + category.getName()).color(C.WHITE));
        }
        // Lore終了
        if (visibleFlags.end) {
            lore.add("");
        }
        meta.lore(lore);

        // unbreaking & flag系
        ItemUtils.addHideFlags(meta, alchemyMaterial);

        // meta設定
        item.setItemMeta(meta);
        return item;
    }

    private static List<AlchemyIngredients> createIngredientList(@NotNull AlchemyMaterial alchemyMaterial, @Nullable List<AlchemyIngredients> overrideIngredients) {
        int allLevel = 0;
        final Object2IntMap<AlchemyAttribute> levels = new Object2IntOpenHashMap<>();
        final List<AlchemyIngredients> activeIngredients = new ObjectArrayList<>();
        // 錬金成分・確定取得
        if (overrideIngredients != null) {
            for (final AlchemyIngredients ingredients : overrideIngredients) {
                allLevel += getLevel(levels, activeIngredients, ingredients);
            }
        } else {
            for (final ObjectIntImmutablePair<AlchemyIngredients> dd : alchemyMaterial.ingredients()) {
                if (dd.rightInt() == 100) {
                    final AlchemyIngredients ingredients = dd.left();
                    allLevel += getLevel(levels, activeIngredients, ingredients);
                }
            }
            // 錬金成分・ランダム取得
            for (int i = 0; i < Math.min(6, Randomizer.nextInt(alchemyMaterial.ingredients().size()) + 1); i++) {
                final ObjectIntImmutablePair<AlchemyIngredients> dd = alchemyMaterial.ingredients().get(Randomizer.nextInt(alchemyMaterial.ingredients().size()));
                if (Randomizer.nextInt(100) <= dd.rightInt()) {
                    final AlchemyIngredients ingredients = dd.left();
                    if (!activeIngredients.contains(ingredients)) {
                        activeIngredients.add(ingredients);
                        int level = ingredients.getLevel();
                        final AlchemyAttribute type = ingredients.getType();
                        if (levels.containsKey(type)) {
                            levels.put(type, levels.getInt(type) + level);
                        } else {
                            levels.put(type, level);
                        }
                        allLevel += level;
                    }
                }
            }
            // 錬金成分・非マイナス化
            final List<AlchemyIngredients> materialIngredients = new ObjectArrayList<>();
            alchemyMaterial.ingredients().forEach(dd -> materialIngredients.add(dd.left()));
            while (allLevel <= 0) {
                Collections.shuffle(materialIngredients);
                for (final AlchemyIngredients ingredients : materialIngredients) {
                    if (ingredients.getLevel() > 0) {
                        allLevel += getLevel(levels, activeIngredients, ingredients);
                        break;
                    }
                }
            }
        }
        return activeIngredients;
    }

    private static List<Characteristic> createCharacteristicList(@NotNull AlchemyMaterial alchemyMaterial, @Nullable List<Characteristic> overrideCharacteristics) {
        final List<Characteristic> characteristics = new ObjectArrayList<>();
        if (overrideCharacteristics == null) {
            if (alchemyMaterial.characteristics() != null) {
                final List<ObjectIntImmutablePair<Characteristic>> cs = new ObjectArrayList<>();
                alchemyMaterial.characteristics().forEach(obj -> obj.add(cs));
                Collections.shuffle(cs);
                for (final ObjectIntImmutablePair<Characteristic> dd : cs) {
                    if (characteristics.size() < 3) {
                        if (Randomizer.nextInt(1000 /* - Math.max(500, (錬金レベル * 10)) */) <= dd.rightInt()) {
                            characteristics.add(dd.left());
                        }
                        continue;
                    }
                    break;
                }
            }
        } else {
            characteristics.addAll(overrideCharacteristics);
        }
        return characteristics;
    }

    private static int getLevel(Object2IntMap<AlchemyAttribute> levels, List<AlchemyIngredients> acls, AlchemyIngredients i) {
        acls.add(i);
        return getLevel(levels, i);
    }

    private static int getLevel(Object2IntMap<AlchemyAttribute> levels, AlchemyIngredients i) {
        final int level = i.getLevel();
        final AlchemyAttribute type = i.getType();
        if (levels.containsKey(type)) {
            levels.put(type, levels.getInt(type) + level);
        } else {
            levels.put(type, level);
        }
        return level;
    }

    @NotNull
    public IntObjectImmutablePair<Object2IntMap<AlchemyAttribute>> getLevels() {
        int allLevel = 0;
        final Object2IntMap<AlchemyAttribute> levels = new Object2IntOpenHashMap<>();
        for (final AlchemyIngredients ingredient : ingredients) {
            allLevel += getLevel(levels, ingredient);
        }
        return new IntObjectImmutablePair<>(allLevel, levels);
    }

    @NotNull
    public List<AlchemyAttribute> getMaxTypes(final Object2IntMap<AlchemyAttribute> levels) {
        int maxLevel = 0;
        final List<AlchemyAttribute> maxTypes = new ObjectArrayList<>();
        for (final Object2IntMap.Entry<AlchemyAttribute> type : levels.object2IntEntrySet()) {
            if (type.getIntValue() > maxLevel) {
                maxLevel = type.getIntValue();
                maxTypes.clear();
                maxTypes.add(type.getKey());
            } else if (type.getIntValue() == maxLevel) {
                maxTypes.add(type.getKey());
            }
        }
        return maxTypes;
    }

    @Nullable
    public static AlchemyMaterial getMaterialNullable(@Nullable ItemStack item) {
        return CObjects.nullIfFunction(load(item), AlchemyItemStatus::getAlchemyMaterial, null);

    }

    @NotNull
    public static Optional<AlchemyMaterial> getMaterialOptional(@Nullable ItemStack item) {
        return CObjects.nullIfOptional(load(item), AlchemyItemStatus::getAlchemyMaterial);
    }

    @NotNull
    public static AlchemyMaterial getMaterialNonNull(@Nullable final ItemStack item) {
        final Optional<AlchemyMaterial> material = getMaterialOptional(item);
        if (material.isPresent()) {
            return material.get();
        }
        throw new IllegalArgumentException("item is not AlchemyMaterial.");
    }

    public static int getQuality(@Nullable ItemStack item) {
        return CObjects.nullIfFunction(load(item), AlchemyItemStatus::getQuality, 0);
    }

    @NotNull
    public static List<Category> getCategories(@Nullable ItemStack item) {
        return CObjects.nullIfFunction(load(item), AlchemyItemStatus::getCategories, Collections.emptyList());
    }

    @NotNull
    public static List<Characteristic> getCharacteristics(@Nullable ItemStack item) {
        return CObjects.nullIfFunction(load(item), AlchemyItemStatus::getCharacteristics, Collections.emptyList());
    }

    @NotNull
    public static List<AlchemyIngredients> getIngredients(@Nullable ItemStack item) {
        return CObjects.nullIfFunction(load(item), AlchemyItemStatus::getIngredients, Collections.emptyList());
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

        public VisibleFlags(boolean all) {
            this(all, all, all, all, all, all, all, all);
        }

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

}
