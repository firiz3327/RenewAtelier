package net.firiz.renewatelier.inventory.item.json.itemeffect;

import net.firiz.renewatelier.alchemy.recipe.StarEffect;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class EnchantInitialize implements ItemInitialize {

    private final StarEffect.EnchantEffect enchantEffect;

    public EnchantInitialize(StarEffect.EnchantEffect enchantEffect) {
        this.enchantEffect = enchantEffect;
    }

    @Override
    public void accept(ItemStack itemStack) {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(enchantEffect.enchant(), enchantEffect.level(), true);
        itemStack.setItemMeta(meta);
    }
}
