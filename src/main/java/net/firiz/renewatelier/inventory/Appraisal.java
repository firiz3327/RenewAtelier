package net.firiz.renewatelier.inventory;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.inventory.manager.NonParamInventory;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class Appraisal implements NonParamInventory {

    private static final Component TITLE = Component.text("鑑定");

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.title().equals(TITLE);
    }

    @Override
    public void open(@NotNull Player player) {
        final Inventory inv = Bukkit.createInventory(player, 18, TITLE);
        for (int i = 9; i < 18; i++) {
            if (i == 13) {
                inv.setItem(i, ItemUtils.ci(Material.ENCHANTED_BOOK, 0, Text.itemName("鑑定", C.GREEN), null));
            } else {
                inv.setItem(i, ItemUtils.ci(Material.GRAY_STAINED_GLASS_PANE, 0, "", null));
            }
        }
        player.openInventory(inv);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final int raw = e.getRawSlot();
        if (raw >= 9 && raw < inv.getSize()) {
            e.setCancelled(true);
            if (raw == 13) {
                final Player player = (Player) e.getWhoClicked();
                final Char character = PlayerSaveManager.INSTANCE.getChar(player);
                for (int i = 0; i < 9; i++) {
                    final ItemStack item = inv.getItem(i);
                    if (item != null) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        ReplaceVanillaItems.changeItems(true, alchemyItem -> {
                            character.increaseIdea(alchemyItem);
                            return alchemyItem;
                        }, item);
                    }
                    inv.setItem(i, null);
                    ItemUtils.addItem(player, item);
                }
            }
        }
    }
}
