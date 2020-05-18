package net.firiz.renewatelier.version;

import net.firiz.renewatelier.utils.Chore;
import net.minecraft.server.v1_15_R1.DamageSource;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.MathHelper;
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

    public static boolean isDead(org.bukkit.entity.Entity entity) {
        return entity.isDead() || (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0);
    }

    public static void hurt(@NotNull LivingEntity victim, @NotNull org.bukkit.entity.Entity damager, @Nullable DamageSource damageSource) {
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

    public static void sweepParticle(Player player) {
        ((CraftHumanEntity) player).getHandle().ea(); // 1.15
    }

    public static void knockBack(LivingEntity victim, org.bukkit.entity.Entity damager) {
        final int i = 1;
        final Entity nmsDamager = ((CraftEntity) damager).getHandle();
        ((CraftLivingEntity) victim).getHandle().a( // 1.15
                nmsDamager,
                (float) i * 0.5F,
                MathHelper.sin(nmsDamager.yaw * 0.017453292F),
                -MathHelper.cos(nmsDamager.yaw * 0.017453292F)
        );
    }
}
