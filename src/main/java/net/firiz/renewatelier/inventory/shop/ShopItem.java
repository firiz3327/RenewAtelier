package net.firiz.renewatelier.inventory.shop;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopItem {

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
        final List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        lore.add("");
        final String sb = Chore.createStridColor(coinType == null ? "$null" : coinType.getId()) +
                ChatColor.ITALIC + ChatColor.RESET + ChatColor.GREEN +
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

}
