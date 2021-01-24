package net.firiz.renewatelier.inventory;

import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.horse.HorseManager;
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
import org.jetbrains.annotations.Nullable;

public class MatingHorseInventory implements NonParamInventory {

    private static final String TITLE = "馬の交配";
    private static final ItemStack PANEL_ITEM = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private static final ItemStack CHECK_ITEM = ItemUtils.createCustomModelItem(Material.BARRIER, 1, 1);
    private static final ItemStack CROSS_ITEM = ItemUtils.createCustomModelItem(Material.BARRIER, 1, 2);

    private static final int INV_SIZE = 36;
    private static final int MALE_SLOT = 11;
    private static final int FEMALE_SLOT = 15;
    private static final int RESULT_SLOT = 22;

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.getTitle().equals(TITLE);
    }

    @Override
    public void open(@NotNull Player player) {
        // 0 1 2 3 4 5 6 7 8 - 0
        // 9 0 x 2 3 4 x 6 7 - 1
        // 8 9 0 1 x 3 4 5 6 - 2
        // 7 8 9 0 1 2 3 4 5 - 3
        final Inventory inv = Bukkit.createInventory(null, INV_SIZE, TITLE);
        for (int i = 0; i < INV_SIZE; i++) {
            if (i == RESULT_SLOT) {
                inv.setItem(RESULT_SLOT, CROSS_ITEM.clone());
            } else if (i != MALE_SLOT && i != FEMALE_SLOT) {
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
        }
        final InventoryView view = event.getView();
        final Player player = (Player) view.getPlayer();
        if (raw == MALE_SLOT) {
            clickSexSlot(player, view, event.getCursor(), event.getCurrentItem(), false);
        } else if (raw == FEMALE_SLOT) {
            clickSexSlot(player, view, event.getCursor(), event.getCurrentItem(), true);
        } else if (raw == RESULT_SLOT) {
            clickResultSlot(view, player);
        } else if (event.isShiftClick() && raw >= INV_SIZE) {
            event.setCancelled(true);
            shiftClickSaddle(player, raw, view, event.getCurrentItem());
        }
    }

    private void shiftClickSaddle(Player player, int raw, InventoryView view, @Nullable ItemStack current) {
        if (HorseSaddle.has(current)) {
            final HorseSaddle saddle = HorseSaddle.load(current);
            final int slot = saddle.isFemale() ? FEMALE_SLOT : MALE_SLOT;
            if (view.getItem(slot) == null) {
                view.setItem(raw, null);
                view.setItem(slot, current);
            }
            update(view, player);
        }
    }

    private void clickSexSlot(Player player, InventoryView view, @Nullable ItemStack cursor, @Nullable ItemStack current, boolean female) {
        final int slot = female ? FEMALE_SLOT : MALE_SLOT;
        if (HorseSaddle.has(cursor) && current == null) {
            final HorseSaddle saddle = HorseSaddle.load(cursor);
            if (female == saddle.isFemale() && saddle.getLevel() >= 5) {
                view.setItem(slot, cursor.clone());
                view.setCursor(null);
            }
        } else if ((cursor == null || cursor.getType() == Material.AIR) && current != null) {
            ItemUtils.addItem(player, current.clone());
            view.setItem(slot, null);
        }
        update(view, player);
    }

    private void clickResultSlot(InventoryView view, Player player) {
        final ItemStack femaleItem = view.getItem(FEMALE_SLOT);
        final ItemStack maleItem = view.getItem(MALE_SLOT);
        final ItemStack resultItem = view.getItem(RESULT_SLOT);
        if (CHECK_ITEM.isSimilar(resultItem) && HorseSaddle.has(femaleItem) && HorseSaddle.has(maleItem)) {
            final HorseSaddle femaleSaddle = HorseSaddle.load(femaleItem);
            if (availableMating(femaleSaddle)) {
                view.setItem(RESULT_SLOT, HorseManager.INSTANCE.mating(femaleItem, femaleSaddle, HorseSaddle.load(maleItem)));
            }
        } else if (resultItem != null && resultItem.getType() == Material.SADDLE) {
            ItemUtils.addItem(player, resultItem);
            update(view, player);
        }
    }

    private void update(InventoryView view, Player player) {
        final ItemStack femaleItem = view.getItem(FEMALE_SLOT);
        final ItemStack maleItem = view.getItem(MALE_SLOT);
        if (HorseSaddle.has(femaleItem) && HorseSaddle.has(maleItem)) {
            final HorseSaddle femaleSaddle = HorseSaddle.load(femaleItem);
            if (availableMating(femaleSaddle)) {
                view.setItem(RESULT_SLOT, CHECK_ITEM.clone());
            } else {
                view.setItem(RESULT_SLOT, CROSS_ITEM);
            }
        } else {
            view.setItem(RESULT_SLOT, CROSS_ITEM);
        }
        player.updateInventory();
    }

    private boolean availableMating(@NotNull final HorseSaddle femaleSaddle) {
        return femaleSaddle.isFemale() && femaleSaddle.getMatingCount() < GameConstants.HORSE_MATING_MAX_COUNT && femaleSaddle.availableMatingTime();
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
        final ItemStack result = inv.getItem(RESULT_SLOT);
        if (result != null && result.getType() == Material.SADDLE) {
            ItemUtils.addItem(event.getPlayer(), result);
        }
    }
}
