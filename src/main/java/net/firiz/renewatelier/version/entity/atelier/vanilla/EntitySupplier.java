package net.firiz.renewatelier.version.entity.atelier.vanilla;

import net.minecraft.server.v1_16_R3.DamageSource;

import java.util.function.Supplier;

public interface EntitySupplier extends Supplier<Object> {

    boolean damageEntity(DamageSource damageSource, float f);

    void die(DamageSource damageSource);

}
