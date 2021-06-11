package net.firiz.renewatelier.server.listener;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.entity.horse.HorseManager;
import net.firiz.renewatelier.version.entity.drop.PlayerDropItem;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Collections;

public class EntityListener implements Listener {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final ArrowManager arrowManager = ArrowManager.INSTANCE;
    private static final HorseManager horseManager = HorseManager.INSTANCE;

    @EventHandler
    private void mount(final EntityMountEvent e) {
        if (e.getMount() instanceof Horse && e.getEntity() instanceof Player) {
            horseManager.onMount(e, (Horse) e.getMount(), (Player) e.getEntity());
        }
    }

    @EventHandler
    private void tame(final EntityTameEvent e) {
        if (e.getOwner() instanceof Player && e.getEntity() instanceof Horse) {
            horseManager.onTame((Player) e.getOwner(), (Horse) e.getEntity());
        }
    }

    @EventHandler
    private void spawnMob(final CreatureSpawnEvent e) {
        final Entity entity = e.getEntity();
        if (!aEntityUtils.hasLivingData(entity)) {
            final TargetEntityTypes type = TargetEntityTypes.search(entity.getType());
            if (type != null) {
                final boolean cancel;
                final boolean spawnEgg = e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG;
                switch (e.getSpawnReason()) {
                    case BUILD_IRONGOLEM:
                    case BUILD_SNOWMAN:
                    case BUILD_WITHER:
                        cancel = false;
                        Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), entity::remove, 1);
                        break;
                    default:
                        cancel = !spawnEgg;
                        e.setCancelled(true);
                        break;
                }
                final Entity atelierEntity = aEntityUtils.spawn(
                        type,
                        e.getLocation(),
                        cancel
                );
                if (spawnEgg && atelierEntity != null) {
                    atelierEntity.setCustomName("spawnEgg");
                }
            }
        }
    }

    @EventHandler
    private void shootBow(final EntityShootBowEvent e) {
        if (e.getProjectile() instanceof AbstractArrow) {
            final ItemStack bow = e.getBow();
            if (e.getEntity() instanceof Player) {
                if (bow != null && bow.getType() == Material.CROSSBOW) {
                    final CrossbowMeta itemMeta = (CrossbowMeta) bow.getItemMeta();
                    assert itemMeta != null;
                    arrowManager.shootCrossbow((Player) e.getEntity(), bow, (AbstractArrow) e.getProjectile(), itemMeta.getChargedProjectiles().get(0));
                } else if (arrowManager.shootBow(e.getEntity(), bow, (AbstractArrow) e.getProjectile(), e.getForce())) {
                    e.setCancelled(true);
                    ((Player) e.getEntity()).updateInventory();
                }
            } else {
                e.setCancelled(arrowManager.shootBow(e.getEntity(), bow, (AbstractArrow) e.getProjectile(), e.getForce()));
            }
        }
    }

    /*
    @EventHandler
    private void vehicleDamage(final VehicleDamageEvent e) {
        if (e.getVehicle() instanceof LootableEntityInventory && ((LootableEntityInventory) e.getVehicle()).hasLootTable()) {
            ReplaceVanillaItems.loot(e.getAttacker(), (LootableEntityInventory) e.getVehicle());
        }
    }
     */

    @EventHandler
    private void itemMerge(final ItemMergeEvent e) {
        if (PlayerDropItem.isPlayerDrop(e.getEntity()) || PlayerDropItem.isPlayerDrop(e.getTarget())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void death(final EntityDeathEvent e) {
        e.setDroppedExp(0);
        if (aEntityUtils.hasLivingData(e.getEntity())) {
            aEntityUtils.getLivingData(e.getEntity()).drop(
                    e.getEntity().getLocation(),
                    Collections.unmodifiableList(e.getDrops())
            );
            e.getDrops().clear();
        }
    }

    /*
    @EventHandler
    private void itemSpawn(final ItemSpawnEvent e) {
        final ItemStack item = e.getEntity().getItemStack();
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            ReplaceVanillaItems.changeItems(true, item);
        }
    }
     */

}
