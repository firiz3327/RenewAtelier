package net.firiz.renewatelier.version.entity.atelier.test;

import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.EntitySupplier;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EntityZombie;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

@MinecraftVersion("1.17")
public class TestBoss extends EntityZombie implements EntitySupplier {

    private final LivingData livingData;

    public TestBoss(Location location) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.livingData = new LivingData(TargetEntityTypes.ZOMBIE, this, new MonsterStats(
                getBukkitEntity(),
                Race.UNDEAD,
                40,
                1000,
                1000,
                100,
                30,
                15,
                true
        ), "テストボス");
        setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void spawn() {
        AtelierEntityUtils.INSTANCE.spawn(this);
    }

    @Override
    public Object get() {
        return livingData;
    }

    @Override
    public boolean damageEntity(@NotNull DamageSource ds, float f) {
        return livingData.onDamageEntity(ds, f);
    }

    @Override
    public void die(@NotNull DamageSource damagesource) {
        livingData.onDie(damagesource);
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        livingData.dropDeathLoot(damagesource, i, flag);
    }
}
