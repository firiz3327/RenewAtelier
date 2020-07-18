package net.firiz.renewatelier.inventory.manager;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.firiz.renewatelier.a.AAlchemyKettle;
import net.firiz.renewatelier.a.ACatalystSelect;
import net.firiz.renewatelier.a.AItemSelect;
import net.firiz.renewatelier.a.ARecipeSelect;
import net.firiz.renewatelier.inventory.Appraisal;
import net.firiz.renewatelier.inventory.BagInventory;
import net.firiz.renewatelier.inventory.ConfirmInventory;
import net.firiz.renewatelier.inventory.shop.ShopInventory;
import net.firiz.renewatelier.item.json.AlchemyItemBag;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.chores.CObjects;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InventoryManager {

    private final Map<Class<?>, CustomInventory> inventories = new Object2ObjectLinkedOpenHashMap<>();

    public InventoryManager() {
        inventories.put(Appraisal.class, new Appraisal());
        inventories.put(ARecipeSelect.class, new ARecipeSelect(this));
        inventories.put(AItemSelect.class, new AItemSelect(this));
        inventories.put(ACatalystSelect.class, new ACatalystSelect(this));
        inventories.put(AAlchemyKettle.class, new AAlchemyKettle());
        inventories.put(ConfirmInventory.class, new ConfirmInventory());
        inventories.put(ShopInventory.class, new ShopInventory());
        inventories.put(BagInventory.class, new BagInventory());
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

    public void onClick(@NotNull InventoryView view, @NotNull InventoryClickEvent event) {
        if (AlchemyItemBag.has(event.getCurrentItem())) {
            event.setCancelled(true);
            // プレイヤーインベントリの時だけ
            if (event.getInventory() instanceof CraftingInventory && ((CraftingInventory) event.getInventory()).getMatrix().length == 4) {
                getInventory(BagInventory.class).open((Player) view.getPlayer(), event.getCurrentItem());
            }
            return;
        }
        CObjects.nonNullConsumer(getInventory(view), inv -> inv.onClick(event));
    }

    public void onDrag(@NotNull InventoryView view, @NotNull InventoryDragEvent event) {
        final Optional<ItemStack> first = event.getNewItems().values().stream().filter(AlchemyItemBag::has).findFirst();
        if (first.isPresent()) {
            event.setCancelled(true);
            return;
        }
        CObjects.nonNullConsumer(getInventory(view), inv -> inv.onDrag(event));
    }

    public void onClose(@NotNull InventoryView view, @NotNull InventoryCloseEvent event) {
        CObjects.nonNullConsumer(getInventory(view), inv -> inv.onClose(event));
    }

    @NotNull
    public <T> T getInventory(@NotNull Class<T> clasz) {
        final Object obj = Objects.requireNonNull(inventories.get(clasz));
        return CommonUtils.cast(obj);
    }

}
