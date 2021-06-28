package net.firiz.renewatelier.version.entity.projectile.arrow;

import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.ateliercommonapi.nms.item.NMSItemStack;
import net.firiz.renewatelier.entity.arrow.AtelierTippedArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NMSAtelierTippedArrow extends EntityTippedArrow implements INMSAtelierArrow {

    private CraftEntity bukkitEntity;
    private final Location location;
    private final NMSItemStack bow;
    private final NMSItemStack arrow;
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
    public NMSAtelierTippedArrow(@NotNull Location location, @NotNull ItemStack bow, @NotNull ItemStack arrow, @NotNull LivingEntity source, float force, boolean isSkill) {
        super(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle(), ((CraftLivingEntity) source).getHandle());
        this.location = location;
        this.bow = NMSItemStack.convert(bow);
        this.arrow = NMSItemStack.convert(arrow);
        this.source = source;
        this.force = force;
        this.isSkill = isSkill;
    }

    @Override
    public void shoot(@Nullable Vector velocity) {
        this.velocity = velocity;
        getWorld().addEntity(this);
        shoot(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    @Override
    public void shoot(double x, double y, double z, float pitch, float yaw) {
        if (velocity == null) {
            super.shoot(x, y, z, pitch, yaw);
        } else {
            s(this, velocity);
        }
    }

    /**
     * EntityArrowのtickのMathHelper.sqrt(c(vec3d))辺りを参照
     */
    @MinecraftVersion("1.17")
    protected static void s(@NotNull EntityArrow arrow, @NotNull Vector velocity) {
        final Vec3D vec3d = CraftVector.toNMS(velocity);
        arrow.setMot(vec3d);
        final double d0 = vec3d.h();
        arrow.setYRot((float) (MathHelper.d(vec3d.b, vec3d.d) * 57.2957763671875D));
        arrow.setXRot((float) (MathHelper.d(vec3d.c, d0) * 57.2957763671875D));
        arrow.x = arrow.getYRot(); // lastYaw
        arrow.y = arrow.getXRot(); // lastPitch
    }

    @Override
    @NotNull
    public LivingEntity getSource() {
        return source;
    }

    @Override
    @NotNull
    public ItemStack getBow() {
        return bow.itemStack();
    }

    @Override
    @NotNull
    public ItemStack getArrow() {
        return arrow.itemStack();
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return getArrow();
    }

    @Override
    public @NotNull CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new AtelierTippedArrow(
                    getWorld().getCraftServer(),
                    this,
                    source,
                    bow.itemStack(),
                    arrow.itemStack(),
                    force,
                    isSkill
            );
        }

        return bukkitEntity;
    }

    @Override
    public AtelierTippedArrow getAtelierArrowEntity() {
        return (AtelierTippedArrow) getBukkitEntity();
    }

    @Override
    public @NotNull net.minecraft.world.item.ItemStack getItemStack() {
        return arrow.nms();
    }
}
