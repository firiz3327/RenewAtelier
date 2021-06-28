package net.firiz.renewatelier.version.nms;

import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.EntityEffect;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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

    @MinecraftVersion("1.17")
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
        final Method c = VersionUtils.getMethod(EntityLiving.class, "d", DamageSource.class);
        try {
            c.invoke(victimLiving, damageSource == null ? DamageSource.a(((CraftEntity) damager).getHandle(), victimLiving) : damageSource);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CommonUtils.logWarning(e);
        }
    }

    @MinecraftVersion("1.16")
    public static void sweepParticle(@NotNull final Player player) {
        // find EntityHuman Particles.SWEEP_ATTACK
        ((CraftHumanEntity) player).getHandle().ff();
    }

    @MinecraftVersion("1.16")
    public static void knockBack(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager) {
        final int i = 1;
        final Entity nmsDamager = ((CraftEntity) damager).getHandle();
        ((CraftLivingEntity) victim).getHandle().knockback(
                (float) i * 0.5F,
                MathHelper.sin(nmsDamager.getYRot() * 0.017453292F),
                -MathHelper.cos(nmsDamager.getYRot() * 0.017453292F),
                ((CraftLivingEntity) damager).getHandle()
        );
    }

    @MinecraftVersion("1.16")
    public static void knockBackArrow(@NotNull final LivingEntity victim, @NotNull final org.bukkit.entity.Entity damager) {
        final Entity nmsDamager = ((CraftEntity) damager).getHandle();
        if (nmsDamager instanceof final EntityArrow arrow) {
            /*
            EntityArrow.class { protected void a(MovingObjectPositionEntity movingobjectpositionentity) }
            if (this.aw > 0) {
                Vec3D vec3d = this.getMot().d(1.0D, 0.0D, 1.0D).d().a((double)this.aw * 0.6D);
                if (vec3d.g() > 0.0D) {
                    entityliving.i(vec3d.b, 0.1D, vec3d.d);
                }
            }
             */
            if (arrow.aw > 0) {
                final Vec3D vec3d = arrow.getMot().d(1.0D, 0.0D, 1.0D).d().a((double) arrow.aw * 0.6D);
                if (vec3d.g() > 0.0D) {
                    ((CraftLivingEntity) victim).getHandle().i(vec3d.b, 0.1D, vec3d.d);
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
        return ((CraftPlayer) player).getHandle().getRecipeBook().hasDiscoveredRecipe(Objects.requireNonNull(MinecraftKey.a(key)));
    }

}
