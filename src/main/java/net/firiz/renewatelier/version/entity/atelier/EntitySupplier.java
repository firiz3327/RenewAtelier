package net.firiz.renewatelier.version.entity.atelier;


import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Supplier;

public interface EntitySupplier extends Supplier<Object> {

    boolean damageEntity(DamageSource damageSource, float f);

    void die(DamageSource damageSource);

}
