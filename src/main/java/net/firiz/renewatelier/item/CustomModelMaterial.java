package net.firiz.renewatelier.item;

import net.firiz.renewatelier.utils.Chore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomModelMaterial {

    private final Material material;
    private final int customModel;

    public CustomModelMaterial(Material material, int customModel) {
        this.material = material;
        this.customModel = customModel;
    }

    public Material getMaterial() {
        return material;
    }

    public int getCustomModel() {
        return customModel;
    }

    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    public ItemStack toItemStack(int amount) {
        if (customModel >= 0) {
            return Chore.createCustomModelItem(material, amount, customModel);
        }
        return new ItemStack(material, amount);
    }

}
