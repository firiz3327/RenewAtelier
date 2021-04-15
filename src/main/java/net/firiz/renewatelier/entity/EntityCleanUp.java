package net.firiz.renewatelier.entity;

import net.firiz.ateliercommonapi.loop.TickRunnable;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public final class EntityCleanUp implements TickRunnable {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;

    @Override
    public void run() {
        Bukkit.getWorlds().forEach(world ->
                world.getEntities().stream()
                        .filter(aEntityUtils::hasLivingData)
                        .forEach(this::removeEntity)
        );
    }

    /**
     * エンティティを一定のルールに基づいて削除する
     * <p>
     * エンティティの場所から128マス以内にプレイヤーがいない場合、そのエンティティを削除する
     * エンティティの場所から32マス以内にプレイヤーがいない場合、2.5％の確率で削除する
     * <p>
     * ※ 1秒毎に実行し、2.5%で削除する場合は毎分78.11%の確率で削除される
     * ※ minecraft公式の1/20秒毎に0.125%の確率で削除する場合は毎分77.71%で削除される
     *
     * @param entity 削除するエンティティ
     */
    private void removeEntity(final Entity entity) {
        final Location location = entity.getLocation();
        final Collection<Player> nearby128 = location.getNearbyPlayers(128);
        // 32 * 32 = 1024
        if (nearby128.isEmpty() || (
                nearby128.stream().noneMatch(player -> 1024 > player.getLocation().distanceSquared(location))
                        && Randomizer.percent(25, 1000)
        )) {
            if (entity.getCustomName() != null) {
                CommonUtils.log("cleanUp CN: " + entity.getCustomName());
            }
            entity.remove();
        }
    }

}
