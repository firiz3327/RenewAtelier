package net.firiz.renewatelier.listener.player;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.kettle.inventory.AlchemyKettleInventory;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.entity.horse.HorseManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.event.AsyncPlayerInteractEntityEvent;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.notification.Notification;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.version.inject.PlayerInjection;
import net.firiz.renewatelier.version.packet.PayloadPacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

/**
 * @author firiz
 */
public class PlayerListener implements Listener {

    private final PlayerInteractManager interactManager = new PlayerInteractManager();

    @EventHandler
    private void readyArrow(final PlayerReadyArrowEvent e) {
        final Player player = e.getPlayer();
        final ItemStack item = e.getBow();
        if (item.getType() == Material.CROSSBOW && !((CrossbowMeta) item.getItemMeta()).hasChargedProjectiles()) {
            e.setCancelled(ArrowManager.INSTANCE.interactCrossbow(player));
        }
    }

    @EventHandler
    private void loadCrossbow(final EntityLoadCrossbowEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player) e.getEntity();
            final ItemStack item = e.getCrossbow();
            if (item.getType() == Material.CROSSBOW && !((CrossbowMeta) item.getItemMeta()).hasChargedProjectiles()) {
                e.setCancelled(ArrowManager.INSTANCE.interactCrossbow(player));
                player.updateInventory();
            }
        }
    }

    @EventHandler
    private void interact(final PlayerInteractEvent e) {
        interactManager.interact(e, e.getPlayer(), e.getAction(), e.getClickedBlock(), e.getItem());
    }

    @EventHandler
    private void pickup(final EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            InventoryManager.INSTANCE.getInventory(AlchemyKettleInventory.class).pickup(e);
        }
    }

    @EventHandler
    private void interactEntity(final AsyncPlayerInteractEntityEvent e) {
        interactManager.interactEntity(e, e.getPlayer(), e.getEntity(), e.getHand(), e.isRightClick(), e.getEntityId());
    }

    @EventHandler
    private void join(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        PlayerSaveManager.INSTANCE.loadStatus(player);
        PlayerInjection.inject(player);
        Notification.loginNotification(player);
        AtelierPlugin.getPlugin().getTabList().update(player);
        PayloadPacket.sendBrand(player);
    }

    @EventHandler
    private void held(final PlayerItemHeldEvent e) {
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).getCharStats().updateWeapon(e.getNewSlot());
    }

    @EventHandler
    private void swap(final PlayerSwapHandItemsEvent e) {
        final ItemStack item = e.getMainHandItem();
        if (item != null && item.getType() == Material.SADDLE) {
            HorseManager.INSTANCE.swap(e, e.getPlayer(), item);
        }
    }

    @EventHandler
    private void armorChange(final PlayerArmorChangeEvent e) {
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).getCharStats().updateEquip();
    }

    @EventHandler
    private void quit(final PlayerQuitEvent e) {
        PlayerSaveManager.INSTANCE.unloadStatus(e.getPlayer().getUniqueId());
        HorseManager.INSTANCE.checkHorse(e.getPlayer());
    }

    @EventHandler
    private void respawn(final PlayerRespawnEvent e) {
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).respawn();
    }

    @EventHandler
    private void death(final PlayerDeathEvent e) {
        e.setReviveHealth(1);
        e.setKeepLevel(true);
    }

    @EventHandler
    private void expChange(final PlayerExpChangeEvent e) {
        e.setAmount(0);
    }

    @EventHandler
    private void pickup(final PlayerAttemptPickupItemEvent e) {
        final Char player = PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId());
        final ItemStack item = e.getItem().getItemStack();
        if (e.getItem().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            player.increaseIdea(item);
        }
        e.setCancelled(player.getBag().add(player.getPlayer(), e.getItem()));
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                AtelierPlugin.getPlugin(),
                () -> player.getCharStats().updateWeapon(),
                1
        );
    }

    @EventHandler
    private void drop(final PlayerDropItemEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                AtelierPlugin.getPlugin(),
                () -> PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).getCharStats().updateWeapon(),
                1
        );
    }

}
