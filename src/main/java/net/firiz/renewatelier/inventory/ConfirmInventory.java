package net.firiz.renewatelier.inventory;

import java.util.Map;
import java.util.UUID;
import java.util.function.ObjIntConsumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.firiz.ateliercommonapi.nms.packet.InventoryPacket;
import net.firiz.renewatelier.inventory.manager.ParamInventory;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
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

    private static final Component TITLE = Component.text("Confirm");
    private final Map<UUID, ObjectObjectImmutablePair<Component, ObjIntConsumer<Player>>> consumers = new Object2ObjectOpenHashMap<>();

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.title().equals(TITLE);
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final ConfirmInfo info) {
        final UUID uuid = player.getUniqueId();
        final Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, TITLE);
        inv.setItem(1, ItemUtils.ci(Material.LIME_WOOL, 0, info.yes, null));
        inv.setItem(3, ItemUtils.ci(Material.RED_WOOL, 0, info.no, null));
        if (!(consumers.containsKey(uuid) && consumers.get(uuid).left().equals(info.title))) {
            consumers.put(uuid, new ObjectObjectImmutablePair<>(info.title, info.consumer));
        }
        player.openInventory(inv);
        InventoryPacket.update(player, info.title, InventoryPacket.InventoryPacketType.HOPPER);
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent e) {
        e.setCancelled(true);
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final ObjIntConsumer<Player> cr = consumers.get(uuid).right();
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
            final ObjIntConsumer<Player> cr = consumers.get(uuid).right();
            cr.accept(player, -1);
            consumers.remove(uuid);
        }
    }

    public record ConfirmInfo(Component title, String yes, String no, ObjIntConsumer<Player> consumer) {
    }

}
