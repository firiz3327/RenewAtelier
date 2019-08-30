package net.firiz.renewatelier.inventory.shop;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {

    private final ItemStack item;
    private final int amount;
    private final int price;
    private final AlchemyMaterial coinType;

    public ShopItem(ItemStack item, int amount, int price, AlchemyMaterial coinType) {
        this.item = item;
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
        final StringBuilder sb = new StringBuilder();
        sb.append(Chore.createStridColor(coinType == null ? "$null" : coinType.getId()))
                .append(ChatColor.ITALIC).append(ChatColor.RESET).append(ChatColor.GREEN)
                .append(coinType == null ? "価格" : coinType.getName() + ChatColor.RESET)
                .append(": ")
                .append(price)
                .append(coinType == null ? " エメラルド" : " 個");
        lore.add(sb.toString());
        meta.setLore(lore);
        clone.setItemMeta(meta);

        return clone;
    }

}
