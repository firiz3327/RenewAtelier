package net.firiz.renewatelier.config;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicTemplate;
import net.firiz.renewatelier.item.CustomModelMaterial;
import net.firiz.renewatelier.skill.item.EnumItemSkill;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.ItemUtils;
import net.firiz.renewatelier.utils.CustomConfig;
import net.firiz.renewatelier.utils.chores.CollectionUtils;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public class AlchemyMaterialLoader extends ConfigLoader<AlchemyMaterial> {

    private static final List<String> notFounds = new ObjectArrayList<>();
    private static final String PREFIX = "MaterialLoader: ";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_QUALITY_MIN = "quality_min";
    private static final String KEY_QUALITY_MAX = "quality_max";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_INGREDIENTS = "ingredients";

    private final Map<Material, AlchemyMaterial> vanillaReplaceItems = new Object2ObjectOpenHashMap<>();

    AlchemyMaterialLoader() {
        super(new File(AtelierPlugin.getPlugin().getDataFolder(), "materials"), true);
    }

    @Override
    protected void initClear() {
        vanillaReplaceItems.clear();
    }

    @Override
    protected void loadConfig(final FileConfiguration config) {
        notFounds.clear();
        final AtomicInteger errorCount = new AtomicInteger(0);
        final Set<String> keys = config.getKeys(false);
        keys.forEach(key -> {
            try {
                final ConfigurationSection item = config.getConfigurationSection(key);
                assert item != null;

                final CustomModelMaterial materialData = getMaterial(item, key); // *
                final boolean defaultName = getBoolean(item, "default_name");
                final String name = getName(item, defaultName, materialData); // *
                final int quality_min = getQualityMin(item); // *
                final int quality_max = getQualityMax(item); // *
                final int price = item.contains("price") ? item.getInt("price") : 1;
                final List<Category> categories = getCategories(item); // *
                final List<ImmutablePair<AlchemyIngredients, Integer>> ingredients = getIngredients(item); // *
                final MaterialSizeTemplate sizeTemplate = getSize(item);
                final List<Object> characteristics = getCharacteristics(item);
                final Catalyst catalyst = getCatalyst(item);
                final EnumItemSkill itemSkill = getItemSkill(item);

                if (notFounds.isEmpty()) {
                    final AlchemyMaterial material = new AlchemyMaterial(
                            key,
                            name,
                            defaultName,
                            materialData,
                            quality_min,
                            quality_max,
                            price,
                            getValueOrZero(item, "hp"),
                            getValueOrZero(item, "mp"),
                            getValueOrZero(item, "atk"),
                            getValueOrZero(item, "def"),
                            getValueOrZero(item, "speed"),
                            getValueOrZero(item, "baseDamageMin"),
                            getValueOrZero(item, "baseDamageMax"),
                            getValueOrZero(item, "power"),
                            categories,
                            ingredients,
                            sizeTemplate,
                            characteristics,
                            catalyst,
                            item.getString("script"),
                            itemSkill,
                            getBoolean(item, "unbreaking"),
                            getBoolean(item, "hideAttribute"),
                            getBoolean(item, "hideDestroy"),
                            getBoolean(item, "hideEnchant"),
                            getBoolean(item, "hidePlacedOn"),
                            getBoolean(item, "hidePotionEffect"),
                            getBoolean(item, "hideUnbreaking")
                    );
                    if (material.isDefaultName()) {
                        if (material.getMaterial().getCustomModel() == 0) {
                            vanillaReplaceItems.put(material.getMaterial().getMaterial(), material);
                        } else {
                            throw new IllegalStateException("default name items cannot be assigned a custom model.");
                        }
                    }
                    add(material);
                }
            } catch (Exception ex) {
                CommonUtils.logWarning(PREFIX.concat(key).concat(" -> "), ex);
                errorCount.incrementAndGet();
            } finally {
                if (!notFounds.isEmpty()) {
                    CommonUtils.logWarning(PREFIX.concat(key).concat(" -> Not found columns for ").concat(notFounds.toString()).concat("."));
                    errorCount.incrementAndGet();
                }
            }
        });
        if (errorCount.intValue() != 0) {
            CommonUtils.logWhiteWarning("error founded.");
        }
        final String fileName = ((CustomConfig.CConfiguration) config).getConfigFile().getName();
        CommonUtils.log(PREFIX + fileName + " - " + getList().size() + " loaded and " + errorCount + " errors found.");
    }

    @NotNull
    public Map<Material, AlchemyMaterial> getVanillaReplaceItems() {
        return Collections.unmodifiableMap(vanillaReplaceItems);
    }

    @Nullable
    private CustomModelMaterial getMaterial(ConfigurationSection item, String key) {
        final CustomModelMaterial result;
        if (item.contains(KEY_MATERIAL)) {
            final String mat_str = item.getString(KEY_MATERIAL);
            assert mat_str != null;
            if (!mat_str.contains(",")) {
                if (mat_str.equalsIgnoreCase("XXX")) {
                    CommonUtils.logWhiteWarning(PREFIX.concat(key).concat(" -> No customModelData value has been set for XXX."));
                }
                result = new CustomModelMaterial(ItemUtils.getMaterial(mat_str), 0);
            } else {
                final String[] matSplit = mat_str.split(",");
                result = new CustomModelMaterial(ItemUtils.getMaterial(matSplit[0]), Integer.parseInt(matSplit[1]));
            }
        } else {
            result = null;
            notFounds.add(KEY_MATERIAL);
        }
        return result;
    }

    @Nullable
    private String getName(ConfigurationSection item, boolean defaultName, final CustomModelMaterial mat) {
        String name = null;
        if (defaultName && mat != null) {
            name = ChatColor.RESET + LanguageItemUtil.getLocalizeName(new ItemStack(mat.getMaterial())); // クエストブック用に名前を設定しておく
        } else {
            if (item.contains("name")) {
                name = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(item.getString("name")));
            } else {
                notFounds.add("name");
            }
        }
        return name;
    }

    private int getQualityMin(ConfigurationSection item) {
        final int quality;
        if (item.contains(KEY_QUALITY_MIN)) {
            quality = item.getInt(KEY_QUALITY_MIN);
        } else {
            quality = 0;
            notFounds.add(KEY_QUALITY_MIN);
        }
        return quality;
    }

    private int getQualityMax(ConfigurationSection item) {
        final int quality;
        if (item.contains(KEY_QUALITY_MAX)) {
            quality = item.getInt(KEY_QUALITY_MAX);
        } else {
            quality = 0;
            notFounds.add(KEY_QUALITY_MAX);
        }
        return quality;
    }

    @NotNull
    private List<Category> getCategories(ConfigurationSection item) {
        final List<Category> categories = new ObjectArrayList<>();
        if (item.contains(KEY_CATEGORIES)) {
            final List<String> categoriesStr = CommonUtils.cast(item.getList(KEY_CATEGORIES));
            categoriesStr.forEach(cStr -> categories.add(Category.searchName(cStr)));
        } else {
            notFounds.add(KEY_CATEGORIES);
        }
        return categories;
    }

    @NotNull
    private List<ImmutablePair<AlchemyIngredients, Integer>> getIngredients(ConfigurationSection item) {
        final List<ImmutablePair<AlchemyIngredients, Integer>> ingredients = new ObjectArrayList<>();
        if (item.contains(KEY_INGREDIENTS)) {
            final List<String> ingsStr = CommonUtils.cast(item.getList(KEY_INGREDIENTS));
            if (ingsStr != null) {
                ingsStr.forEach(ing -> {
                    final String[] ingData = ing.split(",");
                    ingredients.add(new ImmutablePair<>(
                            AlchemyIngredients.searchName(ingData[0].trim()),
                            Integer.parseInt(ingData[1].trim())
                    ));
                });
            }
        } else {
            notFounds.add(KEY_INGREDIENTS);
        }
        return ingredients;
    }

    @Nullable
    private MaterialSizeTemplate getSize(ConfigurationSection item) {
        if (item.contains("size")) {
            return MaterialSizeTemplate.valueOf("TYPE" + item.getInt("size"));
        }
        notFounds.add("size");
        return null;
    }

    @NotNull
    private List<Object> getCharacteristics(ConfigurationSection item) {
        final List<Object> characteristics = new ObjectArrayList<>();
        if (item.contains("characteristics")) {
            final List<String> stringList = CommonUtils.cast(item.getList("characteristics"));
            stringList.forEach(cStr -> {
                if (cStr.contains(",")) {
                    final String[] strs = cStr.split(",");
                    final String id = strs[0].trim().toUpperCase();
                    Characteristic c;
                    try {
                        c = Characteristic.getCharacteristic(id);
                    } catch (IllegalArgumentException e) {
                        c = Characteristic.search(id);
                    }
                    characteristics.add(new ImmutablePair<>(c, Integer.parseInt(strs[1].trim())));
                } else {
                    characteristics.add(CharacteristicTemplate.valueOf(cStr.toUpperCase()));
                }
            });
        }
        return characteristics;
    }

    @Nullable
    private Catalyst getCatalyst(ConfigurationSection item) {
        if (item.contains("catalyst")) {
            final ConfigurationSection catalystConfig = item.getConfigurationSection("catalyst");
            final List<CatalystBonus> bonus = new ObjectArrayList<>();
            assert catalystConfig != null;
            catalystConfig.getKeys(false).stream()
                    .filter(cKey -> (cKey.startsWith("bonus")))
                    .map(catalystConfig::getConfigurationSection)
                    .filter(Objects::nonNull)
                    .forEachOrdered(sec -> {
                        final List<Integer> size = sec.getIntegerList("size");
                        bonus.add(new CatalystBonus(
                                CollectionUtils.parseInts(size),
                                new CatalystBonusData(
                                        CatalystBonusData.BonusType.valueOf(sec.getString("type")),
                                        sec.contains("x") ? sec.getInt("x") : 0,
                                        sec.contains("y") ? sec.getString("y") : null
                                )
                        ));
                    });
            return new Catalyst(bonus);
        }
        return null;
    }

    @Nullable
    private EnumItemSkill getItemSkill(ConfigurationSection item) {
        if (item.contains("item_skill")) {
            return EnumItemSkill.valueOf(Objects.requireNonNull(item.getString("item_skill")).toUpperCase());
        }
        return null;
    }

    private boolean getBoolean(ConfigurationSection item, String name) {
        return item.contains(name) && item.getBoolean(name);
    }

    private int getValueOrZero(ConfigurationSection item, String name) {
        return item.contains(name) ? item.getInt(name) : 0;
    }

}
