package net.firiz.renewatelier.inventory;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.inventory.manager.ParamInventory;
import net.firiz.renewatelier.item.json.AlchemyItemBag;
import net.firiz.renewatelier.utils.chores.ItemUtils;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class BagInventory implements ParamInventory<ItemStack> {

    private static final String TITLE = "錬金バッグ";
    private final Object2ObjectMap<UUID, ImmutablePair<ItemStack, AlchemyItemBag>> bags = new Object2ObjectOpenHashMap<>();

    @Override
    public void open(@NotNull Player player, @NotNull ItemStack item) {
        final AlchemyItemBag bag = AlchemyItemBag.load(item);
        bags.put(player.getUniqueId(), new ImmutablePair<>(item, bag));
        final Inventory inv = Bukkit.createInventory(player, 27, TITLE);
        for (int i = 0; i < Math.min(inv.getSize(), bag.getItems().size()); i++) {
            inv.setItem(i, bag.getItems().get(i).toItemStack());
        }
        player.openInventory(inv);
    }

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.getTitle().equals(TITLE);
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        final ImmutablePair<ItemStack, AlchemyItemBag> bagData = bags.remove(event.getPlayer().getUniqueId());
        final AlchemyItemBag bag = bagData.getRight();
        final List<ItemStack> notApplicableItems = bag.refreshInventory(event.getInventory());
        bag.writeItem(bagData.getLeft());
        notApplicableItems.forEach(i -> ItemUtils.addItem(event.getPlayer(), i));
    }
}
