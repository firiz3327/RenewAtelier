package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.entity.Race;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.entity.EntityType;

public enum TargetEntityTypes {
    ZOMBIE(EntityType.ZOMBIE, EntityZombie.class, Race.UNDEAD,null, "ZOMBIE", "ゾンビ"),
    ZOMBIE_PIG(EntityType.PIG_ZOMBIE, EntityPigZombie.class, Race.UNDEAD, null, "ZOMBIE_PIGMAN", "ゾンビピッグマン"),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, EntityZombieVillager.class, Race.UNDEAD, null, "ZOMBIE_VILLAGER", "村人ゾンビ"),
    ZOMBIE_HUSK(EntityType.HUSK, EntityZombieHusk.class, Race.UNDEAD, null, "HUSK", "ハスク"),
    ZOMBIE_GIANT(EntityType.GIANT, EntityGiantZombie.class, Race.UNDEAD, null, "GIANT", "巨人"),
    SKELETON(EntityType.SKELETON, EntitySkeleton.class, Race.UNDEAD, null, "SKELETON", "スケルトン"),
    SKELETON_WITHER(EntityType.WITHER_SKELETON, EntitySkeletonWither.class, Race.UNDEAD, null, "WITHER_SKELETON", "ウィザースケルトン"),
    SKELETON_STRAY(EntityType.STRAY, EntitySkeletonStray.class, Race.UNDEAD, null, "STRAY", "ストレイ"),
    CREEPER(EntityType.CREEPER, EntityCreeper.class, Race.UNDEAD, null, "CREEPER", "クリーパー"),
    SPIDER(EntityType.SPIDER, EntitySpider.class, Race.INSECT, null, "SPIDER", "クモ"),
    SPIDER_CAVE(EntityType.CAVE_SPIDER, EntityCaveSpider.class, Race.INSECT, null, "CAVE_SPIDER", "洞窟グモ"),
    SLIME(EntityType.SLIME, EntitySlime.class, Race.PUNI, null, "SLIME", "スライム"),
    ;

    final EntityType type;
    final Class<?> clasz;
    final Race race;
    final String body;
    final String name;

    TargetEntityTypes(EntityType type, Class<? extends EntityLiving> clasz, Race race, String body, String name) {
        this(type, clasz, race, body, null, name);
    }

    TargetEntityTypes(EntityType type, Class<? extends EntityLiving> clasz, Race race, String body, String entityType, String name) {
        this.type = type;
        this.clasz = clasz;
        this.race = race;
        this.name = name;
        if (body != null) {
            this.body = body.replace("$NMS", "net.minecraft.server.v1_15_R1");
        } else if (entityType != null) {
            this.body = "{super(net.minecraft.server.v1_15_R1.EntityTypes." + entityType + ", (net.minecraft.server.v1_15_R1.World) $args[0]);}";
        } else {
            throw new IllegalStateException("not found body code.");
        }
    }
}
