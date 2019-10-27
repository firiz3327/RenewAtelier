package net.firiz.renewatelier.version.entity.projectile;

import com.google.common.base.Preconditions;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.nms.VItemStack;
import net.minecraft.server.v1_14_R1.EntityArrow;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftVector;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtelierArrow extends EntityArrow implements AtelierProjectile {

    private final Location location;
    private final VItemStack itemStack;
    private final ItemStack arrow;
    private final LivingEntity source;
    private Vector velocity;

    /**
     * 使用アイテム（弓）データを保持した矢を生成します
     *
     * @param location 発射地点
     * @param itemStack 使用アイテム（弓）
     * @param source 射撃者
     */
    public AtelierArrow(@NotNull Location location, @NotNull ItemStack itemStack, @NotNull LivingEntity source) {
        super(EntityTypes.ARROW, ((CraftLivingEntity) source).getHandle(), ((CraftWorld) location.getWorld()).getHandle());
        this.location = location;
        this.itemStack = VersionUtils.asVItemCopy(itemStack);
        this.arrow = new ItemStack(Material.ARROW);
        this.source = source;
    }

    /**
     * 使用アイテム（弓）データを保持した矢を生成します
     *
     * @param location 発射地点
     * @param itemStack 使用アイテム（弓）
     * @param source 射撃者
     */
    public AtelierArrow(@NotNull Location location, @NotNull ItemStack itemStack, @NotNull ItemStack arrow, @NotNull LivingEntity source) {
        super(EntityTypes.ARROW, ((CraftLivingEntity) source).getHandle(), ((CraftWorld) location.getWorld()).getHandle());
        this.location = location;
        this.itemStack = VersionUtils.asVItemCopy(itemStack);
        this.arrow = arrow;
        this.source = source;
    }

    @Override
    @NotNull
    protected net.minecraft.server.v1_14_R1.ItemStack getItemStack() {
        return (net.minecraft.server.v1_14_R1.ItemStack) itemStack.getNmsItem();
    }

    @Override
    public void shoot(@Nullable Vector velocity) {
        this.velocity = velocity;
        this.shoot(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
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
        return itemStack.getItem();
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

}
