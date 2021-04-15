package net.firiz.renewatelier.inventory.item.json;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlchemyItemBag {

    private static final NamespacedKey persistentDataKey = CommonUtils.createKey("alchemyItemBag");
    private final ObjectList<JsonItem> items;

    public AlchemyItemBag() {
        this.items = new ObjectArrayList<>();
    }

    private AlchemyItemBag(@NotNull final List<ItemStack> items) {
        this();
        Objects.requireNonNull(items).stream().filter(AlchemyItemStatus::has).map(JsonItem::load).forEach(this.items::add);
    }

    public static ItemStack createBagItem() {
        final ItemStack bagItem = ItemUtils.createCustomModelItem(Material.ARROW, 1, 1);
        final ItemMeta meta = bagItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "錬金バッグ");
        final List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "錬金素材を格納できる");
        meta.setLore(lore);
        bagItem.setItemMeta(meta);
        new AlchemyItemBag().writeItem(bagItem);
        return bagItem;
    }

    public static boolean has(@Nullable final ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(
                persistentDataKey,
                PersistentDataType.STRING
        );
    }

    @NotNull
    public static AlchemyItemBag load(@NotNull final ItemStack item) {
        final AlchemyItemBag bag = new AlchemyItemBag();
        if (Objects.requireNonNull(item).hasItemMeta()) {
            final PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
            if (persistentDataContainer.has(persistentDataKey, PersistentDataType.STRING)) {
                bag.deserializeItems(Objects.requireNonNull(persistentDataContainer.get(
                        persistentDataKey,
                        PersistentDataType.STRING
                )));
            }
        }
        return bag;
    }

    public void writeItem(@NotNull final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        final String json = JsonItemList.toJson(items);
        meta.getPersistentDataContainer().set(
                persistentDataKey,
                PersistentDataType.STRING,
                json
        );
        item.setItemMeta(meta);
    }

    public ObjectList<JsonItem> getItems() {
        return items;
    }

    public ObjectList<ItemStack> refreshInventory(@NotNull final Inventory inv) {
        items.clear();
        final ObjectList<ItemStack> notApplicableItems = new ObjectArrayList<>();
        for (final ItemStack i : inv.getStorageContents()) {
            if (AlchemyItemStatus.has(i)) {
                this.items.add(JsonItem.load(i));
            } else if (i != null) {
                notApplicableItems.add(i);
            }
        }
        return notApplicableItems;
    }

    private void deserializeItems(@NotNull final String json) {
        items.addAll(JsonItemList.fromJson(json).getJsonItems());
    }
}
