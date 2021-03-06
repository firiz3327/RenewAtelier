package net.firiz.renewatelier.version.nms;

import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.MinecraftVersion;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.EntityEffect;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class NMSEntityUtils {

    private NMSEntityUtils() {
    }

    @MinecraftVersion("1.16")
    public static void hurt(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager, @Nullable final DamageSource damageSource) {
        victim.playEffect(EntityEffect.HURT);

        // hurt play sound
        /*
        EntityLiving.class
        protected void c(DamageSource damagesource) {
            SoundEffect soundeffect = this.getSoundHurt(damagesource);
            if (soundeffect != null) {
                this.playSound(soundeffect, this.getSoundVolume(), this.dG());
            }
        }
         */
        final EntityLiving victimLiving = ((CraftLivingEntity) victim).getHandle();
        final Method c = VersionUtils.getMethod(EntityLiving.class, "c", DamageSource.class);
        try {
            c.invoke(victimLiving, damageSource == null ? DamageSource.a(((CraftEntity) damager).getHandle(), victimLiving) : damageSource);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CommonUtils.logWarning(e);
        }
    }

    @MinecraftVersion("1.16")
    public static void sweepParticle(@NotNull final Player player) {
        // find EntityHuman Particles.SWEEP_ATTACK
        ((CraftHumanEntity) player).getHandle().ex();
    }

    @MinecraftVersion("1.16")
    public static void knockBack(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager) {
        final int i = 1;
        final Entity nmsDamager = ((CraftEntity) damager).getHandle();
        ((CraftLivingEntity) victim).getHandle().a(
                (float) i * 0.5F,
                MathHelper.sin(nmsDamager.yaw * 0.017453292F),
                -MathHelper.cos(nmsDamager.yaw * 0.017453292F)
        );
    }

    @MinecraftVersion("1.16")
    public static void knockBackArrow(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager) {
        final Entity nmsDamager = ((CraftEntity) damager).getHandle();
        if (nmsDamager instanceof EntityArrow) {
            final EntityArrow arrow = (EntityArrow) nmsDamager;
            // EntityArrow.class { protected void a(MovingObjectPositionEntity movingobjectpositionentity) }
            if (arrow.knockbackStrength > 0) {
                final Vec3D vec3d = arrow.getMot().d(1.0D, 0.0D, 1.0D).d().a((double) arrow.knockbackStrength * 0.6D);
                if (vec3d.g() > 0.0D) {
                    ((CraftLivingEntity) victim).getHandle().h(vec3d.x, 0.1D, vec3d.z);
                }
            }
        }
    }

    @MinecraftVersion("1.16")
    public static boolean hasRecipe(@NotNull final Player player, @NotNull final String recipeId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(recipeId);
        String key;
        if (recipeId.startsWith("minecraft:")) {
            key = recipeId.substring(recipeId.indexOf(':') + 1);
        } else {
            key = recipeId;
        }
        return ((CraftPlayer) player).getHandle().getRecipeBook().hasDiscoveredRecipe(MinecraftKey.a(key));
    }

}
