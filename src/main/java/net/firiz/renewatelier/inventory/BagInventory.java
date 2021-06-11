package net.firiz.renewatelier.inventory;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.inventory.item.json.JsonItem;
import net.firiz.renewatelier.inventory.manager.NonParamInventory;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemBag;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BagInventory implements NonParamInventory {

    private static final Component TITLE = Component.text("錬金バッグ");

    @Override
    public void open(@NotNull Player player) {
        final Char character = PlayerSaveManager.INSTANCE.getChar(player);
        final List<JsonItem> items = character.getBag().getItems();
        final Inventory inv = Bukkit.createInventory(player, AlchemyItemBag.SIZE, TITLE);
        for (int i = 0, size = Math.min(inv.getSize(), items.size()); i < size; i++) {
            inv.setItem(i, items.get(i).toItemStack());
        }
        player.openInventory(inv);
    }

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.title().equals(TITLE);
    }

    public static boolean checkS(@NotNull InventoryView view) {
        return view.title().equals(TITLE);
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final Char character = PlayerSaveManager.INSTANCE.getChar(player);
        character.getBag()
                .refresh(event.getInventory().getStorageContents())
                .forEach(i -> ItemUtils.addItem(player, i));
    }
}
