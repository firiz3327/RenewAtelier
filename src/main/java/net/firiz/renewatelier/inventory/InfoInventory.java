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

import java.util.function.BiConsumer;

public class InfoInventory implements NonParamInventory {

    // 0 1 2 3 4 5 6 7 8
    private enum Item {
        BAG(1, Material.ARROW, 1, "錬金バッグを開く", "クリックで錬金バッグを開きます。", ((player, inventoryManager) -> inventoryManager.getInventory(BagInventory.class).open(player))),
        QUEST_BOOK(3, Material.WRITABLE_BOOK, "クエスト一覧", "クリックでクエストブックを開きます。", ((player, inventoryManager) -> QuestBook.openQuestBook(player))),
        SKILL_TREE(5, Material.ENCHANTED_BOOK, "スキルツリー", "クリックでスキルツリーを開きます。", (((player, inventoryManager) -> inventoryManager.getInventory(SkillTreeInventory.class).open(player)))),
        SETTING(7, Material.IRON_PICKAXE, "設定", "クリックで設定画面を開きます。", ((player, inventoryManager) -> inventoryManager.getInventory(SettingInventory.class).open(player)));

        final int index;
        final ItemStack itemStack;
        final BiConsumer<Player, InventoryManager> consumer;

        Item(int index, Material material, String name, String lore, BiConsumer<Player, InventoryManager> consumer) {
            this.index = index;
            this.consumer = consumer;
            this.itemStack = ItemUtils.unavailableItem(
                    material,
                    new Text(name, true).color(C.FLAT_GREEN1),
                    new Lore(lore, true).color(C.FLAT_SILVER1)
            );
        }

        Item(int index, Material material, int customModel, String name, String lore, BiConsumer<Player, InventoryManager> consumer) {
            this.index = index;
            this.consumer = consumer;
            this.itemStack = ItemUtils.unavailableItem(
                    material,
                    customModel,
                    new Text(name, true).color(C.FLAT_GREEN1),
                    new Lore(lore, true).color(C.FLAT_SILVER1)
            );
        }

    }

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
        for (final Item item : Item.values()) {
            inv.setItem(item.index, item.itemStack);
        }
        player.openInventory(inv);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();
        final int raw = event.getRawSlot();
        for (final Item item : Item.values()) {
            if (raw == item.index) {
                player.closeInventory();
                item.consumer.accept(player, manager);
                break;
            }
        }
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
