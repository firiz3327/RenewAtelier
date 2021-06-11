package net.firiz.renewatelier.inventory.shop;

import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.utils.CommonUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ShopItem {

    private static final NamespacedKey priceKey = CommonUtils.createKey("price");
    private static final NamespacedKey coinTypeKey = CommonUtils.createKey("coinType");

    @NotNull
    private final ItemStack item;
    private final int amount;
    private final int price;
    @Nullable
    private final AlchemyMaterial coinType;

    public ShopItem(@NotNull ItemStack item, int amount, int price, @Nullable AlchemyMaterial coinType) {
        this.item = Objects.requireNonNull(item);
        this.amount = amount;
        this.price = price;
        this.coinType = coinType;
    }

    public ItemStack create() {
        final ItemStack clone = item.clone();
        clone.setAmount(amount);

        final ItemMeta meta = clone.getItemMeta();
        final PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(priceKey, PersistentDataType.INTEGER, price);
        if (coinType != null) {
            dataContainer.set(coinTypeKey, PersistentDataType.STRING, coinType.id());
        }
        final List<Component> lore = meta.lore() == null ? new ObjectArrayList<>() : meta.lore();
        assert lore != null;
        lore.add(Component.empty());
        lore.add(
                new Text().append((coinType == null ? Component.text("価格") : coinType.getName()).color(C.GREEN))
                        .append(": " + price + (coinType == null ? " $" : " 個")).color(C.WHITE)
        );
        meta.lore(lore);
        clone.setItemMeta(meta);

        return clone;
    }

    public static IntObjectImmutablePair<String> loadShopItem(@NotNull final ItemStack item) {
        final PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        if (dataContainer.has(priceKey, PersistentDataType.INTEGER) && dataContainer.has(coinTypeKey, PersistentDataType.STRING)) {
            final int value = Objects.requireNonNull(dataContainer.get(priceKey, PersistentDataType.INTEGER));
            return new IntObjectImmutablePair<>(value, dataContainer.get(coinTypeKey, PersistentDataType.STRING));
        }
        throw new IllegalArgumentException("item not has shop item key.");
    }

}
