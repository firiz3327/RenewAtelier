package net.firiz.renewatelier.version.entity.living.monster;

import net.firiz.renewatelier.utils.Chore;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import java.lang.reflect.Field;

public class C extends EntityCreeper {

    private Field fuseTicks = null;
    private int lockFuseTicks = -1;

    public C(org.bukkit.World world, org.bukkit.entity.Player player) {
        super(EntityTypes.CREEPER, ((CraftWorld) world).getHandle());
        this.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0, 0);

        try {
            fuseTicks = EntityCreeper.class.getDeclaredField("fuseTicks");
            fuseTicks.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Chore.logWarning(ex);
        }
        this.world.addEntity(this);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        try {
            lockFuseTicks = fuseTicks.getInt(this);
        } catch (IllegalAccessException ex) {
            Chore.logWarning(ex);
        }
        return super.damageEntity(damagesource, f);
    }

    @Override
    public void tick() {
        super.tick();

        if (fuseTicks != null && lockFuseTicks != -1) {
            ignite();
            try {
                fuseTicks.set(this, lockFuseTicks);
            } catch (IllegalAccessException ex) {
                Chore.logWarning(ex);
            }
        }
    }
}
