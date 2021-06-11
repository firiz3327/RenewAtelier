package net.firiz.renewatelier.alchemy.material;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.characteristic.ICharacteristic;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.AlchemyMaterialLoader;
import net.firiz.renewatelier.inventory.item.CustomModelMaterial;
import net.firiz.renewatelier.skills.item.EnumItemSkill;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public record AlchemyMaterial(
        String id, Component name,
        boolean defaultName,
        CustomModelMaterial material,
        AlchemyMaterialCategory materialCategory,
        int qualityMin, int qualityMax, int price, int hp, int mp, int atk, int def, int speed,
        int baseDamageMin, int baseDamageMax, int itemDamageMin, int itemDamageMax,
        List<Category> categories,
        List<ObjectIntImmutablePair<AlchemyIngredients>> ingredients,
        MaterialSizeTemplate sizeTemplate,
        List<ICharacteristic> characteristics,
        Catalyst catalyst,
        @Nullable String script,
        @Nullable EnumItemSkill itemSkill,
        int usableCount, double itemCooldown, boolean unbreaking, boolean hideAttribute,
        boolean hideDestroy, boolean hideEnchant, boolean hidePlacedOn, boolean hidePotionEffect,
        boolean hideUnbreaking
) {

    private static final AlchemyMaterialLoader LOADER;

    static {
        LOADER = (AlchemyMaterialLoader) ConfigManager.INSTANCE.getLoader(AlchemyMaterialLoader.class, AlchemyMaterial.class);
    }

    public AlchemyMaterial(
            String id,
            Component name,
            boolean defaultName,
            CustomModelMaterial material,
            AlchemyMaterialCategory materialCategory,
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
            int itemDamageMin,
            int itemDamageMax,
            List<Category> categories,
            List<ObjectIntImmutablePair<AlchemyIngredients>> ingredients,
            MaterialSizeTemplate sizeTemplate,
            List<ICharacteristic> characteristics,
            Catalyst catalyst,
            @Nullable String script,
            @Nullable EnumItemSkill itemSkill,
            int usableCount, double itemCooldown, boolean unbreaking,
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
        this.materialCategory = materialCategory;
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
        this.itemDamageMin = itemDamageMin;
        this.itemDamageMax = itemDamageMax;
        this.categories = categories;
        this.ingredients = ingredients;
        this.sizeTemplate = sizeTemplate;
        this.characteristics = characteristics;
        this.catalyst = catalyst;
        this.script = script;
        this.itemSkill = itemSkill;
        this.usableCount = usableCount;
        this.itemCooldown = itemCooldown;
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
            if (am.id.equalsIgnoreCase(id)) {
                return am;
            }
        }
        return null;
    }

    @NotNull
    public static AlchemyMaterial getMaterial(@NotNull final String id) {
        for (final AlchemyMaterial am : LOADER.getList()) {
            if (am.id.equalsIgnoreCase(id)) {
                return am;
            }
        }
        throw new IllegalArgumentException(id.concat(" not found."));
    }

    public Component getName() {
        return name;
    }

    public boolean hasUsefulCatalyst(final AlchemyRecipe recipe) {
        final List<RequireMaterial> catalystCategories = recipe.getCatalystCategories();
        if (catalyst != null) {
            for (final RequireMaterial requireMaterial : catalystCategories) {
                if (this.equals(requireMaterial.getMaterial())) {
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
}
