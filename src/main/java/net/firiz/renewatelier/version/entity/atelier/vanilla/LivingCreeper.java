package net.firiz.renewatelier.version.entity.atelier.vanilla;

import net.firiz.renewatelier.version.MinecraftVersion;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.minecraft.server.v1_16_R2.DamageSource;
import net.minecraft.server.v1_16_R2.EntityCreeper;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.World;

@MinecraftVersion("1.16")
public class LivingCreeper extends EntityCreeper implements EntitySupplier {

    private LivingData livingData;

    public LivingCreeper(World world) {
        super(EntityTypes.CREEPER, world);
    }

    @Override
    public Object get() {
        return livingData;
    }

    @Override
    public boolean damageEntity(DamageSource ds, float f) {
        return livingData.onDamageEntity(ds, f);
    }

    @Override
    public void die(DamageSource damagesource) {
        livingData.onDie(damagesource);
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        livingData.dropDeathLoot(damagesource, i, flag);
    }

    @Override
    public void explode() {
        super.explode();
        livingData.getHoloHealth().die();
    }
}
