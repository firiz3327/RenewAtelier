package net.firiz.renewatelier.inventory.item;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

public class PotionMaterial extends CustomModelMaterial {

    private final Type type;
    private final Color color;

    public PotionMaterial(@NotNull Type type, int customModel, Color color) {
        super(type.material, customModel);
        this.type = type;
        this.color = color;
    }

    @Override
    public ItemStack toItemStack(int amount) {
        final ItemStack item = super.toItemStack(amount);
        final PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        potionMeta.setColor(color);
        if (type.hide) {
            potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        }
        switch (type) {
            case GLOW:
            case HIDE_GLOW:
                potionMeta.addEnchant(Enchantment.LUCK, 1, true);
                potionMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                break;
            default:
                // ignored
                break;
        }
        item.setItemMeta(potionMeta);
        return item;
    }

    public enum Type {
        NORMAL(Material.POTION),
        LINGERING(Material.LINGERING_POTION),
        SPLASH(Material.SPLASH_POTION),
        GLOW(Material.POTION),
        HIDE_NORMAL(Material.POTION, true),
        HIDE_LINGERING(Material.LINGERING_POTION, true),
        HIDE_SPLASH(Material.SPLASH_POTION, true),
        HIDE_GLOW(Material.POTION, true);

        private final Material material;
        private final boolean hide;

        Type(Material material) {
            this.material = material;
            this.hide = false;
        }

        Type(Material material, boolean hide) {
            this.material = material;
            this.hide = hide;
        }
    }
}
