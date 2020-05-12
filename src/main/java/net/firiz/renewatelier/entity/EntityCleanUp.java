package net.firiz.renewatelier.entity;

import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public final class EntityCleanUp implements Runnable {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;

    @Override
    public void run() {
        Bukkit.getWorlds().forEach(world ->
                world.getEntities().stream()
                        .filter(aEntityUtils::hasLivingData)
                        .forEach(entity -> removeEntity(entity))
        );
    }

    /**
     * 1秒毎に実行し、2.5%で削除する場合は毎分78.11%の確率で削除される
     * minecraft公式の1/20秒毎に0.125%の確率で削除する場合は毎分77.71%で削除される
     */
    private void removeEntity(final Entity entity) {
        final Location location = entity.getLocation();
        final Collection<Player> nearby128 = location.getNearbyPlayers(128);
        if (nearby128.isEmpty() || (
                nearby128.stream().anyMatch(player -> 1024 > player.getLocation().distanceSquared(location))
                        && Randomizer.percent(25, 1000)
        )) {
            entity.remove();
        }
    }

}
