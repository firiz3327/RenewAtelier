package net.firiz.renewatelier.version;

import net.firiz.renewatelier.utils.Chore;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.EntityEffect;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class NMSEntityUtils {

    private NMSEntityUtils() {
    }

    public static boolean isDead(@NotNull final org.bukkit.entity.Entity entity) {
        return entity.isDead() || (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0);
    }

    @MinecraftVersion("1.15")
    public static void hurt(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager, @Nullable final DamageSource damageSource) {
        victim.playEffect(EntityEffect.HURT);

        // hurt play sound
        final EntityLiving victimLiving = ((CraftLivingEntity) victim).getHandle();
        final Method c = VersionUtils.getMethod(EntityLiving.class, "c", DamageSource.class);
        try {
            c.invoke(victimLiving, damageSource == null ? DamageSource.a(((CraftEntity) damager).getHandle(), victimLiving) : damageSource);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Chore.logWarning(e);
        }
    }

    @MinecraftVersion("1.15")
    public static void sweepParticle(@NotNull final Player player) {
        ((CraftHumanEntity) player).getHandle().ea();
    }

    @MinecraftVersion("1.15")
    public static void knockBack(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager) {
        final int i = 1;
        final Entity nmsDamager = ((CraftEntity) damager).getHandle();
        ((CraftLivingEntity) victim).getHandle().a(
                nmsDamager,
                (float) i * 0.5F,
                MathHelper.sin(nmsDamager.yaw * 0.017453292F),
                -MathHelper.cos(nmsDamager.yaw * 0.017453292F)
        );
    }

    @MinecraftVersion("1.15")
    public static void knockBackArrow(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager) {
        final Entity nmsDamager = ((CraftEntity) damager).getHandle();
        if (nmsDamager instanceof EntityArrow) {
            final EntityArrow arrow = (EntityArrow) nmsDamager;
            final Vec3D vec3d = arrow.getMot().d(1.0D, 0.0D, 1.0D).d().a((double) arrow.knockbackStrength * 0.6D);
            if (vec3d.g() > 0.0D) {
                ((CraftLivingEntity) victim).getHandle().h(vec3d.x, 0.1D, vec3d.z);
            }
        }
    }
}
