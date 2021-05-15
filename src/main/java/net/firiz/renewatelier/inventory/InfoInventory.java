package net.firiz.renewatelier.inventory;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.inventory.manager.NonParamInventory;
import net.firiz.renewatelier.quest.book.QuestBook;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InfoInventory implements NonParamInventory {

    private static final ItemStack bag = ItemUtils.unavailableItem(
            Material.ARROW,
            1,
            new Text("錬金バッグを開く", true).color(C.FLAT_GREEN1),
            new Lore("クリックで錬金バッグを開きます。", true).color(C.FLAT_SILVER1)
    );
    private static final ItemStack questBook = ItemUtils.unavailableItem(
            Material.WRITABLE_BOOK,
            new Text("クエスト一覧", true).color(C.FLAT_GREEN1),
            new Lore("クリックでクエストブックを開きます。", true).color(C.FLAT_SILVER1)
    );
    private static final ItemStack infoItem = ItemUtils.unavailableItem(
            Material.IRON_PICKAXE,
            new Text("設定", true).color(C.FLAT_GREEN1),
            new Lore("クリックで設定画面を開きます。", true).color(C.FLAT_SILVER1)
    );
    private final Component title = Component.text("Info");
    private final InventoryManager manager;

    public InfoInventory(InventoryManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.title().equals(title);
    }

    @Override
    public void open(@NotNull Player player) {
        final Inventory inv = Bukkit.createInventory(player, 9, title);
        // 0 1 x 3 x 5 x 7 8
        inv.setItem(2, bag);
        inv.setItem(4, questBook);
        inv.setItem(6, infoItem);
        player.openInventory(inv);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();
        switch (event.getRawSlot()) {
            case 2:
                player.closeInventory();
                manager.getInventory(BagInventory.class).open(player);
                break;
            case 4:
                player.closeInventory();
                QuestBook.openQuestBook(player);
                break;
            case 6:
                player.closeInventory();
                manager.getInventory(SettingInventory.class).open(player);
                break;
            default: // ignored
                break;
        }
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
