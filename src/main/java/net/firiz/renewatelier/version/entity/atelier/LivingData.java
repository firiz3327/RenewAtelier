package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class LivingData {

    private final EntityLiving wrapEntity;
    private final MonsterStats stats;
    private final String name;

    private HoloHealth holoHealth;

    public LivingData(TargetEntityTypes types, EntityLiving wrapEntity, Location location) {
        this(types, wrapEntity, location, null);
    }

    public LivingData(TargetEntityTypes types, EntityLiving wrapEntity, Location location, String name) {
        Objects.requireNonNull(types);
        Objects.requireNonNull(wrapEntity);
        this.wrapEntity = wrapEntity;
        this.wrapEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.name = name == null && wrapEntity.hasCustomName() ? getBukkitEntity().getCustomName() : types.name;
        final double distance = location.distance(getBukkitEntity().getWorld().getSpawnLocation());
        final int mapLevel = (int) (Math.floor(distance) / 50);
        final int level = Math.max(1, mapLevel - 5 + Randomizer.nextInt(11));
        final int levelRate = level - 50; // 50lvあたりが一気に強くなる (atan)
        final int maxHp = BigDecimal.valueOf(((Math.PI / 2 - 0.375) + Math.atan(levelRate * 0.05)) * (7000 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int atk = BigDecimal.valueOf(((Math.PI / 2 - 0.5) + Math.atan(levelRate * 0.03)) * (360 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int def = BigDecimal.valueOf(((Math.PI / 2 - 0.77) + Math.atan(levelRate * 0.02)) * (200 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int speed = BigDecimal.valueOf(((Math.PI / 2 - 0.4) + Math.atan(levelRate * 0.02)) * (260 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final double hp = maxHp - (maxHp * (new Random().nextDouble() * 0.05));
        this.stats = new MonsterStats(getBukkitEntity(), types.race, level, maxHp, hp, atk, def, speed);
        init();
    }

    public LivingData(TargetEntityTypes types, Object wrapEntity, MonsterStats stats) {
        this(types, wrapEntity, stats, null);
    }

    public LivingData(TargetEntityTypes types, Object wrapEntity, MonsterStats stats, String name) {
        Objects.requireNonNull(wrapEntity);
        this.wrapEntity = (EntityLiving) wrapEntity;
        this.stats = stats;
        this.name = name == null && this.wrapEntity.hasCustomName() ? getBukkitEntity().getCustomName() : types.name;
        init();
    }

    private void init() {
        final LivingEntity bukkit = getBukkitEntity();
        final StringBuilder displayName = new StringBuilder(this.name);
        if (hasStats()) {
            displayName.append(" Lv.").append(stats.getLevel());
            bukkit.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
            bukkit.setHealth(100);
        }
        wrapEntity.setCustomNameVisible(false);
        holoHealth = new HoloHealth(wrapEntity, displayName.toString());
    }

    @NotNull
    public EntityLiving getNMSEntity() {
        return wrapEntity;
    }

    @NotNull
    public LivingEntity getBukkitEntity() {
        return (LivingEntity) wrapEntity.getBukkitEntity();
    }

    public boolean hasStats() {
        return stats != null;
    }

    @Nullable
    public MonsterStats getStats() {
        return stats;
    }

    public void damage(org.bukkit.entity.Entity damager, double damage) {
        final LivingEntity bukkit = getBukkitEntity();
        if (hasStats()) {
            stats.setHp(Math.max(0, stats.getHp() - damage));
            bukkit.setHealth((stats.getHp() / stats.getMaxHp()) * 100);
        } else {
            bukkit.setHealth(Math.max(0, bukkit.getHealth() - damage));
        }
        bukkit.setLastDamageCause(damager == null ? new EntityDamageEvent(
                wrapEntity.getBukkitEntity(),
                EntityDamageEvent.DamageCause.CUSTOM,
                damage
        ) : new EntityDamageByEntityEvent(
                wrapEntity.getBukkitEntity(),
                damager,
                EntityDamageEvent.DamageCause.CUSTOM,
                damage
        ));
    }

    /**
     * javassistで動的に生成されたEntityクラスからReflectionを用いて実行されます
     *
     * @param ds DamageSource
     * @param f  float ダメージ
     * @return entity.damageEntity
     */
    public boolean damageEntity(final Object ds, final Object f) {
        final Map<Object, Class<?>> params = new LinkedHashMap<>();
        params.put(ds, DamageSource.class);
        params.put(f, float.class);
        final boolean result = Objects.requireNonNull(VersionUtils.superInvoke(
                "damageEntity",
                wrapEntity,
                wrapEntity.getClass().getSuperclass(),
                boolean.class,
                params
        ));
        if (wrapEntity.getHealth() > 0 && (!hasStats() || stats.getHp() > 0)) {
            wrapEntity.noDamageTicks = 0;
            holoHealth.holo();
        }
        return result;
    }

}
