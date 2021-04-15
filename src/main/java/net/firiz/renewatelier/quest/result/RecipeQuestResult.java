package net.firiz.renewatelier.quest.result;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.result.ARecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.AlchemyMaterialRecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.MinecraftMaterialRecipeResult;
import net.firiz.renewatelier.inventory.item.CustomModelMaterial;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

/**
 * @author firiz
 */
public class RecipeQuestResult extends ObjectQuestResult<AlchemyRecipe> {

    public RecipeQuestResult(AlchemyRecipe recipe) {
        super(recipe);
    }

    @Override
    public void appendQuestResult(Player player, TextComponent.Builder builder) {
        final AlchemyRecipe recipe = getResult();
        final ARecipeResult<?> resultData = recipe.getResult();
        builder.append(Component.text("レシピ: "));

        final List<ItemFlag> flags = new ObjectArrayList<>();
        String name;
        CustomModelMaterial material;
        if (resultData instanceof AlchemyMaterialRecipeResult) {
            final AlchemyMaterial alchemyMaterial = ((AlchemyMaterialRecipeResult) resultData).getResult();
            name = alchemyMaterial.getName();
            material = alchemyMaterial.getMaterial();
            if (alchemyMaterial.isHideAttribute()) {
                flags.add(ItemFlag.HIDE_ATTRIBUTES);
            }
            if (alchemyMaterial.isHideDestroy()) {
                flags.add(ItemFlag.HIDE_DESTROYS);
            }
            if (alchemyMaterial.isHideEnchant()) {
                flags.add(ItemFlag.HIDE_ENCHANTS);
            }
            if (alchemyMaterial.isHidePlacedOn()) {
                flags.add(ItemFlag.HIDE_PLACED_ON);
            }
            if (alchemyMaterial.isHidePotionEffect()) {
                flags.add(ItemFlag.HIDE_POTION_EFFECTS);
            }
            if (alchemyMaterial.isHideUnbreaking()) {
                flags.add(ItemFlag.HIDE_UNBREAKABLE);
            }
        } else if (resultData instanceof MinecraftMaterialRecipeResult) { // 基本想定しない
            material = resultData.getCustomModelMaterial();
            name = null;
        } else {
            throw new IllegalStateException("not support result_str");
        }
        final ItemStack viewItem = material.toItemStack();
        final ItemMeta viewMeta = Objects.requireNonNull(viewItem.getItemMeta());
        final Component nameComponent;
        if (name != null) {
            nameComponent = Component.text(name);
            viewMeta.displayName(nameComponent);
        } else {
            name = LanguageItemUtil.getLocalizeName(viewItem, player);
            nameComponent = Component.text(name);
        }
        if (!flags.isEmpty()) {
            viewMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }
        final List<Component> viewLore = new ObjectArrayList<>();
        viewLore.add(Component.text("作成量: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY).append(Component.text(recipe.getAmount()).color(NamedTextColor.WHITE)));
        viewLore.add(Component.text("必要素材:").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
        for (final RequireAmountMaterial req : recipe.getReqMaterial()) {
            switch (req.getType()) {
                case CATEGORY:
                    viewLore.add(
                            Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                    .append(Component.text(req.getCategory().getName() + " × " + req.getAmount()))
                    );
//                    viewLore.add(ChatColor.WHITE + "- " + ChatColor.stripColor(req.getCategory().getName()) + " × " + req.getAmount());
                    break;
                case MATERIAL:
                    viewLore.add(
                            Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                    .append(Component.text(req.getMaterial().getName() + " × " + req.getAmount()))
                    );
//                    viewLore.add(ChatColor.WHITE + "- " + ChatColor.stripColor(req.getMaterial().getName()) + " × " + req.getAmount());
                    break;
                default: // 想定しない
                    break;
            }
        }
//        viewMeta.setLore(viewLore);
        viewMeta.lore(viewLore);
        viewItem.setItemMeta(viewMeta);
        builder.append(nameComponent.hoverEvent(viewItem.asHoverEvent()));
    }

}
