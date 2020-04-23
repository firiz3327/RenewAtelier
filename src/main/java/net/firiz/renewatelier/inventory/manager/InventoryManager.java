package net.firiz.renewatelier.inventory.manager;

import net.firiz.renewatelier.inventory.Appraisal;
import net.firiz.renewatelier.inventory.ConfirmInventory;
import net.firiz.renewatelier.inventory.alchemykettle.AlchemyKettle;
import net.firiz.renewatelier.inventory.alchemykettle.CatalystSelect;
import net.firiz.renewatelier.inventory.alchemykettle.ItemSelect;
import net.firiz.renewatelier.inventory.alchemykettle.RecipeSelect;
import net.firiz.renewatelier.inventory.shop.ShopInventory;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class InventoryManager {

    private final Map<Class<?>, CustomInventory> inventories = new LinkedHashMap<>();

    public InventoryManager() {
        inventories.put(Appraisal.class, new Appraisal());
        inventories.put(RecipeSelect.class, new RecipeSelect(this));
        inventories.put(ItemSelect.class, new ItemSelect(this));
        inventories.put(CatalystSelect.class, new CatalystSelect(this));
        inventories.put(AlchemyKettle.class, new AlchemyKettle());
        inventories.put(ConfirmInventory.class, new ConfirmInventory());
        inventories.put(ShopInventory.class, new ShopInventory());
    }

    @Nullable
    public CustomInventory getInventory(@NotNull InventoryView view) {
        for (final CustomInventory inventory : inventories.values()) {
            if (inventory.check(view)) {
                return inventory;
            }
        }
        return null;
    }

    @NotNull
    public <T> T getInventory(@NotNull Class<T> clasz) {
        final Object obj = Objects.requireNonNull(inventories.get(clasz));
        return Chore.cast(obj);
    }

}
