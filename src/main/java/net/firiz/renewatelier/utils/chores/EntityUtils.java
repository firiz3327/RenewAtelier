package net.firiz.renewatelier.utils.chores;

import net.firiz.renewatelier.npc.NPCObject;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.stream.Stream;

public final class EntityUtils {

    private EntityUtils() {
    }

    public static boolean isDead(@NotNull final org.bukkit.entity.Entity entity) {
        return entity.isDead() || (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0);
    }

    public static Stream<Creature> rangeCreatures(@NotNull final Location location, double radius, int maxMobCount) {
        return rangeCreatures(location, radius, maxMobCount, null);
    }

    public static Stream<Creature> rangeCreatures(@NotNull final Location location, double radius, int maxMobCount, @Nullable Predicate<? super Creature> additionalFilter) {
        Stream<Creature> stream = location.getNearbyEntitiesByType(Creature.class, radius).stream().filter(entity -> !(entity instanceof Player) && !NPCObject.hasEntity(entity));
        if (additionalFilter != null) {
            stream = stream.filter(additionalFilter);
        }
        return stream.limit(maxMobCount);
    }

}
