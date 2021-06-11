package net.firiz.renewatelier.inventory.manager;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.firiz.renewatelier.alchemy.kettle.inventory.AlchemyKettleInventory;
import net.firiz.renewatelier.alchemy.kettle.inventory.CatalystSelectInventory;
import net.firiz.renewatelier.alchemy.kettle.inventory.ItemSelectInventory;
import net.firiz.renewatelier.alchemy.kettle.inventory.RecipeSelectInventory;
import net.firiz.renewatelier.inventory.*;
import net.firiz.renewatelier.inventory.shop.ShopInventory;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemBag;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.java.CObjects;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public enum InventoryManager {
    INSTANCE;

    private final Map<Class<?>, CustomInventory> inventories = new Object2ObjectLinkedOpenHashMap<>();

    InventoryManager() {
        inventories.put(Appraisal.class, new Appraisal());
        inventories.put(RecipeSelectInventory.class, new RecipeSelectInventory(this));
        inventories.put(ItemSelectInventory.class, new ItemSelectInventory(this));
        inventories.put(CatalystSelectInventory.class, new CatalystSelectInventory(this));
        inventories.put(AlchemyKettleInventory.class, new AlchemyKettleInventory());
        inventories.put(ConfirmInventory.class, new ConfirmInventory());
        inventories.put(ShopInventory.class, new ShopInventory());
        inventories.put(BagInventory.class, new BagInventory());
        inventories.put(MatingHorseInventory.class, new MatingHorseInventory());
        inventories.put(InfoInventory.class, new InfoInventory(this));
        inventories.put(SettingInventory.class, new SettingInventory(this));
        inventories.put(SkillTreeInventory.class, new SkillTreeInventory());
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
        return CommonUtils.cast(Objects.requireNonNull(inventories.get(clasz)));
    }

    public void onClick(@NotNull InventoryView view, @NotNull InventoryClickEvent event) {
        if (AlchemyItemBag.has(event.getCurrentItem())) {
            event.setCancelled(true);
            final Player player = (Player) event.getWhoClicked();

            // プレイヤーインベントリの時だけ
            if (event.getInventory() instanceof CraftingInventory && ((CraftingInventory) event.getInventory()).getMatrix().length == 4) {
                // バッグをシフトクリックした際、プレイヤーインベントリのクイックバーに
                // 空きスロットがあった場合、バッグが増殖するバグがある
                // 上記バグはクリエイティブ時限定
                switch (event.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        getInventory(BagInventory.class).open(player);
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        getInventory(InfoInventory.class).open(player);
                        break;
                    default:
                        break;
                }
            }
            return;
        }
        if (view.getTopInventory() instanceof AbstractHorseInventory) {
            event.setCancelled(true);
            return;
        }
        CObjects.nonNullConsumer(getInventory(view), inv -> inv.onClick(event));
    }

    public void onDrag(@NotNull InventoryView view, @NotNull InventoryDragEvent event) {
        if (view.getTopInventory() instanceof AbstractHorseInventory) {
            event.setCancelled(true);
            return;
        }
        final Optional<ItemStack> first = event.getNewItems().values().stream().filter(AlchemyItemBag::has).findFirst();
        if (first.isPresent()) {
            event.setCancelled(true);
            return;
        }
        CObjects.nonNullConsumer(getInventory(view), inv -> inv.onDrag(event));
    }

    public void onOpen(@NotNull InventoryView view, @NotNull InventoryOpenEvent event) {
        if (view.getTopInventory() instanceof AbstractHorseInventory) {
            event.setCancelled(true);
        }
    }

    public void onClose(@NotNull InventoryView view, @NotNull InventoryCloseEvent event) {
        CObjects.nonNullConsumer(getInventory(view), inv -> inv.onClose(event));
    }

}
