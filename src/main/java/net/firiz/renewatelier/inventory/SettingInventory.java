package net.firiz.renewatelier.inventory;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.entity.player.CharSettings;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.inventory.manager.NonParamInventory;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SettingInventory implements NonParamInventory {

    private final Text title = new Text("設定");
    private final ItemStack backItem = ItemUtils.unavailableItem(Material.IRON_PICKAXE, new Text("戻る", true));
    private final InventoryManager manager;

    public SettingInventory(InventoryManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.title().equals(title);
    }

    @Override
    public void open(@NotNull Player player) {
        final Inventory inv = Bukkit.createInventory(player, 9, title);
        final CharSettings settings = PlayerSaveManager.INSTANCE.getChar(player).getSettings();
        inv.setItem(0, backItem.clone());
        inv.setItem(2, lantern(0, settings.isShowDamage()));
        inv.setItem(3, lantern(1, settings.isShowOthersDamage()));
        inv.setItem(4, lantern(2, settings.isShowPlayerChat()));
        player.openInventory(inv);
    }

    private ItemStack lantern(int i, boolean on) {
        final String msg;
        switch (i) {
            case 0:
                msg = "ダメージ描画は";
                break;
            case 1:
                msg = "他プレイヤーのダメージ描画は";
                break;
            case 2:
                msg = "他プレイヤーのチャットは";
                break;
            default: // ignored
                throw new IllegalArgumentException("illegal index.");
        }
        return ItemUtils.unavailableItem(
                on ? Material.LANTERN : Material.SOUL_LANTERN,
                new Text(msg, true).color(C.FLAT_SILVER1)
                        .append(on ? " 表示" : " 非表示").color(C.FLAT_GREEN1)
                        .append(" 状態です").color(C.FLAT_SILVER1),
                new Lore("クリックで", true).color(C.FLAT_SILVER1)
                        .append(on ? " 非表示" : " 表示").color(C.YELLOW)
                        .append(" に切り替えます。").color(C.FLAT_SILVER1)
        );
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();
        final CharSettings settings = PlayerSaveManager.INSTANCE.getChar(player).getSettings();
        switch (event.getRawSlot()) {
            case 0:
                manager.getInventory(InfoInventory.class).open(player);
                break;
            case 2:
                final boolean next0 = !settings.isShowDamage();
                settings.setShowDamage(next0);
                event.setCurrentItem(lantern(0, next0));
                break;
            case 3:
                final boolean next1 = !settings.isShowOthersDamage();
                settings.setShowOthersDamage(next1);
                event.setCurrentItem(lantern(1, next1));
                break;
            case 4:
                final boolean next2 = !settings.isShowPlayerChat();
                settings.setShowPlayerChat(next2);
                event.setCurrentItem(lantern(2, next2));
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
