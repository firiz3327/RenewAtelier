package net.firiz.renewatelier.version.entity.living.monster;

import net.firiz.renewatelier.entity.Race;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import java.util.function.Supplier;

public class SSSkeleton extends EntitySkeleton implements Supplier<Object> {

    private final Object livingData;

    public SSSkeleton(org.bukkit.World world) {
        super(EntityTypes.SKELETON, ((CraftWorld) world).getHandle());
        this.livingData = AtelierEntityUtils.INSTANCE.createLivingData(this,
                new MonsterStats(Race.UNDEAD, 24, 1966, 1966, 287, 150, 130)
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

}
