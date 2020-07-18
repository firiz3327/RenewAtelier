package net.firiz.renewatelier.inventory.shop;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.ItemUtils;
import net.firiz.renewatelier.utils.pair.Pair;
import org.bukkit.ChatColor;
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

    private static final NamespacedKey coinTypeKey = CommonUtils.createKey("coinType");
    private static final NamespacedKey priceKey = CommonUtils.createKey("price");

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
            dataContainer.set(coinTypeKey, PersistentDataType.STRING, coinType.getId());
        }
        final List<String> lore = meta.getLore() == null ? new ObjectArrayList<>() : meta.getLore();
        lore.add("");
        final String sb = ChatColor.GREEN +
                (coinType == null ? "価格" : coinType.getName()) +
                ChatColor.RESET +
                ": " +
                price +
                (coinType == null ? " エメラルド" : " 個");
        lore.add(sb);
        meta.setLore(lore);
        clone.setItemMeta(meta);

        return clone;
    }

    public static Pair<Integer, String> loadShopItem(@NotNull final ItemStack item) {
        return new Pair<>(
                item.getItemMeta().getPersistentDataContainer().get(coinTypeKey, PersistentDataType.INTEGER),
                item.getItemMeta().getPersistentDataContainer().get(coinTypeKey, PersistentDataType.STRING)
        );
    }

}
