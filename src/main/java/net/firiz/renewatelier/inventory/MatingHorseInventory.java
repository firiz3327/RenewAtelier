package net.firiz.renewatelier.inventory;

import net.firiz.renewatelier.inventory.manager.NonParamInventory;
import net.firiz.renewatelier.item.json.HorseSaddle;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MatingHorseInventory implements NonParamInventory {

    private static final String TITLE = "馬の交配";
    private static final ItemStack PANEL_ITEM = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    private static final int INV_SIZE = 27;
    private static final int MALE_SLOT = 13;
    private static final int FEMALE_SLOT = 17;

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.getTitle().equals(TITLE);
    }

    @Override
    public void open(@NotNull Player player) {
        // 0 1 2 3 4 5 6 7 8 9 0
        // 0 1 2 x 4 5 6 x 8 9 1
        // 0 1 2 3 4 5 6 7 8 9 2
        final Inventory inv = Bukkit.createInventory(null, INV_SIZE, TITLE);
        for (int i = 0; i < INV_SIZE; i++) {
            if (i != MALE_SLOT && i != FEMALE_SLOT) {
                inv.setItem(i, PANEL_ITEM.clone());
            }
        }
        player.openInventory(inv);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        final int raw = event.getRawSlot();
        if (raw >= 0 && raw < INV_SIZE) {
            event.setCancelled(true);
            final ItemStack cursor = event.getCursor();
            if (raw == MALE_SLOT) {
                nonCancelClick(event, cursor, false);
            } else if (raw == FEMALE_SLOT) {
                nonCancelClick(event, cursor, true);
            }
        }
    }

    private void nonCancelClick(InventoryClickEvent event, ItemStack cursor, boolean female) {
        if (HorseSaddle.has(cursor)) {
            final HorseSaddle saddle = HorseSaddle.load(cursor);
            if (female == saddle.isFemale() && saddle.getLevel() >= 5) {
                event.setCancelled(false);
            }
        } else if (cursor == null) {
            event.setCancelled(false);
        }
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.getRawSlots().stream()
                .filter(raw -> (raw >= 0 && raw < INV_SIZE))
                .forEach(itemValue -> event.setCancelled(true));
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        final Inventory inv = event.getInventory();
        ItemUtils.addItem(event.getPlayer(), inv.getItem(MALE_SLOT));
        ItemUtils.addItem(event.getPlayer(), inv.getItem(FEMALE_SLOT));
    }
}
