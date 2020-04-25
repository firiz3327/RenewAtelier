package net.firiz.renewatelier.listener;

import com.destroystokyo.paper.loottable.LootableEntityInventory;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

public class EntityListener implements Listener {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private final ArrowManager arrowManager = new ArrowManager();

    @EventHandler
    private void spawnEntity(final EntitySpawnEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof LivingEntity && !aEntityUtils.hasLivingData((LivingEntity) entity)) {
            final TargetEntityTypes type = TargetEntityTypes.search(entity.getType());
            if (type != null) {
                e.setCancelled(true);
                aEntityUtils.spawn(type, e.getLocation());
            }
        } else if (entity.getType() == EntityType.EXPERIENCE_ORB) {
            e.setCancelled(true);
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
                    arrowManager.shootBow(e.getEntity(), bow, (AbstractArrow) e.getProjectile(), e.getForce());
                }
            } else {
                arrowManager.shootBow(e.getEntity(), bow, (AbstractArrow) e.getProjectile(), e.getForce());
            }
        }
    }

    @EventHandler
    private void vehicleDamage(final VehicleDamageEvent e) {
        if (e.getVehicle() instanceof LootableEntityInventory && ((LootableEntityInventory) e.getVehicle()).hasLootTable()) {
            ReplaceVanillaItems.loot(e.getAttacker(), (LootableEntityInventory) e.getVehicle());
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
