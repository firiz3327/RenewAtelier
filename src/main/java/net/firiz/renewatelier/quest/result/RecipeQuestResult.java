package net.firiz.renewatelier.quest.result;

import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author firiz
 */
public class RecipeQuestResult extends ObjectQuestResult<AlchemyRecipe> {

    public RecipeQuestResult(AlchemyRecipe recipe) {
        super(recipe);
    }

    @Override
    public void appendQuestResult(Player player, ComponentBuilder builder) {
        final AlchemyRecipe recipe = getResult();
        final String result_str = recipe.getResult();
        builder.append("レシピ: ");

        final List<ItemFlag> flags = new ArrayList<>();
        String name;
        Material material;
        final int cmd;
        if (result_str.startsWith("material:")) {
            final AlchemyMaterial am = AlchemyMaterial.getMaterial(result_str.substring(9));
            name = am.getName();
            material = am.getMaterial().getMaterial();
            cmd = am.getMaterial().getCustomModel();
            if (am.isHideAttribute()) {
                flags.add(ItemFlag.HIDE_ATTRIBUTES);
            }
            if (am.isHideDestroy()) {
                flags.add(ItemFlag.HIDE_DESTROYS);
            }
            if (am.isHideEnchant()) {
                flags.add(ItemFlag.HIDE_ENCHANTS);
            }
            if (am.isHidePlacedOn()) {
                flags.add(ItemFlag.HIDE_PLACED_ON);
            }
            if (am.isHidePotionEffect()) {
                flags.add(ItemFlag.HIDE_POTION_EFFECTS);
            }
            if (am.isHideUnbreaking()) {
                flags.add(ItemFlag.HIDE_UNBREAKABLE);
            }
        } else if (result_str.startsWith("minecraft:")) { // 基本想定しない
            material = Material.matchMaterial(result_str);
            if (material == null) {
                material = Material.matchMaterial(result_str, true);
            }
            name = null;
            cmd = 0;
        } else {
            throw new IllegalStateException("not support result_str");
        }
        final ItemStack viewItem = Chore.createCustomModelItem(material, 1, cmd);
        final ItemMeta viewMeta = Objects.requireNonNull(viewItem.getItemMeta());
        if (name != null) {
            viewMeta.setDisplayName(name);
        } else {
            name = LanguageItemUtil.getLocalizeName(viewItem, player);
        }
        if (!flags.isEmpty()) {
            viewMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }
        final List<String> viewLore = new ArrayList<>();
        viewLore.add(ChatColor.GRAY + "作成量: " + ChatColor.RESET + recipe.getAmount());
        viewLore.add(ChatColor.GRAY + "必要素材:");
        for (final RequireAmountMaterial req : recipe.getReqMaterial()) {
            switch (req.getType()) {
                case CATEGORY:
                    viewLore.add(ChatColor.RESET + "- " + ChatColor.stripColor(req.getCategory().getName()) + " × " + req.getAmount());
                    break;
                case MATERIAL:
                    viewLore.add(ChatColor.RESET + "- " + ChatColor.stripColor(req.getMaterial().getName()) + " × " + req.getAmount());
                    break;
                default: // 想定しない
                    break;
            }
        }
        viewMeta.setLore(viewLore);
        viewItem.setItemMeta(viewMeta);
        builder.append(name).event(
                TellrawUtils.createHoverEvent(viewItem)
        );
    }

}
