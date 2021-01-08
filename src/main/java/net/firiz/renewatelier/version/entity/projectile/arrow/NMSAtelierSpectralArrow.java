package net.firiz.renewatelier.version.entity.projectile.arrow;

import com.google.common.base.Preconditions;
import net.firiz.renewatelier.entity.arrow.AtelierAbstractArrow;
import net.firiz.renewatelier.entity.arrow.AtelierSpectralArrow;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.nms.VItemStack;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NMSAtelierSpectralArrow extends EntitySpectralArrow implements IAtelierArrow {

    private CraftEntity bukkitEntity;
    private final Location location;
    private final VItemStack bow;
    private final VItemStack arrow;
    private final LivingEntity source;
    private final float force;
    private final boolean isSkill;
    private Vector velocity;

    /**
     * 使用アイテム（弓）データを保持した矢を生成します
     *
     * @param location 発射地点
     * @param bow      使用アイテム(弓)
     * @param arrow    使用アイテム(矢)
     * @param source   射撃者
     * @param force    弓のチャージ
     * @param isSkill  スキルであるか
     */
    public NMSAtelierSpectralArrow(@NotNull Location location, @NotNull ItemStack bow, @NotNull ItemStack arrow, @NotNull LivingEntity source, float force, boolean isSkill) {
        super(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle(), ((CraftLivingEntity) source).getHandle());
        this.location = location;
        this.bow = VersionUtils.asVItemCopy(bow);
        this.arrow = VersionUtils.asVItemCopy(arrow);
        this.source = source;
        this.force = force;
        this.isSkill = isSkill;
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
        } else {
            NMSAtelierTippedArrow.s(this, velocity);
        }
    }

    @Override
    @NotNull
    public LivingEntity getSource() {
        return source;
    }

    @Override
    @NotNull
    public org.bukkit.inventory.ItemStack getBow() {
        return bow.getItem();
    }

    @Override
    @NotNull
    public org.bukkit.inventory.ItemStack getArrow() {
        return arrow.getItem();
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @Override
    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof org.bukkit.entity.Entity) {
            super.setShooter(((CraftEntity) shooter).getHandle());
        } else {
            super.setShooter(null);
        }

        projectileSource = shooter;
    }

    @Override
    public void setPickupStatus(AbstractArrow.PickupStatus status) {
        Preconditions.checkNotNull(status, "status");
        fromPlayer = net.minecraft.server.v1_16_R3.EntityArrow.PickupStatus.a(status.ordinal());
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
        return arrow.getItem();
    }

    @Override
    public void setVelocity(@NotNull Vector velocity) {
        velocity.checkFinite();
        this.setMot(CraftVector.toNMS(velocity));
        this.velocityChanged = true;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new AtelierSpectralArrow(
                    this.world.getServer(),
                    this,
                    source,
                    bow.getItem(),
                    arrow.getItem(),
                    force,
                    isSkill
            );
        }

        return bukkitEntity;
    }

    @Override
    public AtelierAbstractArrow getAtelierArrowEntity() {
        return (AtelierAbstractArrow) getBukkitEntity();
    }

    @Override
    protected net.minecraft.server.v1_16_R3.ItemStack getItemStack() {
        return (net.minecraft.server.v1_16_R3.ItemStack) arrow.getNmsItem();
    }
}
