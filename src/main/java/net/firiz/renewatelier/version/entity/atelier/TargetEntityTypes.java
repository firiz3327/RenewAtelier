package net.firiz.renewatelier.version.entity.atelier;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.AttackResistance;
import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.version.MinecraftVersion;
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

import java.util.Arrays;
import java.util.function.Consumer;

public enum TargetEntityTypes {
    ZOMBIE(EntityType.ZOMBIE, EntityZombie.class, Race.UNDEAD, null, "ZOMBIE", "ゾンビ", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )),
    ZOMBIE_PIG(EntityType.PIG_ZOMBIE, EntityPigZombie.class, Race.UNDEAD, null, "ZOMBIE_PIGMAN", "ゾンビピッグマン", (entity -> setMainHand(entity, new ItemStack(Material.GOLDEN_SWORD))), r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE)
    )),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, EntityZombieVillager.class, Race.UNDEAD, null, "ZOMBIE_VILLAGER", "村人ゾンビ", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )),
    ZOMBIE_HUSK(EntityType.HUSK, EntityZombieHusk.class, Race.UNDEAD, null, "HUSK", "ハスク", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE)
    )),
    ZOMBIE_GIANT(EntityType.GIANT, EntityGiantZombie.class, Race.UNDEAD, null, "GIANT", "巨人", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )),
    SKELETON(EntityType.SKELETON, EntitySkeleton.class, Race.UNDEAD, null, "SKELETON", "スケルトン", (entity -> setMainHand(entity, new ItemStack(Material.BOW))), r(
            p(AttackAttribute.SLASH, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )),
    SKELETON_WITHER(EntityType.WITHER_SKELETON, EntitySkeletonWither.class, Race.UNDEAD, null, "WITHER_SKELETON", "ウィザースケルトン", (entity -> setMainHand(entity, new ItemStack(Material.STONE_SWORD))), r(
            p(AttackAttribute.SLASH, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE)
    )),
    SKELETON_STRAY(EntityType.STRAY, EntitySkeletonStray.class, Race.UNDEAD, null, "STRAY", "ストレイ", (entity -> setMainHand(entity, new ItemStack(Material.BOW))), r(
            p(AttackAttribute.SLASH, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE)
    )),
    CREEPER(EntityType.CREEPER, EntityCreeper.class, Race.UNDEAD, "クリーパー", LivingCreeper.class, r(
            p(AttackAttribute.THRUST, AttackResistance.SMALL),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS),
            p(AttackAttribute.LIGHTNING, AttackResistance.SUPER)
    )),
    SPIDER(EntityType.SPIDER, EntitySpider.class, Race.INSECT, null, "SPIDER", "クモ", r(
            p(AttackAttribute.ABNORMAL, AttackResistance.SMALL)
    )),
    SPIDER_CAVE(EntityType.CAVE_SPIDER, EntityCaveSpider.class, Race.INSECT, null, "CAVE_SPIDER", "洞窟グモ", r(
            p(AttackAttribute.ABNORMAL, AttackResistance.SMALL)
    )),
    SLIME(EntityType.SLIME, EntitySlime.class, Race.PUNI, null, "SLIME", "スライム", r()),
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
    @NotNull
    final Object2ObjectMap<AttackAttribute, AttackResistance> resistances;

    TargetEntityTypes(
            @NotNull EntityType type,
            @NotNull Class<? extends EntityLiving> clasz,
            @NotNull Race race,
            @Nullable String body,
            @Nullable String entityType,
            @NotNull String name,
            @NotNull Object2ObjectMap<AttackAttribute, AttackResistance> resistances) {
        this(type, clasz, race, body, entityType, name, null, resistances);
    }

    @MinecraftVersion("1.15")
    TargetEntityTypes(
            @NotNull EntityType type,
            @NotNull Class<? extends EntityLiving> clasz,
            @NotNull Race race,
            @Nullable String body,
            @Nullable String entityType,
            @NotNull String name,
            @Nullable Consumer<Entity> initConsumer,
            @NotNull Object2ObjectMap<AttackAttribute, AttackResistance> resistances) {
        this.type = type;
        this.customClass = null;
        this.clasz = clasz;
        this.race = race;
        this.name = name;
        this.initConsumer = initConsumer;
        this.resistances = resistances;
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
            @NotNull Class<?> customClass,
            @NotNull Object2ObjectMap<AttackAttribute, AttackResistance> resistances) {
        this.type = type;
        this.clasz = clasz;
        this.race = race;
        this.name = name;
        this.initConsumer = null;
        this.body = null;
        this.customClass = customClass;
        this.resistances = resistances;
    }

    @Nullable
    public static TargetEntityTypes search(@NotNull final EntityType type) {
        return Arrays.stream(values()).filter(tet -> tet.type == type).findFirst().orElse(null);
    }

    private static void setMainHand(Entity entity, ItemStack item) {
        if (entity instanceof LivingEntity) {
            final EntityEquipment equip = ((LivingEntity) entity).getEquipment();
            if (equip != null) {
                equip.setItemInMainHand(item);
            }
        }
    }

    private static P p(@NotNull final AttackAttribute attribute, @NotNull final AttackResistance resistance) {
        return new P(attribute, resistance);
    }

    private static Object2ObjectMap<AttackAttribute, AttackResistance> r(@NotNull final P... p) {
        final Object2ObjectMap<AttackAttribute, AttackResistance> r = new Object2ObjectOpenHashMap<>();
        Arrays.stream(p).forEach(pair -> r.put(pair.attribute, pair.resistance));
        return r;
    }

    private static class P {
        private final AttackAttribute attribute;
        private final AttackResistance resistance;

        private P(AttackAttribute attribute, AttackResistance resistance) {
            this.attribute = attribute;
            this.resistance = resistance;
        }
    }

}
