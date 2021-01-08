package net.firiz.renewatelier.version.entity.atelier;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.entity.atelier.holo.AbstractHoloHealth;
import net.firiz.renewatelier.version.entity.atelier.holo.HoloBossHealth;
import net.firiz.renewatelier.version.entity.atelier.holo.HoloHealth;
import net.firiz.renewatelier.version.entity.drop.PlayerDropItem;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class LivingData {

    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final EntityLiving wrapEntity;
    private final MonsterStats stats;
    private final String name;
    private final Object2DoubleMap<Player> damageSources = new Object2DoubleOpenHashMap<>();

    private AbstractHoloHealth holoHealth;

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
        final int mapLevel = (int) (Math.floor(distance) / 100);
        final int level = Math.max(1, Randomizer.rand(mapLevel - 5, mapLevel + 5));
        final int levelRate = level - 50; // 50lvあたりが一気に強くなる (atan)
        final int maxHp = BigDecimal.valueOf(((Math.PI / 2 - 0.375) + Math.atan(levelRate * 0.05)) * (7000 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int atk = BigDecimal.valueOf(((Math.PI / 2 - 0.5) + Math.atan(levelRate * 0.03)) * (360 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int def = BigDecimal.valueOf(((Math.PI / 2 - 0.77) + Math.atan(levelRate * 0.02)) * (200 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int speed = BigDecimal.valueOf(((Math.PI / 2 - 0.4) + Math.atan(levelRate * 0.02)) * (260 / Math.PI)).setScale(0, RoundingMode.HALF_UP).intValue();
        final int exp = (int) (maxHp / 2.5);
        final double hp = maxHp - (maxHp * (Randomizer.nextDouble() * 0.05));
        this.stats = new MonsterStats(getBukkitEntity(), types.race, level, maxHp, hp, atk, def, speed, exp, false, types.resistances);
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
        if (hasStats() && Objects.requireNonNull(getStats()).isBoss()) {
            holoHealth = new HoloBossHealth(bukkit, this, displayName.toString());
        } else {
            holoHealth = new HoloHealth(bukkit, this, displayName.toString());
        }
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

    public AbstractHoloHealth getHoloHealth() {
        return holoHealth;
    }

    public String getName() {
        return name;
    }

    public Object2DoubleMap<Player> getDamageSources() {
        return new Object2DoubleOpenHashMap<>(damageSources);
    }

    public void damage(org.bukkit.entity.Entity damager, double damage) {
        if (damager instanceof Player) {
            if (damageSources.containsKey(damager)) {
                damageSources.put((Player) damager, damageSources.getDouble(damager) + damage);
            } else {
                damageSources.put((Player) damager, damage);
            }
        }
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
     * @param damageSource DamageSource 原因
     * @param damage       float ダメージ
     * @return entity.damageEntity
     */
    public boolean onDamageEntity(final Object damageSource, final Object damage) {
        final Map<Object, Class<?>> params = new Object2ObjectLinkedOpenHashMap<>();
        params.put(damageSource, DamageSource.class);
        params.put(damage, float.class);
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

    /**
     * javassistで動的に生成されたEntityクラスからReflectionを用いて実行されます
     *
     * @param damageSource DamageSource 原因
     * @return entity.die
     */
    public void onDie(Object damageSource) {
        final Map<Object, Class<?>> params = new Object2ObjectLinkedOpenHashMap<>();
        params.put(damageSource, DamageSource.class);
        VersionUtils.superInvoke(
                "die",
                wrapEntity,
                wrapEntity.getClass().getSuperclass(),
                void.class,
                params
        );
        if (damageSources.size() > 0) {
            final double size = damageSources.size();
            final double exp = stats.getExp() / size;
            final int expR = (int) (exp + (exp * (size - 1) * 0.8));
            for (final Player player : damageSources.keySet()) {
                psm.getChar(player.getUniqueId()).getCharStats().addExp(expR);
            }
        }
        holoHealth.die();
    }

    /**
     * javassistで動的に生成されたEntityクラスからReflectionを用いて実行されます
     *
     * @param damageSource DamageSource 原因
     * @param i            int 謎
     * @param flag         boolean 謎
     */
    public void dropDeathLoot(Object damageSource, Object i, Object flag) {
        // 機能を停止させる(今後、何かしら実装するかも)
    }

    public void drop(Location location, List<ItemStack> itemStacks) {
        location.setX(location.getX() + 0.5);
        location.setZ(location.getZ() + 0.5);
        damageSources.forEach((player, damage) -> itemStacks.forEach(item -> new PlayerDropItem(player, location, item).drop()));
    }

}
