package net.firiz.renewatelier.inventory.shop;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.utils.Chore;
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

import java.util.List;
import java.util.UUID;

public final class ShopInventory {

    private static final String TITLE = "SHOP";
    private static final ItemStack EMERALD = new ItemStack(Material.EMERALD);
    private static final ItemStack GLASS_PANE = Chore.ci(Material.GRAY_STAINED_GLASS_PANE, 0, "", null);

    private ShopInventory() {
    }

    public static boolean isShopInventory(final InventoryView view) {
        return view.getTitle().endsWith(TITLE);
    }

    public static void openInventory(final Player player, final String title, final List<ShopItem> shopItems) {
        final UUID uuid = player.getUniqueId();
        final Inventory inv = Bukkit.createInventory(null, 54, uuid.toString().concat(TITLE));
        for (int i = 0; i < 54; i++) {
            if (i < 9) {
                inv.setItem(i, GLASS_PANE);
            } else if (i >= 45) {
                inv.setItem(i, GLASS_PANE);
            } else {
                inv.setItem(i, GLASS_PANE);
                i += 8;
                inv.setItem(i, GLASS_PANE);
            }
        }
        for (int i = 0; i < shopItems.size() && i < 28; i++) {
            final ShopItem shopItem = shopItems.get(i);
            inv.addItem(shopItem.create());
        }
        player.openInventory(inv);
        InventoryPacket.update(player, title, InventoryPacket.InventoryPacketType.CHEST);
    }

    public static void click(final InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final Inventory inv = e.getInventory();
        final int raw = e.getRawSlot();
        if (raw >= 0 && raw < inv.getSize()) {
            e.setCancelled(true);
            if (player.hasCooldown(Material.GRAY_STAINED_GLASS_PANE)) {
                return;
            }
            final ItemStack item = inv.getItem(e.getRawSlot());
            if (item != null && !GLASS_PANE.isSimilar(item)) {
                final List<String> lore = item.getItemMeta().getLore();
                final String str = lore.get(lore.size() - 1);
                final String id = Chore.getStridColor(str.substring(0, str.indexOf(
                        new StringBuilder()
                                .append(ChatColor.ITALIC)
                                .append(ChatColor.RESET)
                                .append(ChatColor.GREEN)
                                .toString())
                ));
                final String v = str.substring(str.lastIndexOf(": ") + 2);
                final int price = Integer.parseInt(v.substring(0, v.indexOf(" ")));
                final AlchemyMaterial am = AlchemyMaterial.getMaterial(id);
                final int check = id.equals("$null")
                        ? Chore.hasMaterial(player.getInventory(), EMERALD, price) ? 1 : -1
                        : Chore.hasMaterial(player.getInventory(), am, price) ? 2 : -1;
                if (check != -1) {
                    player.setCooldown(Material.GRAY_STAINED_GLASS_PANE, 10);
                    if (check == 1) {
                        Chore.reduceItem(player.getInventory(), EMERALD, price);
                    } else {
                        Chore.reduceItem(player.getInventory(), am, price);
                    }
                    final ItemStack clone = item.clone();
                    final ItemMeta meta = clone.getItemMeta();
                    final List<String> clore = meta.getLore();
                    for (int i = 0; i < 2; i++) {
                        clore.remove(clore.size() - 1);
                    }
                    meta.setLore(clore);
                    clone.setItemMeta(meta);
                    Chore.addItem(player, clone);
                }
            }
        }
    }

    public static void drag(final InventoryDragEvent e) {
        final Inventory inv = e.getInventory();
        e.getRawSlots().stream()
                .filter((raw) -> (raw >= 0 && raw < inv.getSize()))
                .forEach((_item) -> e.setCancelled(true));
    }
}
