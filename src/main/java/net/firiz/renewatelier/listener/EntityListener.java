package net.firiz.renewatelier.listener;

import com.destroystokyo.paper.loottable.LootableEntityInventory;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import java.util.Collections;

public class EntityListener implements Listener {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final ArrowManager arrowManager = ArrowManager.INSTANCE;

    @EventHandler
    private void spawnCreature(final CreatureSpawnEvent e) {
        final Entity entity = e.getEntity();
        if (!aEntityUtils.hasLivingData(entity)) {
            final TargetEntityTypes type = TargetEntityTypes.search(entity.getType());
            if (type != null) {
                e.setCancelled(true);
                final boolean spawnEgg = e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG;
                final Entity atelierEntity = aEntityUtils.spawn(
                        type,
                        e.getLocation(),
                        !spawnEgg
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
                } else {
                    if (AlchemyItemStatus.has(bow)) {
                        final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(bow);
                        assert itemStatus != null;
                        if (itemStatus.getAlchemyMaterial().getItemSkill() != null) {
                            e.setCancelled(true);
                            itemStatus.getAlchemyMaterial().getItemSkill().createSkill(
                                    (Player) e.getEntity(),
                                    itemStatus,
                                    e.getForce()
                            ).fire();
                            return;
                        }
                    }
                    if (arrowManager.shootBow(e.getEntity(), bow, (AbstractArrow) e.getProjectile(), e.getForce())) {
                        e.setCancelled(true);
                        ((Player) e.getEntity()).updateInventory();
                    }
                }
            } else {
                e.setCancelled(arrowManager.shootBow(e.getEntity(), bow, (AbstractArrow) e.getProjectile(), e.getForce()));
            }
        }
    }

    @EventHandler
    private void vehicleDamage(final VehicleDamageEvent e) {
        if (e.getVehicle() instanceof LootableEntityInventory && ((LootableEntityInventory) e.getVehicle()).hasLootTable()) {
            ReplaceVanillaItems.loot(e.getAttacker(), (LootableEntityInventory) e.getVehicle());
        }
    }

    @EventHandler
    private void itemMerge(final ItemMergeEvent e) {
        if (e.getEntity().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM
                || e.getTarget().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
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
