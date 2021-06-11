package net.firiz.renewatelier.alchemy.kettle.inventory;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.kettle.KettleManager;
import net.firiz.renewatelier.alchemy.kettle.KettleUserData;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.inventory.manager.BiParamInventory;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author firiz
 */
public final class CatalystSelectInventory implements BiParamInventory<AlchemyRecipe, Inventory> {

    private static final KettleManager KETTLE_MANAGER = KettleManager.INSTANCE;
    private static final String TITLE = "KETTLE_SELECT_CATALYST";
    private final InventoryManager manager;
    private final List<UUID> openUsers = new ObjectArrayList<>();

    public CatalystSelectInventory(final InventoryManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().equals(TITLE);
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final AlchemyRecipe recipe, @NotNull final Inventory itemInv) {
        final Inventory inv = Bukkit.createInventory(player, 54, TITLE);
        setCatalystSlot(player.getUniqueId(), inv, recipe);
        player.openInventory(inv);
    }

    private void setCatalystSlot(final UUID uuid, final Inventory inv, final AlchemyRecipe recipe) {
        for (int i = 3; i < inv.getSize(); i++) {
            inv.setItem(i, null);
        }
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(uuid);
        final ItemStack catalystItem = kettleUserData.getCatalystItem();
        Catalyst catalyst;
        if (catalystItem == null) {
            catalyst = Catalyst.getDefaultCatalyst();
        } else {
            catalyst = AlchemyItemStatus.getMaterialNonNull(catalystItem).catalyst();
        }
        catalyst.setInv(inv, false);
        final Lore lore = new Lore();
        lore.add("");
        lore.add("使用可能カテゴリー").color(C.GRAY);
        recipe.getCatalystCategories().forEach(ct -> {
            switch (ct.getType()) {
                case MATERIAL -> lore.add("- " + ct.getMaterial().getName()).color(C.WHITE);
                case CATEGORY -> lore.add("- " + ct.getCategory().getName()).color(C.WHITE);
                default -> throw new IllegalStateException("Unexpected value: " + ct.getType());
            }
        });
        final Component displayName;
        if (catalystItem == null) {
            displayName = Text.of("触媒を指定せずに作成");
        } else {
            if (catalystItem.getItemMeta().hasDisplayName()) {
                displayName = catalystItem.getItemMeta().displayName();
            } else {
                displayName = Component.text(LanguageItemUtil.getLocalizeName(catalystItem));
            }
        }
        assert displayName != null;
        inv.setItem(37, ItemUtils.ci(
                catalystItem == null ? Material.BARRIER : catalystItem.getType(),
                0,
                Text.of("現在の触媒： ", C.GRAY).append(displayName).color(C.WHITE),
                lore
        ));
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) { // 増殖防止 - チェスト内のアイテムにフラッグを付与すれば対処可能
            e.setCancelled(true);
            return;
        }
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final int raw = e.getRawSlot();
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(uuid);
        final AlchemyRecipe recipe = kettleUserData.getRecipe();
        if (raw >= inv.getSize() && e.isShiftClick()) {
            e.setCancelled(true);
            if (Objects.requireNonNull(inv.getItem(37)).getType() != Material.BARRIER) {
                ItemUtils.addItem(player, kettleUserData.getCatalystItem());
            }
            final ItemStack current = e.getCurrentItem();
            setCatalystItem(inv, uuid, recipe, current);
            return;
        }
        if (e.getSlotType() == InventoryType.SlotType.CONTAINER && raw < inv.getSize()) {
            e.setCancelled(true);
            switch (raw) {
                case 37: {
                    if (e.getCurrentItem().getType() == Material.BARRIER) {
                        final ItemStack cursor = e.getCursor();
                        setCatalystItem(inv, uuid, recipe, cursor);
                    } else {
                        ItemUtils.addItem(player, kettleUserData.removeCatalystItem());
                        setCatalystSlot(uuid, inv, recipe);
                    }
                    break;
                }
                case 19: {
                    openUsers.add(player.getUniqueId());
                    player.closeInventory();
                    manager.getInventory(AlchemyKettleInventory.class).open(player, recipe, inv);
                    openUsers.remove(uuid);
                    break;
                }
                default:
                    // 想定外スロット
                    break;
            }
        }
    }

    private void setCatalystItem(Inventory inv, UUID uuid, AlchemyRecipe recipe, ItemStack item) {
        final AlchemyMaterial alchemyMaterial = AlchemyItemStatus.getMaterialNullable(item);
        if (alchemyMaterial != null && alchemyMaterial.hasUsefulCatalyst(recipe)) {
            final ItemStack cloneItem = item.clone();
            cloneItem.setAmount(1);
            item.setAmount(item.getAmount() - 1);
            KETTLE_MANAGER.getUserData(uuid).setCatalystItem(cloneItem);
            setCatalystSlot(uuid, inv, recipe);
        }
    }

    @Override
    public void onDrag(@NotNull final InventoryDragEvent e) {
        final Set<Integer> raws = e.getRawSlots();
        final Inventory inv = e.getInventory();
        raws.stream().filter(raw -> (raw >= 0 && raw < inv.getSize())).forEach(itemValue -> e.setCancelled(true));
    }

    @Override
    public void onClose(@NotNull final InventoryCloseEvent e) {
        if (!openUsers.contains(e.getPlayer().getUniqueId())) {
            KETTLE_MANAGER.remove((Player) e.getPlayer(), false);
        }
    }
}
