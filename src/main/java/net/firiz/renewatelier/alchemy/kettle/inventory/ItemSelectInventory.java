package net.firiz.renewatelier.alchemy.kettle.inventory;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.nms.packet.InventoryPacket;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.kettle.KettleManager;
import net.firiz.renewatelier.alchemy.kettle.KettleUserData;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.inventory.manager.BiParamInventory;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.inventory.item.CustomModelMaterial;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author firiz
 */
public final class ItemSelectInventory implements BiParamInventory<AlchemyRecipe, Inventory> {

    private static final KettleManager KETTLE_MANAGER = KettleManager.INSTANCE;
    private static final Component TITLE = Component.text("KETTLE_SELECT_ITEM");
    private final InventoryManager manager;
    private final List<UUID> openUsers = new ObjectArrayList<>();

    public ItemSelectInventory(final InventoryManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.title().equals(TITLE);
    }

    @Override
    public void open(@NotNull Player player, @NotNull AlchemyRecipe recipe, @NotNull Inventory recipeInv) {
        final Inventory inv = Bukkit.createInventory(player, 45, TITLE);
        inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, 1508, "", null));
        inv.setItem(36, ItemUtils.ci(Material.DIAMOND_AXE, 1562, "", null));
        inv.setItem(1, ItemUtils.setSetting(ItemUtils.ci(Material.BARRIER, 0, "", null), KettleConstants.scrollKey, 0)); // ページ番号

        for (int i = 3; i < inv.getSize(); i++) {
            if (i != 36) {
                inv.setItem(i, ItemUtils.ci(Material.BARRIER, 0, "", null));
            }
        }
        KETTLE_MANAGER.getUserData(player.getUniqueId()).createPageItemSize(recipe.getReqMaterial().size());
        setItemSelectNumber(player, inv, recipe, 0);
        player.openInventory(inv);
        InventoryPacket.update(player, Component.text(""), InventoryPacket.InventoryPacketType.CHEST);
    }

    private void setItemSelectNumber(final Player player, final Inventory inv, final AlchemyRecipe recipe, final int add_page) {
        final List<RequireAmountMaterial> reqs = recipe.getReqMaterial();
        final int nextPage = Math.min(reqs.size() - 1, Math.max(0, getPage(inv) + add_page));
        setPage(inv, nextPage);

        final RequireAmountMaterial requireMaterial = reqs.get(nextPage);
        Component name = null;
        CustomModelMaterial material;
        switch (requireMaterial.getType()) {
            case MATERIAL -> {
                final AlchemyMaterial alchemyMaterial = requireMaterial.getMaterial();
                if (!alchemyMaterial.defaultName()) {
                    name = alchemyMaterial.getName();
                }
                material = alchemyMaterial.material();
            }
            case CATEGORY -> {
                final Category c = requireMaterial.getCategory();
                name = c.getNameComponent().color(C.WHITE);
                material = c.getMaterial();
            }
            default -> throw new IllegalStateException("illegal type. " + requireMaterial);
        }

        if (material != null) {
            final int req_amount = requireMaterial.getAmount();
            final ItemStack item = material.toItemStack();
            final ItemMeta meta = item.getItemMeta();
            if (name != null) {
                meta.displayName(name);
            }
            final Lore lore = new Lore();
            lore.add("必要個数: " + req_amount).color(C.GRAY);
            meta.lore(lore);
            item.setItemMeta(meta);
            inv.setItem(4, item);

            final List<ItemStack> useItems = KETTLE_MANAGER.getUserData(player.getUniqueId()).getPageItems(nextPage);
            int count = 0;
            for (int i = 1; i <= 3; i++) { // 12,13,14 - 21,22,23 - 30,31,32
                for (int j = 0; j < 3; j++) {
                    final int nextSlot = i * 12 - ((i - 1) * 3) + j;
                    if (count >= useItems.size()) {
                        inv.setItem(nextSlot, null);
                    } else {
                        inv.setItem(nextSlot, useItems.get(count));
                    }
                    count++;
                }
            }
        }

    }

    private boolean checkMaxSlot(final UUID uuid, final AlchemyRecipe recipe, final int page) {
        final List<RequireAmountMaterial> requireMaterials = recipe.getReqMaterial();
        final List<ItemStack> pageItems = KETTLE_MANAGER.getUserData(uuid).getPageItems(page);
        final RequireAmountMaterial requireMaterial = requireMaterials.get(page);
        CustomModelMaterial material;
        switch (requireMaterial.getType()) {
            case MATERIAL -> {
                final AlchemyMaterial alchemyMaterial = requireMaterial.getMaterial();
                material = alchemyMaterial.material();
            }
            case CATEGORY -> {
                final Category c = requireMaterial.getCategory();
                material = c.getMaterial();
            }
            default -> throw new IllegalStateException("illegal type. " + requireMaterial);
        }
        if (material != null && !pageItems.isEmpty()) {
            final int requireAmount = requireMaterial.getAmount();
            CommonUtils.log(pageItems.size() + " " + (pageItems.size() >= requireAmount) + " " + requireMaterial);
            return pageItems.size() < requireAmount;
        }
        return true;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) { // 増殖防止
            e.setCancelled(true);
            return;
        }
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final KettleUserData kettleUserData = KETTLE_MANAGER.getUserData(uuid);
        final int page = getPage(inv);
        final int raw = e.getRawSlot();
        final AlchemyRecipe recipe = kettleUserData.getRecipe();

        // shift click
        if ((e.getSlotType() == InventoryType.SlotType.CONTAINER || e.getSlotType() == InventoryType.SlotType.QUICKBAR)
                && raw >= inv.getSize() && e.isShiftClick()) {
            e.setCancelled(true);
            final ItemStack current = e.getCurrentItem();
            if (current != null && current.getType() != Material.AIR && checkMaxSlot(uuid, recipe, page)) {
                final RequireAmountMaterial data = recipe.getReqMaterial().get(page);
                if (ItemUtils.checkMaterial(current, data)) {
                    final ItemStack cloneItem = current.clone();
                    cloneItem.setAmount(1);
                    kettleUserData.addPageItem(page, cloneItem);
                    e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
                    setItemSelectNumber(player, inv, recipe, 0); // refresh page
                }
            }
            return;
        }

        // normal click
        if (e.getSlotType() == InventoryType.SlotType.CONTAINER && raw < inv.getSize()) {
            e.setCancelled(true);
            if ((raw >= 12 && raw <= 14) // 配置可能スロット
                    || (raw >= 21 && raw <= 23)
                    || (raw >= 30 && raw <= 32)) {
                final ItemStack current = e.getCurrentItem();
                if (current != null) {
                    final ItemStack cursor = e.getCursor();
                    if (current.getType() != Material.AIR) { // アイテムを外す
                        final List<ItemStack> items = kettleUserData.getPageItems(page);
                        if (items != null && !items.isEmpty()) {
                            final ItemStack item = items.get(raw >= 21 ? (raw >= 30 ? raw - 24 : raw - 18) : raw - 12);
                            if (kettleUserData.removePageItem(page, item)) {
                                ItemUtils.addItem(player, item);
                            }
                        }
                    } else if (checkMaxSlot(uuid, recipe, page)) { // アイテムをスロットに設置
                        final RequireAmountMaterial data = recipe.getReqMaterial().get(page);
                        if (ItemUtils.checkMaterial(cursor, data)) {
                            final ItemStack cloneItem = cursor.clone();
                            cloneItem.setAmount(1);
                            cursor.setAmount(cursor.getAmount() - 1);
                            kettleUserData.addPageItem(page, cloneItem);
                        }
                    }
                    setItemSelectNumber(player, inv, recipe, 0); // refresh page
                }
            } else if (raw == 20 || raw == 24) { // ページ変更
                setItemSelectNumber(player, inv, recipe, raw != 20 ? 1 : -1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
            } else if (raw == 40) { // 決定
                int check = 0;
                for (int i = 0; i < recipe.getReqMaterial().size(); i++) {
                    if (checkMaxSlot(uuid, recipe, i)) {
                        check = 0;
                        break;
                    }
                    check++;
                }
                if (check == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                    return;
                }
                openUsers.add(uuid);
                player.closeInventory();
                manager.getInventory(CatalystSelectInventory.class).open(player, recipe, inv);
                openUsers.remove(uuid);
            }
        }
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent e) {
        final Set<Integer> raws = e.getRawSlots();
        final Inventory inv = e.getInventory();
        raws.stream().filter(raw -> (raw >= 0 && raw < inv.getSize())).forEach(itemValue -> e.setCancelled(true));
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent e) {
        if (!openUsers.contains(e.getPlayer().getUniqueId())) {
            KETTLE_MANAGER.remove((Player) e.getPlayer(), false);
        }
    }

    public int getPage(Inventory inv) {
        return ItemUtils.getSettingInt(Objects.requireNonNull(inv.getItem(1)), KettleConstants.scrollKey);
    }

    public void setPage(Inventory inv, int scroll) {
        ItemUtils.setSetting(Objects.requireNonNull(inv.getItem(1)), KettleConstants.scrollKey, scroll);
    }

}
