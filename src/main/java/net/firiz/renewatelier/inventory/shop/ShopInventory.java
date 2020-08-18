package net.firiz.renewatelier.inventory.shop;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.inventory.manager.BiParamInventory;
import net.firiz.renewatelier.utils.chores.ItemUtils;
import net.firiz.renewatelier.utils.pair.ImmutableNullablePair;
import net.firiz.renewatelier.version.packet.InventoryPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ShopInventory implements BiParamInventory<String, List<ShopItem>> {

    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private static final String TITLE = "SHOP";
    private static final ItemStack GLASS_PANE = ItemUtils.ci(Material.GRAY_STAINED_GLASS_PANE, 0, "", null);

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().endsWith(TITLE);
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final String title, @NotNull final List<ShopItem> shopItems) {
        final UUID uuid = player.getUniqueId();
        final Inventory inv = Bukkit.createInventory(null, 54, uuid.toString().concat(TITLE));
        for (int slot = 0; slot < 54; slot++) {
            if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8) {
                inv.setItem(slot, GLASS_PANE);
            }
        }
        for (int i = 0; i < shopItems.size() && i < 28; i++) {
            final ShopItem shopItem = shopItems.get(i);
            inv.addItem(shopItem.create());
        }
        player.openInventory(inv);
        InventoryPacket.update(
                player,
                ChatColor.translateAlternateColorCodes('&', title),
                InventoryPacket.InventoryPacketType.CHEST
        );
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final Inventory inv = e.getInventory();
        final int raw = e.getRawSlot();
        if (raw >= 0 && raw < inv.getSize()) {
            e.setCancelled(true);
            if (!player.hasCooldown(Material.GRAY_STAINED_GLASS_PANE)) {
                final ItemStack item = inv.getItem(e.getRawSlot());
                if (item != null && !GLASS_PANE.isSimilar(item)) {
                    final ShopResult priceMode = createShopResult(item, player);
                    if (priceMode.mode != -1) {
                        player.setCooldown(Material.GRAY_STAINED_GLASS_PANE, 10);
                        reduceItem(priceMode, player);
                        addItem(item.clone(), player);
                    }
                }
            }
        }
    }

    /**
     * 決済を処理するモードを返す
     *
     * @param item   アイテム
     * @param player 購入者
     * @return ShopResult
     */
    @NotNull
    private ShopResult createShopResult(@NotNull ItemStack item, @NotNull Player player) {
        final ImmutableNullablePair<Integer, String> shopItem = ShopItem.loadShopItem(item);
        final int price = Objects.requireNonNull(shopItem.getLeft());
        if (shopItem.getRight() == null) {
            final int mode = psm.getChar(player.getUniqueId()).hasMoney(price) ? 1 : -1;
            return new ShopResult(mode, price, null);
        } else {
            final AlchemyMaterial alchemyMaterial = AlchemyMaterial.getMaterial(shopItem.getRight());
            final int mode = ItemUtils.hasMaterial(player.getInventory(), alchemyMaterial, price) ? 2 : -1;
            return new ShopResult(mode, price, alchemyMaterial);
        }
    }

    private void reduceItem(ShopResult priceMode, Player player) {
        if (priceMode.mode == 1) {
            psm.getChar(player.getUniqueId()).gainMoney(-priceMode.money);
        } else if (priceMode.material != null) {
            ItemUtils.gainItem(player.getInventory(), priceMode.material, priceMode.money);
        }
    }

    private void addItem(ItemStack item, Player player) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> cLore = Objects.requireNonNull(meta.getLore());
        for (int i = 0; i < 2; i++) {
            cLore.remove(cLore.size() - 1);
        }
        meta.setLore(cLore);
        item.setItemMeta(meta);
        ItemUtils.addItem(player, item);
    }

    @Override
    public void onDrag(@NotNull final InventoryDragEvent e) {
        final Inventory inv = e.getInventory();
        e.getRawSlots().stream()
                .filter(raw -> (raw >= 0 && raw < inv.getSize()))
                .forEach(itemValue -> e.setCancelled(true));
    }

    private static class ShopResult {
        private final int mode;
        private final int money;
        @Nullable
        private final AlchemyMaterial material;

        private ShopResult(int mode, int money, @Nullable AlchemyMaterial material) {
            this.mode = mode;
            this.money = money;
            this.material = material;
        }
    }
}
