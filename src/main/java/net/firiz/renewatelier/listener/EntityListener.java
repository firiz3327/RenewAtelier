package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityListener implements Listener {

    private final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;

    @EventHandler
    private void spawnEntity(final EntitySpawnEvent e) {
        if (e.getEntity() instanceof LivingEntity && !aEntityUtils.hasLivingData((LivingEntity) e.getEntity())) {
            final TargetEntityTypes type = TargetEntityTypes.search(e.getEntity().getType());
            if (type != null) {
                e.setCancelled(true);
                aEntityUtils.spawn(type, e.getLocation());
            }
        }
    }

}
