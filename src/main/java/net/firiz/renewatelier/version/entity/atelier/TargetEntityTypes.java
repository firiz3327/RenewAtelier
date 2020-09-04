package net.firiz.renewatelier.version.entity.atelier;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.AttackResistance;
import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.MinecraftVersion;
import net.firiz.renewatelier.version.entity.atelier.vanilla.LivingCreeper;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Consumer;

public enum TargetEntityTypes {
    /**
     * UNDEAD
     */
    ZOMBIE(EntityType.ZOMBIE, EntityZombie.class, Race.UNDEAD, null, "ZOMBIE", "ゾンビ", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.SMALL),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )), // .
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, EntityZombieVillager.class, Race.UNDEAD, null, "ZOMBIE_VILLAGER", "村人ゾンビ", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.SMALL),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )), // .
    ZOMBIE_HUSK(EntityType.HUSK, EntityZombieHusk.class, Race.UNDEAD, null, "HUSK", "ハスク", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE),
            p(AttackAttribute.FIRE, AttackResistance.SMALL)
    )), // .
    ZOMBIE_DROWNED(EntityType.DROWNED, EntityDrowned.class, Race.UNDEAD, null, "DROWNED", "ドラウンド", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.SMALL)
    )), // .
    ZOMBIE_GIANT(EntityType.GIANT, EntityGiantZombie.class, Race.UNDEAD, null, "GIANT", "巨人", r(
            p(AttackAttribute.BLOW, AttackResistance.MEDIUM),
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )), // .
    // ZOMBIE_HORSE
    SKELETON(EntityType.SKELETON, EntitySkeleton.class, Race.UNDEAD, null, "SKELETON", "スケルトン",
            (entity -> setMainHand(entity, new ItemStack(Material.BOW))),
            r(
                    p(AttackAttribute.SLASH, AttackResistance.SMALL),
                    p(AttackAttribute.ABNORMAL, AttackResistance.SMALL),
                    p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
            )
    ), // .
    SKELETON_WITHER(EntityType.WITHER_SKELETON, EntitySkeletonWither.class, Race.UNDEAD, null, "WITHER_SKELETON", "ウィザースケルトン",
            (entity -> setMainHand(entity, new ItemStack(Material.STONE_SWORD))),
            r(
                    p(AttackAttribute.SLASH, AttackResistance.SMALL),
                    p(AttackAttribute.ABNORMAL, AttackResistance.SMALL),
                    p(AttackAttribute.FIRE, AttackResistance.MEDIUM)
            )
    ), // .
    SKELETON_STRAY(EntityType.STRAY, EntitySkeletonStray.class, Race.UNDEAD, null, "STRAY", "ストレイ",
            (entity -> setMainHand(entity, new ItemStack(Material.BOW))),
            r(
                    p(AttackAttribute.SLASH, AttackResistance.SMALL),
                    p(AttackAttribute.ABNORMAL, AttackResistance.SMALL),
                    p(AttackAttribute.FIRE, AttackResistance.WEAKNESS),
                    p(AttackAttribute.ICE, AttackResistance.LARGE),
                    p(AttackAttribute.LIGHTNING, AttackResistance.WEAKNESS)
            )
    ), // .
    // SKELETON_HORSE
    CREEPER(EntityType.CREEPER, EntityCreeper.class, Race.UNDEAD, "クリーパー", LivingCreeper.class, r(
            p(AttackAttribute.THRUST, AttackResistance.SMALL),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS),
            p(AttackAttribute.ICE, AttackResistance.WEAKNESS),
            p(AttackAttribute.LIGHTNING, AttackResistance.SUPER)
    )), // .
    // WITHER 生成時にブロックが壊れない
    PHANTOM(EntityType.PHANTOM, EntityPhantom.class, Race.UNDEAD, null, "PHANTOM", "ファントム", r(
            p(AttackAttribute.SLASH, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.MEDIUM),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )), // .
    GHAST(EntityType.GHAST, EntityGhast.class, Race.UNDEAD, null, "GHAST", "ガスト", r(
            p(AttackAttribute.ABNORMAL, AttackResistance.MEDIUM),
            p(AttackAttribute.FIRE, AttackResistance.LARGE)
    )), // .
    VEX(EntityType.VEX, EntityVex.class, Race.UNDEAD, null, "VEX", "ヴェックス",
            (entity -> setMainHand(entity, new ItemStack(Material.IRON_SWORD))),
            r(p(AttackAttribute.ABNORMAL, AttackResistance.WEAKNESS))
    ), // .
    ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, EntityPigZombie.class, Race.UNDEAD, null, "ZOMBIFIED_PIGLIN", "ゾンビピグリン",
            (entity -> setMainHand(entity, new ItemStack(Material.GOLDEN_SWORD))),
            r(
                    p(AttackAttribute.BLOW, AttackResistance.SMALL),
                    p(AttackAttribute.ABNORMAL, AttackResistance.MEDIUM),
                    p(AttackAttribute.FIRE, AttackResistance.MEDIUM),
                    p(AttackAttribute.ICE, AttackResistance.WEAKNESS)
            )
    ), // .
    ZOGLIN(EntityType.ZOGLIN, EntityZoglin.class, Race.UNDEAD, null, "ZOGLIN", "ゾグリン", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.MEDIUM),
            p(AttackAttribute.FIRE, AttackResistance.MEDIUM),
            p(AttackAttribute.ICE, AttackResistance.WEAKNESS)
    )), // .
    /**
     * HUMAN
     */
    // PLAYER
    // VILLAGER
    // WANDERING_TRADER
    /**
     * AJIN
     */
    WITCH(EntityType.WITCH, EntityWitch.class, Race.AJIN, null, "WITCH", "ウィッチ", r(
            p(AttackAttribute.ABNORMAL, AttackResistance.LARGE),
            p(AttackAttribute.LIGHTNING, AttackResistance.WEAKNESS)
    )), // .
    EVOKER(EntityType.EVOKER, EntityEvoker.class, Race.AJIN, null, "EVOKER", "エヴォーカー", r(
            p(AttackAttribute.ABNORMAL, AttackResistance.WEAKNESS)
    )), // .
    PILLAGER(EntityType.PILLAGER, EntityPillager.class, Race.AJIN, null, "PILLAGER", "ピリジャー",
            // 水中に完全に潜っているときは近接攻撃に切り替える
            (entity -> setMainHand(entity, new ItemStack(Material.CROSSBOW))),
            r(p(AttackAttribute.ABNORMAL, AttackResistance.WEAKNESS))
    ), // .
    VINDICATOR(EntityType.VINDICATOR, EntityVindicator.class, Race.AJIN, null, "VINDICATOR", "ヴィンディケーター",
            (entity -> setMainHand(entity, new ItemStack(Material.IRON_AXE))),
            r(p(AttackAttribute.ABNORMAL, AttackResistance.WEAKNESS))
    ), // .
    ILLUSIONER(EntityType.ILLUSIONER, EntityIllagerIllusioner.class, Race.AJIN, null, "ILLUSIONER", "イリュージョナー",
            (entity -> setMainHand(entity, new ItemStack(Material.BOW))),
            r(p(AttackAttribute.ABNORMAL, AttackResistance.WEAKNESS))
    ), // .
    PIGLIN(EntityType.PIGLIN, EntityPiglin.class, Race.AJIN, null, "PIGLIN", "ピグリン",
            (entity -> setMainHand(entity, new ItemStack(Material.CROSSBOW))),
            r(
                    p(AttackAttribute.BLOW, AttackResistance.SMALL),
                    p(AttackAttribute.ABNORMAL, AttackResistance.MEDIUM),
                    p(AttackAttribute.FIRE, AttackResistance.LARGE),
                    p(AttackAttribute.ICE, AttackResistance.WEAKNESS)
            )
    ), // .
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, EntityPiglinBrute.class, Race.AJIN, null, "PIGLIN_BRUTE", "ピグリンブルート",
            (entity -> setMainHand(entity, new ItemStack(Material.GOLDEN_AXE))),
            r(
                    p(AttackAttribute.BLOW, AttackResistance.SMALL),
                    p(AttackAttribute.ABNORMAL, AttackResistance.MEDIUM),
                    p(AttackAttribute.FIRE, AttackResistance.LARGE),
                    p(AttackAttribute.ICE, AttackResistance.WEAKNESS)
            )
    ),
    /**
     * INSECT
     */
    SPIDER(EntityType.SPIDER, EntitySpider.class, Race.INSECT, null, "SPIDER", "クモ", r(
            p(AttackAttribute.ABNORMAL, AttackResistance.SMALL),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )), // .
    SPIDER_CAVE(EntityType.CAVE_SPIDER, EntityCaveSpider.class, Race.INSECT, null, "CAVE_SPIDER", "洞窟グモ", r(
            p(AttackAttribute.ABNORMAL, AttackResistance.SMALL),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )), // .
    SILVERFISH(EntityType.SILVERFISH, EntitySilverfish.class, Race.INSECT, null, "SILVERFISH", "シルバーフィッシュ", r(
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS)
    )), // .
    // BEE
    /**
     * MAGIC
     */
    ENDERMAN(EntityType.ENDERMAN, EntityEnderman.class, Race.MAGIC, null, "ENDERMAN", "エンダーマン", r(
            p(AttackAttribute.ICE, AttackResistance.WEAKNESS),
            p(AttackAttribute.FIRE, AttackResistance.MEDIUM),
            p(AttackAttribute.LIGHTNING, AttackResistance.WEAKNESS),
            p(AttackAttribute.THRUST, AttackResistance.SUPER),
            p(AttackAttribute.MAGIC, AttackResistance.SMALL)
    )), // .
    ENDERMITE(EntityType.ENDERMITE, EntityEndermite.class, Race.MAGIC, null, "ENDERMITE", "エンダーマイト", r(
            p(AttackAttribute.ICE, AttackResistance.WEAKNESS)
    )), // .
    SHULKER(EntityType.SHULKER, EntityShulker.class, Race.MAGIC, null, "SHULKER", "シュルカー", r(
            p(AttackAttribute.ICE, AttackResistance.WEAKNESS),
            p(AttackAttribute.FIRE, AttackResistance.MEDIUM),
            p(AttackAttribute.LIGHTNING, AttackResistance.SMALL),
            p(AttackAttribute.THRUST, AttackResistance.MEDIUM),
            p(AttackAttribute.MAGIC, AttackResistance.MEDIUM)
    )), // .
    BLAZE(EntityType.BLAZE, EntityBlaze.class, Race.MAGIC, null, "BLAZE", "ブレイズ", r(
            p(AttackAttribute.FIRE, AttackResistance.SUPER),
            p(AttackAttribute.ICE, AttackResistance.WEAKNESS),
            p(AttackAttribute.LIGHTNING, AttackResistance.SMALL)
    )), // .
    SNOW_GOLEM(EntityType.SNOWMAN, EntitySnowman.class, Race.MAGIC, null, "SNOW_GOLEM", "スノーゴーレム", r(
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS),
            p(AttackAttribute.ICE, AttackResistance.MEDIUM),
            p(AttackAttribute.LIGHTNING, AttackResistance.WEAKNESS)
    )), // . 生成時にブロックが壊れない
    IRON_GOLEM(EntityType.IRON_GOLEM, EntityIronGolem.class, Race.MAGIC, null, "IRON_GOLEM", "アイアンゴーレム", r(
            p(AttackAttribute.SLASH, AttackResistance.LARGE),
            p(AttackAttribute.BLOW, AttackResistance.LARGE),
            p(AttackAttribute.THRUST, AttackResistance.MEDIUM),
            p(AttackAttribute.FIRE, AttackResistance.WEAKNESS),
            p(AttackAttribute.ICE, AttackResistance.SMALL),
            p(AttackAttribute.LIGHTNING, AttackResistance.SMALL)
    )), // . 生成時にブロックが壊れない
    // STRIDER
    /**
     * PUNI
     */
    SLIME(EntityType.SLIME, EntitySlime.class, Race.PUNI, null, "SLIME", "スライム", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.SLASH, AttackResistance.SMALL),
            p(AttackAttribute.THRUST, AttackResistance.SMALL)
    )), // .　originalクラスにしてサイズにランダム性を持たせた方が良い
    MAGMA_CUBE(EntityType.MAGMA_CUBE, EntityMagmaCube.class, Race.PUNI, null, "MAGMA_CUBE", "マグマキューブ", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.SLASH, AttackResistance.SMALL),
            p(AttackAttribute.THRUST, AttackResistance.SMALL),
            p(AttackAttribute.FIRE, AttackResistance.LARGE)
    )), // . originalクラスにしてサイズにランダム性を持たせた方が良い
    /**
     * FISH
     */
    // COD
    // PUFFERFISH
    // SALMON
    // TROPICAL_FISH
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, EntityGuardianElder.class, Race.FISH, null, "ELDER_GUARDIAN", "エルダーガーディアン", r(
            p(AttackAttribute.THRUST, AttackResistance.MEDIUM),
            p(AttackAttribute.FIRE, AttackResistance.MEDIUM),
            p(AttackAttribute.ICE, AttackResistance.MEDIUM),
            p(AttackAttribute.LIGHTNING, AttackResistance.WEAKNESS)
    )), // .
    GUARDIAN(EntityType.GUARDIAN, EntityGuardian.class, Race.FISH, null, "GUARDIAN", "ガーディアン", r(
            p(AttackAttribute.THRUST, AttackResistance.MEDIUM),
            p(AttackAttribute.FIRE, AttackResistance.MEDIUM),
            p(AttackAttribute.ICE, AttackResistance.MEDIUM),
            p(AttackAttribute.LIGHTNING, AttackResistance.WEAKNESS)
    )), // .
    // SQUID
    // TURTLE
    // DOLPHIN
    /**
     * ANIMAL
     */
    // CAT
    // OCELOT
    // PARROT
    // CHICKEN
    // COW
    // MUSHROOM_COW
    // PIG
    // FOX
    // SHEEP
    // HORSE
    // DONKEY
    // MULE
    // RABBIT
    // LLAMA
    // PANDA
    // BAT
    /**
     * BEAST
     */
    // WOLF
    // POLAR_BEAR
    HOGLIN(EntityType.HOGLIN, EntityHoglin.class, Race.ANIMAL, null, "HOGLIN", "ホグリン", r(
            p(AttackAttribute.BLOW, AttackResistance.SMALL),
            p(AttackAttribute.ABNORMAL, AttackResistance.MEDIUM),
            p(AttackAttribute.FIRE, AttackResistance.LARGE),
            p(AttackAttribute.ICE, AttackResistance.WEAKNESS)
    )), // .
    /**
     * DRAGON
     */
    // ENDER_DRAGON
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

    @Nullable
    final String nmsEntityType;

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

    @MinecraftVersion("1.16")
    TargetEntityTypes(
            @NotNull EntityType type,
            @NotNull Class<? extends EntityLiving> clasz,
            @NotNull Race race,
            @Nullable String body,
            @Nullable String entityType,
            @NotNull String name,
            @Nullable Consumer<Entity> initConsumer,
            @NotNull Object2ObjectMap<AttackAttribute, AttackResistance> resistances
    ) {
        this.type = type;
        this.customClass = null;
        this.clasz = clasz;
        this.race = race;
        this.name = name;
        this.initConsumer = initConsumer;
        this.resistances = resistances;
        this.nmsEntityType = entityType;
        if (body != null) {
            this.body = body.replace("$NMS", "net.minecraft.server.v1_16_R2");
        } else if (entityType != null) {
            this.body = "{super(net.minecraft.server.v1_16_R2.EntityTypes." + entityType + ", (net.minecraft.server.v1_16_R2.World) $args[0]);}";
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
        this.nmsEntityType = null;
    }

    public static void check() {
        final World world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        Arrays.stream(values()).filter(a -> a.customClass == null && a.nmsEntityType != null).forEach(a -> {
            try {
                final EntityTypes<?> type = (EntityTypes<?>) EntityTypes.class.getField(a.nmsEntityType).get(null);
                a.clasz.getConstructor(EntityTypes.class, World.class).newInstance(type, world);
            } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                CommonUtils.logWarning("not checked " + a.nmsEntityType + ".", e);
            }
        });
        CommonUtils.log("TargetEntityTypes check finished.");
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
