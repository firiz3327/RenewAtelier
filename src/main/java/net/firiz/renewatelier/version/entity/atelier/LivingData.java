package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_15_R1.DamageSource;
import net.minecraft.server.v1_15_R1.EntityLiving;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
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

    LivingData(TargetEntityTypes types, EntityLiving wrapEntity) {
        Objects.requireNonNull(types);
        Objects.requireNonNull(wrapEntity);
        this.wrapEntity = wrapEntity;
        final double distance = getBukkitEntity().getLocation().distance(getBukkitEntity().getWorld().getSpawnLocation());
        final int mapLevel = (int) (Math.floor(distance) / 50);
        final int level = Math.max(1, mapLevel - 5 + Randomizer.nextInt(11));
        final int levelRate = level - 50; // 50lvあたりが一気に強くなる (atan)
        final int maxHp = BigDecimal.valueOf(((Math.PI / 2 - 0.375) + Math.atan(levelRate * 0.05)) * (7000 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int atk = BigDecimal.valueOf(((Math.PI / 2 - 0.5) + Math.atan(levelRate * 0.03)) * (360 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int def = BigDecimal.valueOf(((Math.PI / 2 - 0.77) + Math.atan(levelRate * 0.02)) * (200 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int speed = BigDecimal.valueOf(((Math.PI / 2 - 0.4) + Math.atan(levelRate * 0.02)) * (260 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final double hp = maxHp - (maxHp * (new Random().nextDouble() * 0.05));
        this.stats = new MonsterStats(types.race, level, maxHp, hp, atk, def, speed);
        init();
    }

    LivingData(Object wrapEntity, MonsterStats stats) {
        Objects.requireNonNull(wrapEntity);
        this.wrapEntity = (EntityLiving) wrapEntity;
        this.stats = stats;
        init();
    }

    private void init() {
        final LivingEntity bukkit = getBukkitEntity();
        final StringBuilder name = new StringBuilder(bukkit.getCustomName() == null ? bukkit.getName() : bukkit.getCustomName());
        if (hasStats()) {
            name.append(" Lv.").append(stats.getLevel());
            bukkit.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
            bukkit.setHealth(100);
        }
        bukkit.setCustomName(name.toString());
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

    public void damage(LivingEntity damager, double damage) {
        final LivingEntity bukkit = getBukkitEntity();
        if (hasStats()) {
            stats.setHp(Math.max(0, stats.getHp() - damage));
            bukkit.setHealth((stats.getHp() / stats.getMaxHp()) * 100);
        } else {
            bukkit.setHealth(Math.max(0, bukkit.getHealth() - damage));
        }
        bukkit.setLastDamageCause(new EntityDamageEvent(
                damager,
                EntityDamageEvent.DamageCause.CUSTOM,
                damage
        ));
    }

    /**
     * javassistで動的に生成されたEntityクラスからReflectionを用いて実行されます
     *
     * @param ds
     * @param f
     * @return entity.damageEntity
     */
    private boolean damageEntity(final Object ds, final Object f) {
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
        wrapEntity.noDamageTicks = 0;
        return result;
    }

}
