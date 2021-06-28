package net.firiz.renewatelier.version.entity.atelier.vanilla;

import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.renewatelier.version.entity.atelier.EntitySupplier;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.level.World;

@MinecraftVersion("1.17")
public class LivingCreeper extends EntityCreeper implements EntitySupplier {

    private LivingData livingData;

    public LivingCreeper(World world) {
        super(EntityTypes.o, world);
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
