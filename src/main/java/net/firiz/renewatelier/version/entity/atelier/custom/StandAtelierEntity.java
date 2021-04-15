package net.firiz.renewatelier.version.entity.atelier.custom;

import net.firiz.renewatelier.version.entity.atelier.EntitySupplier;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityBat;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;

public class StandAtelierEntity extends EntityBat implements EntitySupplier {

    private final LivingData livingData;

    public StandAtelierEntity(@NotNull final Location location, @NotNull final LivingData livingData) {
        super(EntityTypes.BAT, ((CraftWorld) location.getWorld()).getHandle());
        this.livingData = livingData;
        setNoAI(true);
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
}
