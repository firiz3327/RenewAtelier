package net.firiz.renewatelier.version.minecraft;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import com.destroystokyo.paper.loottable.LootableEntityInventory;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftLootTable;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventory;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ReplaceVanillaItems {

    private ReplaceVanillaItems() {
    }

    /**
     * Lootableが実装されているエンティティ・ブロックのアイテム達をアトリエアイテムへ変更する
     * ※ Lootableを実装しているMobクラスはアイテムドロップ時に更新するのでここでは記載しない
     *
     * @param damager Entity Lootの破壊者
     * @param loot    Lootable Loot(Block or Entity)
     */
    public static void loot(@Nullable Entity damager, @NotNull Lootable loot) {
        if (loot.hasLootTable()) {
            final LootTable lootTable = Objects.requireNonNull(loot.getLootTable());
            Inventory inv = null;
            LootContext.Builder builder = null;

            if (loot instanceof LootableEntityInventory) {
                /*
                LootableEntityInventory --- paper only
                該当クラス: HopperMinecart, StorageMinecart
                 */
                final Entity entity = ((LootableEntityInventory) loot).getEntity();
                inv = ((InventoryHolder) entity).getInventory();
                builder = new LootContext.Builder(entity.getLocation());
            } else if (loot instanceof LootableBlockInventory) {
                /*
                LootableBlockInventory --- paper only
                該当クラス: Chest, Dispenser, Dropper, Hopper, ShulkerBox
                 */
                final Block block = ((LootableBlockInventory) loot).getBlock();
                inv = ((InventoryHolder) block.getState()).getInventory();
                builder = new LootContext.Builder(block.getLocation());
            } else if (loot instanceof BlockInventoryHolder) {
                /*
                Lootable and BlockInventoryHolder
                該当クラス: Barrel
                 */
                final BlockInventoryHolder holder = (BlockInventoryHolder) loot;
                inv = holder.getInventory();
                builder = new LootContext.Builder(holder.getBlock().getLocation());
            }

            if (inv != null) {
                loot.clearLootTable();
                fillInventory(inv, lootTable, builder.build());
                changeItems(true, item -> item, inv.getContents());
            }
        }
    }

    /*
     * CraftLootTableのfillInventoryを元に作成
     */
    private static void fillInventory(@NotNull Inventory inventory, @NotNull LootTable lootTable, @NotNull LootContext context) {
        final CraftLootTable craftLootTable = ((CraftLootTable) lootTable);
        final LootTableInfo nmsContext = convertContext(context, craftLootTable);
        final CraftInventory craftInventory = (CraftInventory) inventory;
        final IInventory handle = craftInventory.getInventory();
        craftLootTable.getHandle().fillInventory(handle, nmsContext);
    }

    /*
     * CraftLootTableのconvertContextを元に作成
     */
    private static LootTableInfo convertContext(@NotNull LootContext context, @NotNull CraftLootTable lootTable) {
        Location loc = context.getLocation();
        WorldServer handle = ((CraftWorld) loc.getWorld()).getHandle();
        LootTableInfo.Builder builder = new LootTableInfo.Builder(handle);
        if (lootTable.getHandle() != net.minecraft.server.v1_15_R1.LootTable.EMPTY) {
            builder.set(LootContextParameters.POSITION, new BlockPosition(
                    context.getLocation().getX(),
                    context.getLocation().getY(),
                    context.getLocation().getZ()
            ));
        }
        return builder.build(lootTable.getHandle().getLootContextParameterSet());
    }

    public static void changeItems(boolean random, UnaryOperator<ItemStack> function, ItemStack... items) {
        for (final ItemStack item : items) {
            if (item != null && !changeVanillaItem(item, random, function)) {
                changeVanillaLore(item);
            }
        }
    }

    /*
    private static Field fieldCookingRecipe;
    private static Field fieldStonecuttingRecipe;
    private static Field fieldMerchantRecipe;
    private static Field fieldShapedRecipe;
    private static Field fieldShaplessRecipe;

    static {
        try {
            fieldCookingRecipe = CookingRecipe.class.getDeclaredField("output");
            fieldStonecuttingRecipe = StonecuttingRecipe.class.getDeclaredField("output");
            fieldMerchantRecipe = MerchantRecipe.class.getDeclaredField("result");;
            fieldShapedRecipe = ShapedRecipe.class.getDeclaredField("output");
            fieldShaplessRecipe = ShapelessRecipe.class.getDeclaredField("output");
        } catch (NoSuchFieldException e) {
            Chore.logWarning(e);
        }
    }
    */

    public static void changeRecipe() {
        final Iterator<Recipe> recipes = Bukkit.recipeIterator();
        final List<Recipe> addRecipes = new ArrayList<>();
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
                default:
                    // レシピも置き換えるがとりあえず未使用
//                    if (AlchemyMaterial.getVanillaReplaceItem(material) != null) {
//                        recipes.remove();
//                        final ItemStack result = recipe.getResult();
//                        changeItems(false, result);
//                        try {
//                            final Field outputField;
//                            if (recipe instanceof CookingRecipe) { // BlastingRecipe, CampfireRecipe, FurnaceRecipe, SmokingRecipe
//                                outputField = fieldCookingRecipe;
//                            } else if (recipe instanceof StonecuttingRecipe) {
//                                outputField = fieldStonecuttingRecipe;
//                            } else if (recipe instanceof MerchantRecipe) {
//                                outputField = fieldMerchantRecipe;
//                            } else if (recipe instanceof ShapedRecipe) {
//                                outputField = fieldShapedRecipe;
//                            } else if (recipe instanceof ShapelessRecipe) {
//                                outputField = fieldShaplessRecipe;
//                            } else {
//                                throw new IllegalStateException("not support recipe class. " + recipe.getClass());
//                            }
//                            outputField.setAccessible(true);
//                            outputField.set(recipe, result);
//                        } catch (IllegalAccessException e) {
//                            Chore.logWarning(e);
//                        }
//                        addRecipes.add(recipe);
//                    }
                    break;
            }
        }
        for (final Recipe recipe : addRecipes) {
            Chore.log("[" + recipe.getClass().getSimpleName() + "] Replace Recipe: " + recipe.getResult().getType());
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
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
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
        final List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "防御力: " + GameConstants.getVanillaItemDefense(item.getType()));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 0, AttributeModifier.Operation.ADD_NUMBER));
        item.setItemMeta(meta);
    }

    private static void changeShieldLore(@NotNull final ItemStack item, @NotNull final ItemMeta meta) {
        final List<String> lore = new ArrayList<>();
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
    public static boolean changeVanillaItem(@NotNull final ItemStack item, boolean random, @NotNull UnaryOperator<ItemStack> function) {
        final AlchemyMaterial material = AlchemyMaterial.getVanillaReplaceItem(item.getType());
        if (material != null) {
            List<AlchemyIngredients> overrideIngredients = random ? null : new ArrayList<>();
            if (overrideIngredients != null) {
                for (ImmutablePair<AlchemyIngredients, Integer> ing : material.getIngredients()) {
                    if (ing.getRight() >= 100) {
                        overrideIngredients.add(ing.getLeft());
                    }
                }
                if (overrideIngredients.isEmpty()) {
                    overrideIngredients.add(material.getIngredients().get(0).getLeft());
                }
            }
            return function.apply(AlchemyItemStatus.getItem(
                    material,
                    overrideIngredients, // override ingredients
                    item,
                    random ? -1 : material.getQualityMin(), // override quality
                    random ? null : material.getSizeTemplate().getSize(2), // override size
                    null,
                    random ? null : Collections.emptyList(), // override characteristics
                    random ? null : Collections.emptyList(), // override categories
                    new AlchemyItemStatus.VisibleFlags(true),
                    0,
                    0,
                    0,
                    0,
                    0
            )) != null;
        }
        return false;
    }

}
