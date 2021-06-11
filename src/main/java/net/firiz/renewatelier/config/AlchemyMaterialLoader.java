package net.firiz.renewatelier.config;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicData;
import net.firiz.renewatelier.characteristic.CharacteristicTemplate;
import net.firiz.renewatelier.characteristic.ICharacteristic;
import net.firiz.renewatelier.inventory.item.CustomModelMaterial;
import net.firiz.renewatelier.inventory.item.PotionMaterial;
import net.firiz.renewatelier.skills.item.EnumItemSkill;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.utils.CustomConfig;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
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
    private static final String ALL_KEY_ALCHEMY_MATERIAL_CATEGORY = "allAlchemyMaterialCategory";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_ALCHEMY_MATERIAL_CATEGORY = "alchemyMaterialCategory";
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
        final AlchemyMaterialCategory allAlchemyMaterialCategory;
        if (config.contains(ALL_KEY_ALCHEMY_MATERIAL_CATEGORY)) {
            allAlchemyMaterialCategory = AlchemyMaterialCategory.search(Objects.requireNonNull(config.getString(ALL_KEY_ALCHEMY_MATERIAL_CATEGORY)));
        } else {
            allAlchemyMaterialCategory = null;
        }
        final Set<String> keys = config.getKeys(false);
        keys.stream().filter(key -> !(key.equals(ALL_KEY_ALCHEMY_MATERIAL_CATEGORY))).forEach(key -> {
            try {
                final ConfigurationSection item = config.getConfigurationSection(key);
                assert item != null;

                final CustomModelMaterial materialData = getMaterial(item, key); // *
                final boolean defaultName = getBoolean(item, "default_name");
                final Component name = getName(item, defaultName, materialData); // *
                final AlchemyMaterialCategory materialCategory = allAlchemyMaterialCategory == null ? getAlchemyMaterialCategory(item) : allAlchemyMaterialCategory; // *
                final int quality_min = getQualityMin(item); // *
                final int quality_max = getQualityMax(item); // *
                final int price = item.getInt("price", 1);
                final List<Category> categories = getCategories(item); // *
                final List<ObjectIntImmutablePair<AlchemyIngredients>> ingredients = getIngredients(item); // *
                final MaterialSizeTemplate sizeTemplate = getSize(item);
                final List<ICharacteristic> characteristics = getCharacteristics(item);
                final Catalyst catalyst = getCatalyst(item);
                final EnumItemSkill itemSkill = getItemSkill(item);
                final int usableCount = item.getInt("usableCount", 0);
                final double itemCooldown = item.getDouble("itemCooldown", 0);

                if (notFounds.isEmpty()) {
                    final AlchemyMaterial material = new AlchemyMaterial(
                            key,
                            name,
                            defaultName,
                            materialData,
                            materialCategory,
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
                            getValueOrZero(item, "itemDamageMin"),
                            getValueOrZero(item, "itemDamageMax"),
                            categories,
                            ingredients,
                            sizeTemplate,
                            characteristics,
                            catalyst,
                            item.getString("script"),
                            itemSkill,
                            usableCount,
                            itemCooldown,
                            getBoolean(item, "unbreaking"),
                            getBoolean(item, "hideAttribute"),
                            getBoolean(item, "hideDestroy"),
                            getBoolean(item, "hideEnchant"),
                            getBoolean(item, "hidePlacedOn"),
                            getBoolean(item, "hidePotionEffect"),
                            getBoolean(item, "hideUnbreaking")
                    );
                    if (material.defaultName()) {
                        if (material.material().getCustomModel() == 0) {
                            vanillaReplaceItems.put(material.material().getMaterial(), material);
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
            final String materialStr = item.getString(KEY_MATERIAL);
            assert materialStr != null;
            if (materialStr.contains(",")) {
                final String[] matSplit = materialStr.split(",");
                if (matSplit[0].equalsIgnoreCase("potion")) {
                    result = new PotionMaterial(
                            PotionMaterial.Type.valueOf("HIDE_" + matSplit[1].toUpperCase()),
                            Integer.parseInt(matSplit[2]),
                            Color.fromRGB(
                                    Integer.parseInt(matSplit[3]),
                                    Integer.parseInt(matSplit[4]),
                                    Integer.parseInt(matSplit[5])
                            )
                    );
                } else {
                    result = new CustomModelMaterial(ItemUtils.getMaterial(matSplit[0]), Integer.parseInt(matSplit[1]));
                }
            } else {
                if (materialStr.equalsIgnoreCase("xxx")) {
                    CommonUtils.logWhiteWarning(PREFIX.concat(key).concat(" -> No customModelData value has been set for XXX."));
                }
                result = new CustomModelMaterial(ItemUtils.getMaterial(materialStr), 0);
            }
        } else {
            result = null;
            notFounds.add(KEY_MATERIAL);
        }
        return result;
    }

    @Nullable
    private Component getName(ConfigurationSection item, boolean defaultName, final CustomModelMaterial mat) {
        Component name = null;
        if (defaultName && mat != null) {
            name = Text.itemName(LanguageItemUtil.getLocalizeName(new ItemStack(mat.getMaterial())));
        } else {
            if (item.contains("name")) {
                name = new Text(LegacyComponentSerializer.legacyAmpersand().deserialize(Objects.requireNonNull(item.getString("name"))));
            } else {
                notFounds.add("name");
            }
        }
        return name;
    }

    @Nullable
    private AlchemyMaterialCategory getAlchemyMaterialCategory(ConfigurationSection item) {
        if (item.contains(KEY_ALCHEMY_MATERIAL_CATEGORY)) {
            return AlchemyMaterialCategory.search(Objects.requireNonNull(item.getString(KEY_ALCHEMY_MATERIAL_CATEGORY)));
        }
        notFounds.add(KEY_ALCHEMY_MATERIAL_CATEGORY);
        return null;
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
    private List<ObjectIntImmutablePair<AlchemyIngredients>> getIngredients(ConfigurationSection item) {
        final List<ObjectIntImmutablePair<AlchemyIngredients>> ingredients = new ObjectArrayList<>();
        if (item.contains(KEY_INGREDIENTS)) {
            final List<String> ingsStr = CommonUtils.cast(item.getList(KEY_INGREDIENTS));
            if (ingsStr != null) {
                ingsStr.forEach(ing -> {
                    final String[] ingData = ing.split(",");
                    ingredients.add(new ObjectIntImmutablePair<>(
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
    private List<ICharacteristic> getCharacteristics(ConfigurationSection item) {
        final List<ICharacteristic> characteristics = new ObjectArrayList<>();
        if (item.contains("characteristics")) {
            final List<String> stringList = CommonUtils.cast(item.getList("characteristics"));
            assert stringList != null;
            stringList.forEach(cStr -> {
                if (cStr.contains(",")) {
                    final String[] strArray = cStr.split(",");
                    final String id = strArray[0].trim().toUpperCase();
                    Characteristic c;
                    try {
                        c = Characteristic.getCharacteristic(id);
                    } catch (IllegalArgumentException e) {
                        c = Characteristic.search(id);
                    }
                    characteristics.add(new CharacteristicData(c, Integer.parseInt(strArray[1].trim())));
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
        if (item.contains("itemSkill")) {
            return EnumItemSkill.valueOf(Objects.requireNonNull(item.getString("itemSkill")).toUpperCase());
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
