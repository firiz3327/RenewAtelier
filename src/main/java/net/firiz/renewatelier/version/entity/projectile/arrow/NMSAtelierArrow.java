package net.firiz.renewatelier.version.entity.projectile.arrow;

import com.google.common.base.Preconditions;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.entity.arrow.AtelierTippedArrow;
import net.firiz.renewatelier.version.nms.VItemStack;
import net.minecraft.server.v1_14_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftVector;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NMSAtelierArrow extends EntityTippedArrow implements IAtelierArrow {

    private CraftEntity bukkitEntity;
    private final Location location;
    private final VItemStack bow;
    private final ItemStack arrow;
    private final LivingEntity source;
    private Vector velocity;

    /**
     * 使用アイテム（弓）データを保持した矢を生成します
     *
     * @param location 発射地点
     * @param bow      使用アイテム(弓)
     * @param arrow    使用アイテム(矢)
     * @param source   射撃者
     */
    public NMSAtelierArrow(@NotNull Location location, @NotNull ItemStack bow, @NotNull ItemStack arrow, @NotNull LivingEntity source) {
        super(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle(), ((CraftLivingEntity) source).getHandle());
        this.location = location;
        this.bow = VersionUtils.asVItemCopy(bow);
        this.arrow = arrow;
        this.source = source;
    }

    @Override
    public void shoot(@Nullable Vector velocity) {
        this.velocity = velocity;
        world.addEntity(this);
        shoot(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    @Override
    public void shoot(double x, double y, double z, float pitch, float yaw) {
        if (velocity == null) {
            super.shoot(x, y, z, pitch, yaw);
        }
        final Vec3D vec3d = CraftVector.toNMS(velocity);
        this.setMot(vec3d);
        float f2 = MathHelper.sqrt(b(vec3d));
        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.d(vec3d.y, f2) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.despawnCounter = 0;
    }

    @Override
    @NotNull
    public LivingEntity getSource() {
        return source;
    }

    @Override
    @NotNull
    public ItemStack getBow() {
        return bow.getItem();
    }

    @Override
    @NotNull
    public ItemStack getArrow() {
        return arrow;
    }

    @Override
    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof org.bukkit.entity.Entity) {
            super.setShooter(((CraftEntity)shooter).getHandle());
        } else {
            super.setShooter(null);
        }

        projectileSource = shooter;
    }

    @Override
    public void setPickupStatus(AbstractArrow.PickupStatus status) {
        Preconditions.checkNotNull(status, "status");
        fromPlayer = net.minecraft.server.v1_14_R1.EntityArrow.PickupStatus.a(status.ordinal());
    }

    @Override
    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    @Override
    public void setKnockbackStrength(int knockbackStrength) {
        this.knockbackStrength = knockbackStrength;
    }

    @Override
    public void setPierceLevel(int pierceLevel) {
        super.setPierceLevel((byte) pierceLevel);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return arrow;
    }

    @Override
    public void setVelocity(@NotNull Vector velocity) {
        velocity.checkFinite();
        this.setMot(CraftVector.toNMS(velocity));
        this.velocityChanged = true;
    }

    public void setBasePotionData(PotionData data) {
        Validate.notNull(data, "PotionData cannot be null");
        setType(CraftPotionUtil.fromBukkit(data));
    }

    public void setColor(Color color) {
        super.setColor(color.asRGB());
    }

    public boolean addCustomEffect(PotionEffect effect, boolean override) {
        int effectId = effect.getType().getId();
        MobEffect existing = null;
        for (MobEffect mobEffect : effects) {
            if (MobEffectList.getId(mobEffect.getMobEffect()) == effectId) {
                existing = mobEffect;
            }
        }
        if (existing != null) {
            if (!override) {
                return false;
            }
            effects.remove(existing);
        }
        addEffect(CraftPotionUtil.fromBukkit(effect));
        refreshEffects();
        return true;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new AtelierTippedArrow(
                    this.world.getServer(),
                    this,
                    source,
                    bow.getItem(),
                    arrow
            );
        }

        return bukkitEntity;
    }
}
