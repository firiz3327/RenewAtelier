package net.firiz.renewatelier.version.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                case TURTLE_HELMET:
                case LEATHER_HELMET:
                case LEATHER_CHESTPLATE:
                case LEATHER_LEGGINGS:
                case LEATHER_BOOTS:
                case CHAINMAIL_HELMET:
                case CHAINMAIL_CHESTPLATE:
                case CHAINMAIL_LEGGINGS:
                case CHAINMAIL_BOOTS:
                case IRON_HELMET:
                case IRON_CHESTPLATE:
                case IRON_LEGGINGS:
                case IRON_BOOTS:
                case GOLDEN_HELMET:
                case GOLDEN_CHESTPLATE:
                case GOLDEN_LEGGINGS:
                case GOLDEN_BOOTS:
                case DIAMOND_HELMET:
                case DIAMOND_CHESTPLATE:
                case DIAMOND_LEGGINGS:
                case DIAMOND_BOOTS:
                case SHIELD:
                    recipes.remove();
                    final ShapedRecipe shapedRecipe = new ShapedRecipe(((ShapedRecipe) recipe).getKey(), ReplaceVanillaItems.changeVanillaLore(recipe.getResult()));
                    shapedRecipe.shape(((ShapedRecipe) recipe).getShape());
                    shapedRecipe.setGroup(((ShapedRecipe) recipe).getGroup());
                    for (Map.Entry<Character, RecipeChoice> entry : ((ShapedRecipe) recipe).getChoiceMap().entrySet()) {
                        shapedRecipe.setIngredient(entry.getKey(), entry.getValue());
                    }
                    addRecipes.add(shapedRecipe);
                    break;
                case NETHERITE_HELMET:
                case NETHERITE_CHESTPLATE:
                case NETHERITE_LEGGINGS:
                case NETHERITE_BOOTS:
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
        switch (item.getType()) {
            case TURTLE_HELMET:
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                changeArmorLore(item, meta);
                break;
            case SHIELD:
                changeShieldLore(item, meta);
                break;
            default: // 想定しない
                return item;
        }
        return item;
    }

    private static void changeArmorLore(@NotNull final ItemStack item, @NotNull final ItemMeta meta) {
        final List<String> lore = new ObjectArrayList<>();
        lore.add(ChatColor.GRAY + "防御力: " + GameConstants.getItemDefense(item.getType()));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 0, AttributeModifier.Operation.ADD_NUMBER));
        item.setItemMeta(meta);
    }

    private static void changeShieldLore(@NotNull final ItemStack item, @NotNull final ItemMeta meta) {
        final List<String> lore = new ObjectArrayList<>();
        lore.add(ChatColor.GRAY + "盾で攻撃を防ぐと自身の防御力が");
        lore.add(ChatColor.GRAY + "20%上昇した状態でダメージを受ける");
        meta.setLore(lore);
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
