package net.firiz.renewatelier.utils.minecraft;

import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.npc.NPCObject;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public final class EntityUtils {

    private EntityUtils() {
    }

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;

    public static Object getHandle(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }

    public static boolean isDead(@NotNull final org.bukkit.entity.Entity entity) {
        return entity.isDead() || (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0);
    }

    public static Stream<Player> rangePlayers(@NotNull final Location location, double radius, int maxPlayerCount) {
        return location.getNearbyPlayers(radius).stream().limit(maxPlayerCount);
    }

    public static Stream<Mob> rangeMobs(@NotNull final Location location, double radius, int maxMobCount) {
        return location.getNearbyEntitiesByType(Mob.class, radius).stream().filter(entity -> !(entity instanceof Player) && !NPCObject.hasEntity(entity)).limit(maxMobCount);
    }

    @Nullable
    public static EntityStatus getEntityStatus(@Nullable final LivingEntity entity) {
        if (entity != null) {
            if (aEntityUtils.hasLivingData(entity)) {
                final LivingData livingData = aEntityUtils.getLivingData(entity);
                if (livingData.hasStats()) {
                    return livingData.getStats();
                }
            } else if (entity instanceof Player) {
                return psm.getChar(entity.getUniqueId()).getCharStats();
            }
        }
        return null;
    }
}
