package net.firiz.renewatelier.inventory.item.json;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.inventory.BagInventory;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlchemyItemBag {

    public static final int SIZE = 54;
    private static final NamespacedKey persistentDataKey = CommonUtils.createKey("alchemyItemBag");
    private static final ItemStack bag = createBagItem();
    private final ObjectList<JsonItem> items;

    public AlchemyItemBag() {
        this.items = new ObjectArrayList<>();
    }

    public AlchemyItemBag(@NotNull final String json) {
        this();
        deserializeItems(json);
    }

    public static ItemStack bag() {
        return bag.clone();
    }

    private static ItemStack createBagItem() {
        final ItemStack bagItem = ItemUtils.createCustomModelItem(Material.ARROW, 1, 1);
        final ItemMeta meta = bagItem.getItemMeta();
        meta.displayName(new Text("錬金バッグ", true).color(C.FLAT_GREEN1));
        final Lore lore = new Lore(true);
        lore.add("錬金素材を格納できる").color(C.GRAY);
        lore.nextLine();
        lore.add("左クリック - 錬金バッグを開く").color(C.FLAT_SILVER1);
        lore.add("右クリック - 詳細画面を開く").color(C.FLAT_SILVER1);
        lore.nextLine();
        meta.lore(lore);
        final PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(persistentDataKey, PersistentDataType.BYTE, (byte) 0);
        bagItem.setItemMeta(meta);
        return bagItem;
    }

    public static boolean has(@Nullable final ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(
                persistentDataKey,
                PersistentDataType.BYTE
        );
    }

    public void save(int id) {
        SQLManager.INSTANCE.insert(
                "bagitems",
                new String[]{"userId", "json"},
                new Object[]{id, serializeItems()}
        );
    }

    public boolean add(@NotNull final Player player, @NotNull final Item item) {
        final ItemStack itemStack = item.getItemStack();
        if (AlchemyItemStatus.has(itemStack)) {
            final InventoryView view = player.getOpenInventory();
            if (BagInventory.checkS(view)) {
                player.closeInventory();
            }
            final ItemStack t = addItem(itemStack);
            item.remove();
            if (t != null) {
                ItemUtils.addItem(player, t);
            }
            return true;
        }
        return false;
    }

    public void add(@NotNull final Player player, @NotNull final ItemStack item) {
        if (AlchemyItemStatus.has(item)) {
            final InventoryView view = player.getOpenInventory();
            if (BagInventory.checkS(view)) {
                player.closeInventory();
            }
            ItemUtils.addItem(player, addItem(item));
        }
    }

    private ItemStack addItem(@NotNull final ItemStack item) {
        final JsonItem jsonItem = JsonItem.load(item);
        int amount = item.getAmount();
        for (final JsonItem v : this.items) {
            amount = amount(jsonItem, amount, v);
        }
        if (amount > 0) {
            if (items.size() < SIZE) {
                items.add(jsonItem);
            } else {
                final ItemStack remainder = item.clone();
                remainder.setAmount(amount);
                return remainder;
            }
        }
        return null;
    }

    private int amount(JsonItem item, int amount, JsonItem v) {
        if (v != null) {
            final int maxStack = v.getMaterial().getMaxStackSize();
            if (v.isSimulator(item) && v.getAmount() < maxStack) {
                final int da = v.getAmount() + amount;
                amount = da - maxStack;
                v.setAmount(Math.min(maxStack, da));
            }
        }
        return amount;
    }

    public ObjectList<JsonItem> getItems() {
        return new ObjectArrayList<>(items);
    }

    public String serializeItems() {
        return JsonItemList.toJson(items);
    }

    private void deserializeItems(@NotNull final String json) {
        items.addAll(JsonItemList.fromJson(json).getJsonItems());
    }

    public ObjectList<ItemStack> refresh(@NotNull final ItemStack[] contents) {
        final ObjectList<JsonItem> jsonItems = new ObjectArrayList<>();
        final ObjectList<ItemStack> excludedItems = new ObjectArrayList<>();
        for (final ItemStack i : contents) {
            if (AlchemyItemStatus.has(i)) {
                jsonItems.add(JsonItem.load(i));
            } else if (i != null) {
                excludedItems.add(i);
            }
        }
        items.clear();
        items.addAll(jsonItems);
        return excludedItems;
    }
}
