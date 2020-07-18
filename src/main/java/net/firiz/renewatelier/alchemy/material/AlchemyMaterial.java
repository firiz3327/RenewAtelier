package net.firiz.renewatelier.alchemy.material;

import java.util.List;
import java.util.Map;

import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.AlchemyMaterialLoader;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.item.CustomModelMaterial;
import net.firiz.renewatelier.skill.item.EnumItemSkill;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public final class AlchemyMaterial {

    private static final AlchemyMaterialLoader LOADER;

    static {
        LOADER = (AlchemyMaterialLoader) ConfigManager.INSTANCE.getLoader(AlchemyMaterialLoader.class, AlchemyMaterial.class);
    }

    private final String id;
    private final String name;
    private final boolean defaultName;
    private final CustomModelMaterial material;
    private final int qualityMin;
    private final int qualityMax;
    private final int price;
    private final int hp; // equip
    private final int mp; // equip
    private final int atk; // equip
    private final int def; // equip
    private final int speed; // equip
    private final int baseDamageMin; // weapon
    private final int baseDamageMax; // weapon
    private final int power; // usable item
    private final List<Category> categories;
    private final List<ImmutablePair<AlchemyIngredients, Integer>> ingredients;
    private final MaterialSizeTemplate sizeTemplate;
    private final List<Object> charas;
    private final Catalyst catalyst;
    @Nullable
    private final String script;
    @Nullable
    private final EnumItemSkill itemSkill; // usable item
    private final boolean unbreaking;
    private final boolean hideAttribute;
    private final boolean hideDestroy;
    private final boolean hideEnchant;
    private final boolean hidePlacedOn;
    private final boolean hidePotionEffect;
    private final boolean hideUnbreaking;

    public AlchemyMaterial(
            String id,
            String name,
            boolean defaultName,
            CustomModelMaterial material,
            int qualityMin,
            int qualityMax,
            int price,
            int hp,
            int mp,
            int atk,
            int def,
            int speed,
            int baseDamageMin,
            int baseDamageMax,
            int power,
            List<Category> categories,
            List<ImmutablePair<AlchemyIngredients, Integer>> ingredients,
            MaterialSizeTemplate sizeTemplate,
            List<Object> charas,
            Catalyst catalyst,
            @Nullable String script,
            @Nullable EnumItemSkill itemSkill,
            boolean unbreaking,
            boolean hideAttribute,
            boolean hideDestroy,
            boolean hideEnchant,
            boolean hidePlacedOn,
            boolean hidePotionEffect,
            boolean hideUnbreaking
    ) {
        this.id = id;
        this.name = name;
        this.defaultName = defaultName;
        this.material = material;
        this.qualityMin = qualityMin;
        this.qualityMax = qualityMax;
        this.price = price;
        this.hp = hp;
        this.mp = mp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.baseDamageMin = baseDamageMin;
        this.baseDamageMax = baseDamageMax;
        this.power = power;
        this.categories = categories;
        this.ingredients = ingredients;
        this.sizeTemplate = sizeTemplate;
        this.charas = charas;
        this.catalyst = catalyst;
        this.script = script;
        this.itemSkill = itemSkill;
        this.unbreaking = unbreaking;
        this.hideAttribute = hideAttribute;
        this.hideDestroy = hideDestroy;
        this.hideEnchant = hideEnchant;
        this.hidePlacedOn = hidePlacedOn;
        this.hidePotionEffect = hidePotionEffect;
        this.hideUnbreaking = hideUnbreaking;
    }

    @Nullable
    public static AlchemyMaterial getVanillaReplaceItem(@NotNull final Material material) {
        final Map<Material, AlchemyMaterial> vanillaReplaceItems = LOADER.getVanillaReplaceItems();
        return vanillaReplaceItems.get(material);
    }

    @Nullable
    public static AlchemyMaterial getMaterialOrNull(@NotNull final String id) {
        for (final AlchemyMaterial am : LOADER.getList()) {
            if (am.getId().equalsIgnoreCase(id)) {
                return am;
            }
        }
        return null;
    }

    @NotNull
    public static AlchemyMaterial getMaterial(@NotNull final String id) {
        for (final AlchemyMaterial am : LOADER.getList()) {
            if (am.getId().equalsIgnoreCase(id)) {
                return am;
            }
        }
        throw new IllegalArgumentException(id.concat(" not found."));
    }

    @Nullable
    public static AlchemyMaterial getMaterialOrNull(@NotNull final ItemStack item) {
        return AlchemyItemStatus.getMaterial(item);
    }

    @NotNull
    public static AlchemyMaterial getMaterial(@NotNull final ItemStack item) {
        final AlchemyMaterial material = getMaterialOrNull(item);
        if (material == null) {
            throw new IllegalArgumentException("item is not AlchemyMaterial.");
        }
        return material;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultName() {
        return defaultName;
    }

    public CustomModelMaterial getMaterial() {
        return material;
    }

    public int getQualityMin() {
        return qualityMin;
    }

    public int getQualityMax() {
        return qualityMax;
    }

    public int getPrice() {
        return price;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getSpeed() {
        return speed;
    }

    public int getBaseDamageMin() {
        return baseDamageMin;
    }

    public int getBaseDamageMax() {
        return baseDamageMax;
    }

    public int getPower() {
        return power;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<ImmutablePair<AlchemyIngredients, Integer>> getIngredients() {
        return ingredients;
    }

    public MaterialSizeTemplate getSizeTemplate() {
        return sizeTemplate;
    }

    public List<Object> getCharas() {
        return charas;
    }

    public Catalyst getCatalyst() {
        return catalyst;
    }

    @Nullable
    public String getScript() {
        return script;
    }

    @Nullable
    public EnumItemSkill getItemSkill() {
        return itemSkill;
    }

    public boolean hasUsefulCatalyst(final AlchemyRecipe recipe) {
        final List<RequireMaterial> catalystCategories = recipe.getCatalystCategories();
        if (catalyst != null) {
            for (final RequireMaterial requireMaterial : catalystCategories) {
                if (requireMaterial.getMaterial().equals(this)) {
                    return true;
                }
                for (final Category c : categories) {
                    if (requireMaterial.getCategory() == c) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isUnbreaking() {
        return unbreaking;
    }

    public boolean isHideAttribute() {
        return hideAttribute;
    }

    public boolean isHideDestroy() {
        return hideDestroy;
    }

    public boolean isHideEnchant() {
        return hideEnchant;
    }

    public boolean isHidePlacedOn() {
        return hidePlacedOn;
    }

    public boolean isHidePotionEffect() {
        return hidePotionEffect;
    }

    public boolean isHideUnbreaking() {
        return hideUnbreaking;
    }
}
