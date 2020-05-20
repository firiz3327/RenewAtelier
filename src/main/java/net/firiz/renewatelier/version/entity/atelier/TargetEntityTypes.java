package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.version.entity.atelier.vanilla.LivingCreeper;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public enum TargetEntityTypes {
    ZOMBIE(EntityType.ZOMBIE, EntityZombie.class, Race.UNDEAD, null, "ZOMBIE", "ゾンビ"),
    ZOMBIE_PIG(EntityType.PIG_ZOMBIE, EntityPigZombie.class, Race.UNDEAD, null, "ZOMBIE_PIGMAN", "ゾンビピッグマン", (entity -> setMainHand(entity, new ItemStack(Material.GOLDEN_SWORD)))),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, EntityZombieVillager.class, Race.UNDEAD, null, "ZOMBIE_VILLAGER", "村人ゾンビ"),
    ZOMBIE_HUSK(EntityType.HUSK, EntityZombieHusk.class, Race.UNDEAD, null, "HUSK", "ハスク"),
    ZOMBIE_GIANT(EntityType.GIANT, EntityGiantZombie.class, Race.UNDEAD, null, "GIANT", "巨人"),
    SKELETON(EntityType.SKELETON, EntitySkeleton.class, Race.UNDEAD, null, "SKELETON", "スケルトン", (entity -> setMainHand(entity, new ItemStack(Material.BOW)))),
    SKELETON_WITHER(EntityType.WITHER_SKELETON, EntitySkeletonWither.class, Race.UNDEAD, null, "WITHER_SKELETON", "ウィザースケルトン", (entity -> setMainHand(entity, new ItemStack(Material.STONE_SWORD)))),
    SKELETON_STRAY(EntityType.STRAY, EntitySkeletonStray.class, Race.UNDEAD, null, "STRAY", "ストレイ", (entity -> setMainHand(entity, new ItemStack(Material.BOW)))),
    CREEPER(EntityType.CREEPER, EntityCreeper.class, Race.UNDEAD, "クリーパー", LivingCreeper.class),
    SPIDER(EntityType.SPIDER, EntitySpider.class, Race.INSECT, null, "SPIDER", "クモ"),
    SPIDER_CAVE(EntityType.CAVE_SPIDER, EntityCaveSpider.class, Race.INSECT, null, "CAVE_SPIDER", "洞窟グモ"),
    SLIME(EntityType.SLIME, EntitySlime.class, Race.PUNI, null, "SLIME", "スライム"),
    ;

    @NotNull
    final EntityType type;
    @NotNull
    final Class<?> clasz;
    @NotNull
    final Race race;
    @Nullable
    final String body;
    @NotNull
    final String name;
    @Nullable
    final Consumer<Entity> initConsumer;
    @Nullable
    final Class<?> customClass;

    TargetEntityTypes(
            @NotNull EntityType type,
            @NotNull Class<? extends EntityLiving> clasz,
            @NotNull Race race,
            @Nullable String body,
            @Nullable String entityType,
            @NotNull String name
    ) {
        this.type = type;
        this.customClass = null;
        this.clasz = clasz;
        this.race = race;
        this.name = name;
        this.initConsumer = null;
        if (body != null) {
            this.body = body.replace("$NMS", "net.minecraft.server.v1_15_R1");
        } else if (entityType != null) {
            this.body = "{super(net.minecraft.server.v1_15_R1.EntityTypes." + entityType + ", (net.minecraft.server.v1_15_R1.World) $args[0]);}";
        } else {
            throw new IllegalStateException("not found body code.");
        }
    }

    TargetEntityTypes(
            @NotNull EntityType type,
            @NotNull Class<? extends EntityLiving> clasz,
            @NotNull Race race,
            @NotNull String name,
            @NotNull Class<?> customClass
    ) {
        this.type = type;
        this.clasz = clasz;
        this.race = race;
        this.name = name;
        this.initConsumer = null;
        this.body = null;
        this.customClass = customClass;
    }

    TargetEntityTypes(
            @NotNull EntityType type,
            @NotNull Class<? extends EntityLiving> clasz,
            @NotNull Race race,
            @Nullable String body,
            @Nullable String entityType,
            @NotNull String name,
            @Nullable Consumer<Entity> initConsumer
    ) {
        this.type = type;
        this.customClass = null;
        this.clasz = clasz;
        this.race = race;
        this.name = name;
        this.initConsumer = initConsumer;
        if (body != null) {
            this.body = body.replace("$NMS", "net.minecraft.server.v1_15_R1");
        } else if (entityType != null) {
            this.body = "{super(net.minecraft.server.v1_15_R1.EntityTypes." + entityType + ", (net.minecraft.server.v1_15_R1.World) $args[0]);}";
        } else {
            throw new IllegalStateException("not found body code.");
        }
    }

    @Nullable
    public static TargetEntityTypes search(EntityType type) {
        for (TargetEntityTypes tet : values()) {
            if (tet.type == type) {
                return tet;
            }
        }
        return null;
    }

    private static void setMainHand(Entity entity, ItemStack item) {
        if (entity instanceof LivingEntity) {
            final EntityEquipment equip = ((LivingEntity) entity).getEquipment();
            if (equip != null) {
                equip.setItemInMainHand(item);
            }
        }
    }
}
