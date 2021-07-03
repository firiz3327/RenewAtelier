package net.firiz.renewatelier.quest;

import java.util.List;

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public record QuestItem(
        @NotNull AlchemyMaterial material,
        @Nullable Component name,
        int amount, int quality, int usecount,
        List<AlchemyIngredients> ingredients,
        List<Characteristic> characteristics
) {

    public QuestItem(@NotNull AlchemyMaterial material, @Nullable Component name, int amount, int quality, int usecount, List<AlchemyIngredients> ingredients, List<Characteristic> characteristics) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.quality = quality;
        this.usecount = usecount;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
    }

    public ItemStack getItem() {
        final ItemStack result = AlchemyItemStatus.getItem(
                material,
                ingredients,
                null,
                quality,
                null,
                characteristics,
                null,
                false
        );
        result.setAmount(amount);
        if (name != null) {
            final ItemMeta meta = result.getItemMeta();
            meta.displayName(name);
            result.setItemMeta(meta);
        }
        return result;
    }

    public ItemStack getItem(AlchemyItemStatus.VisibleFlags flags) {
        final ItemStack result = AlchemyItemStatus.getItem(
                material,
                ingredients,
                null,
                quality,
                null,
                null,
                characteristics,
                null,
                flags
        );
        result.setAmount(amount);
        if (name != null) {
            final ItemMeta meta = result.getItemMeta();
            meta.displayName(name);
            result.setItemMeta(meta);
        }
        return result;
    }

    @NotNull
    public AlchemyMaterial getMaterial() {
        return material;
    }

    @Nullable
    public Component getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getQuality() {
        return quality;
    }

    public int getUsecount() {
        return usecount;
    }

    public List<AlchemyIngredients> getIngredients() {
        return ingredients;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

}
