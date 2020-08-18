package net.firiz.renewatelier.inventory;

import java.util.Map;
import java.util.UUID;
import java.util.function.ObjIntConsumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.inventory.manager.ParamInventory;
import net.firiz.renewatelier.utils.chores.ItemUtils;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.firiz.renewatelier.version.packet.InventoryPacket;
import net.firiz.renewatelier.version.packet.InventoryPacket.InventoryPacketType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public final class ConfirmInventory implements ParamInventory<ConfirmInventory.ConfirmInfo> {

    private static final String CONFIRM_STR = "-Confirm";
    private final Map<UUID, ImmutablePair<String, ObjIntConsumer<Player>>> consumers = new Object2ObjectOpenHashMap<>();

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().endsWith(CONFIRM_STR);
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final ConfirmInfo info) {
        final UUID uuid = player.getUniqueId();
        final Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, uuid.toString().concat(CONFIRM_STR));
        inv.setItem(1, ItemUtils.ci(Material.LIME_WOOL, 0, info.yes, null));
        inv.setItem(3, ItemUtils.ci(Material.RED_WOOL, 0, info.no, null));
        if (!consumers.containsKey(uuid) || !consumers.get(uuid).getLeft().equals(info.title)) {
            consumers.put(uuid, new ImmutablePair<>(info.title, info.consumer));
        }
        player.openInventory(inv);
        InventoryPacket.update(player, info.title, InventoryPacketType.HOPPER);
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent e) {
        e.setCancelled(true);
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final ObjIntConsumer<Player> cr = consumers.get(uuid).getRight();
        switch (e.getRawSlot()) {
            case 1:
                cr.accept(player, 1);
                break;
            case 3:
                cr.accept(player, 0);
                break;
            default:
                // Yes・Noボタンのスロット以外、想定しない
                break;
        }
    }

    @Override
    public void onDrag(@NotNull final InventoryDragEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(@NotNull final InventoryCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (consumers.containsKey(uuid)) {
            final ObjIntConsumer<Player> cr = consumers.get(uuid).getRight();
            cr.accept(player, -1);
            consumers.remove(uuid);
        }
    }

    public static class ConfirmInfo {
        private final String title;
        private final String yes;
        private final String no;
        private final ObjIntConsumer<Player> consumer;

        public ConfirmInfo(String title, String yes, String no, ObjIntConsumer<Player> consumer) {
            this.title = title;
            this.yes = yes;
            this.no = no;
            this.consumer = consumer;
        }
    }

}
