package net.firiz.renewatelier.version.entity.living.monster;

import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes;
import net.minecraft.server.v1_15_R1.DamageSource;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityZombie;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import java.util.function.Supplier;

public class TestZombie extends EntityZombie implements Supplier<Object> {

    private final LivingData livingData;

    public TestZombie(Location location) {
        super(EntityTypes.ZOMBIE, ((CraftWorld) location.getWorld()).getHandle());
        this.livingData = new LivingData(
                TargetEntityTypes.ZOMBIE,
                this,
                location
        );
    }

    @Override
    public Object get() {
        return livingData;
    }

    @Override
    public void tick() {
        super.tick();
        fireTicks = 0;
    }

    @Override
    public boolean damageEntity(DamageSource ds, float f) {
        return livingData.onDamageEntity(ds, f);
    }

}