package net.firiz.renewatelier.version.entity.projectile.arrow;

import net.firiz.ateliercommonapi.nms.item.NMSItemStack;
import net.firiz.renewatelier.entity.arrow.AtelierAbstractArrow;
import net.firiz.renewatelier.entity.arrow.AtelierSpectralArrow;
import net.minecraft.world.entity.projectile.EntitySpectralArrow;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NMSAtelierSpectralArrow extends EntitySpectralArrow implements INMSAtelierArrow {

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
    public NMSAtelierSpectralArrow(@NotNull Location location, @NotNull ItemStack bow, @NotNull ItemStack arrow, @NotNull LivingEntity source, float force, boolean isSkill) {
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
        return arrow.itemStack();
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new AtelierSpectralArrow(
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
    public AtelierAbstractArrow getAtelierArrowEntity() {
        return (AtelierAbstractArrow) getBukkitEntity();
    }

    @Override
    public net.minecraft.world.item.ItemStack getItemStack() {
        return arrow.nms();
    }
}
