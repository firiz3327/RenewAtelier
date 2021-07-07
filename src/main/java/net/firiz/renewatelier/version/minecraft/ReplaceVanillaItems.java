package net.firiz.renewatelier.version.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@MinecraftVersion("1.16")
public class ReplaceVanillaItems {

    private ReplaceVanillaItems() {
    }

    public static Collection<ItemStack> loot(List<ItemStack> loot) {
        return loot.stream().map(ReplaceVanillaItems::changeVanillaLore).collect(Collectors.toList());
    }

    public static void changeItems(boolean random, UnaryOperator<ItemStack> function, ItemStack... items) {
        for (final ItemStack item : items) {
            if (item != null && !changeVanillaItem(item, random, function)) {
                changeVanillaLore(item);
            }
        }
    }

    public static void changeRecipe() {
        final Iterator<Recipe> recipes = Bukkit.recipeIterator();
        final List<Recipe> addRecipes = new ObjectArrayList<>();
        while (recipes.hasNext()) {
            final Recipe recipe = recipes.next();
            final org.bukkit.Material material = recipe.getResult().getType();
            switch (material) {
                case TURTLE_HELMET,
                        LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS,
                        CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS,
                        IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS,
                        GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS,
                        DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS,
                        SHIELD:
                    recipes.remove();
                    final ShapedRecipe shapedRecipe = new ShapedRecipe(((ShapedRecipe) recipe).getKey(), ReplaceVanillaItems.changeVanillaLore(recipe.getResult()));
                    shapedRecipe.shape(((ShapedRecipe) recipe).getShape());
                    shapedRecipe.setGroup(((ShapedRecipe) recipe).getGroup());
                    for (Map.Entry<Character, RecipeChoice> entry : ((ShapedRecipe) recipe).getChoiceMap().entrySet()) {
                        shapedRecipe.setIngredient(entry.getKey(), entry.getValue());
                    }
                    addRecipes.add(shapedRecipe);
                    break;
                case NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS:
                    recipes.remove();
                    final SmithingRecipe baseRecipe = (SmithingRecipe) recipe;
                    final SmithingRecipe smithingRecipe = new SmithingRecipe(
                            baseRecipe.getKey(),
                            ReplaceVanillaItems.changeVanillaLore(baseRecipe.getResult()),
                            baseRecipe.getBase(),
                            baseRecipe.getAddition()
                    );
                    addRecipes.add(smithingRecipe);
                    break;
                default:
                    break;
            }
        }
        for (final Recipe recipe : addRecipes) {
            CommonUtils.log("[" + recipe.getClass().getSimpleName() + "] Replace Recipe: " + recipe.getResult().getType());
            Bukkit.addRecipe(recipe);
        }
    }

    public static ItemStack changeVanillaLore(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        Material type = item.getType();// 想定しない
        switch (type) {
            case TURTLE_HELMET,
                    LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS,
                    CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS,
                    IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS,
                    GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS,
                    DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS,
                    NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS:
                changeArmorLore(item, meta);
                break;
            case SHIELD:
                changeShieldLore(item, meta);
                break;
            default:
                return item;
        }
        return item;
    }

    private static void changeArmorLore(@NotNull final ItemStack item, @NotNull final ItemMeta meta) {
        final Lore lore = new Lore();
        lore.add("防御力: " + GameConstants.getItemDefense(item.getType())).color(C.GRAY);
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 0, AttributeModifier.Operation.ADD_NUMBER));
        item.setItemMeta(meta);
    }

    private static void changeShieldLore(@NotNull final ItemStack item, @NotNull final ItemMeta meta) {
        final Lore lore = new Lore();
        lore.add("盾で攻撃を防ぐと自身の防御力が").color(C.GRAY);
        lore.add("20%上昇した状態でダメージを受ける").color(C.GRAY);
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    /**
     * @param item
     * @param random
     * @return 変更に成功したか否か true=成功/false=失敗
     */
    private static boolean changeVanillaItem(@NotNull final ItemStack item, boolean random, @NotNull UnaryOperator<ItemStack> function) {
        final AlchemyMaterial material = AlchemyMaterial.getVanillaReplaceItem(item.getType());
        if (material != null) {
            return function.apply(AlchemyItemStatus.getItem(
                    material,
                    getOverrideIngredients(random, material), // override ingredients
                    item,
                    random ? -1 : material.qualityMin(), // override quality
                    random ? null : material.sizeTemplate().getSize(2), // override size
                    null,
                    random ? null : Collections.emptyList(), // override characteristics
                    random ? null : Collections.emptyList(), // override categories
                    new AlchemyItemStatus.VisibleFlags(true),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            )) != null;
        }
        return false;
    }

    @Nullable
    private static List<AlchemyIngredients> getOverrideIngredients(boolean random, AlchemyMaterial material) {
        final List<AlchemyIngredients> overrideIngredients = random ? null : new ObjectArrayList<>();
        if (overrideIngredients != null) {
            for (final ObjectIntImmutablePair<AlchemyIngredients> ing : material.ingredients()) {
                if (ing.rightInt() >= 100) {
                    overrideIngredients.add(ing.left());
                }
            }
            if (overrideIngredients.isEmpty()) {
                overrideIngredients.add(material.ingredients().get(0).left());
            }
        }
        return overrideIngredients;
    }

}
