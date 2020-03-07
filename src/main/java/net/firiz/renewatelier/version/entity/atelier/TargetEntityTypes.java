package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.entity.Race;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.entity.EntityType;

public enum TargetEntityTypes {
    ZOMBIE(EntityType.ZOMBIE, EntityZombie.class, Race.UNDEAD,null, "ZOMBIE"),
    ZOMBIE_PIG(EntityType.PIG_ZOMBIE, EntityPigZombie.class, Race.UNDEAD, null, "ZOMBIE_PIGMAN"),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, EntityZombieVillager.class, Race.UNDEAD, null, "ZOMBIE_VILLAGER"),
    ZOMBIE_HUSK(EntityType.HUSK, EntityZombieHusk.class, Race.UNDEAD, null, "HUSK"),
    ZOMBIE_GIANT(EntityType.GIANT, EntityGiantZombie.class, Race.UNDEAD, null, "GIANT"),
    SKELETON(EntityType.SKELETON, EntitySkeleton.class, Race.UNDEAD, null, "SKELETON"),
    SKELETON_WITHER(EntityType.WITHER_SKELETON, EntitySkeletonWither.class, Race.UNDEAD, null, "WITHER_SKELETON"),
    SKELETON_STRAY(EntityType.STRAY, EntitySkeletonStray.class, Race.UNDEAD, null, "STRAY"),
    CREEPER(EntityType.CREEPER, EntityCreeper.class, Race.UNDEAD, null, "CREEPER"),
    SPIDER(EntityType.SPIDER, EntitySpider.class, Race.INSECT, null, "SPIDER"),
    SPIDER_CAVE(EntityType.CAVE_SPIDER, EntityCaveSpider.class, Race.INSECT, null, "CAVE_SPIDER"),
    SLIME(EntityType.SLIME, EntitySlime.class, Race.PUNI, null, "SLIME"),
    ;

    final EntityType type;
    final Class<?> clasz;
    final Race race;
    final String body;

    TargetEntityTypes(EntityType type, Class<? extends EntityLiving> clasz, Race race, String body) {
        this(type, clasz, race, body, null);
    }

    TargetEntityTypes(EntityType type, Class<? extends EntityLiving> clasz, Race race, String body, String entityType) {
        this.type = type;
        this.clasz = clasz;
        this.race = race;
        if (body != null) {
            this.body = body.replace("$NMS", "net.minecraft.server.v1_15_R1");
        } else if (entityType != null) {
            this.body = "{super(net.minecraft.server.v1_15_R1.EntityTypes." + entityType + ", (net.minecraft.server.v1_15_R1.World) $args[0]);}";
        } else {
            throw new IllegalStateException("not found body code.");
        }
    }
}
