package net.firiz.renewatelier.item.json;

import com.google.gson.annotations.Expose;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public final class JsonItem {

    @Expose
    private final Material material;
    @Expose
    private final int amount;
    @Expose
    private final int customModel;
    @Expose
    private final int durability;
    @Expose
    private final String jsonItemStatus;

    public static JsonItem load(ItemStack item) {
        final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(item);
        final boolean hasMeta = item.hasItemMeta();
        return new JsonItem(
                item.getType(),
                item.getAmount(),
                hasMeta && item.getItemMeta().hasCustomModelData() ? item.getItemMeta().getCustomModelData() : -1,
                hasMeta ? (item.getItemMeta() instanceof Damageable ? ((Damageable) item.getItemMeta()).getDamage() : 0) : 0,
                Objects.requireNonNull(itemStatus)
        );
    }

    private JsonItem(Material material, int amount, int customModel, int durability, AlchemyItemStatus itemStatus) {
        this.material = material;
        this.amount = amount;
        this.customModel = customModel;
        this.durability = durability;
        this.jsonItemStatus = itemStatus.toJson();
    }

    public ItemStack toItemStack() {
        final ItemStack result = new ItemStack(material, amount);
        final ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            if (customModel != -1) {
                meta.setCustomModelData(customModel);
            } else if (result.getItemMeta() instanceof Damageable) {
                ((Damageable) meta).setDamage(durability);
            }
            result.setItemMeta(meta);
        }
        AlchemyItemStatus.loadJson(jsonItemStatus).updateItem(result);
        return result;
    }

}
