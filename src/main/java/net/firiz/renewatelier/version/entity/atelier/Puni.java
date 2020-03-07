package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSlime;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.function.Supplier;

public class Puni extends EntitySlime implements Supplier<Object> {

    private final Object livingData;
    private final EntityFallingBlock block;
    private boolean isSpawnedBlock = false;
    private boolean isDead = false;

    public Puni(org.bukkit.World world) {
        super(EntityTypes.SLIME, ((CraftWorld) world).getHandle());
        this.livingData = AtelierEntityUtils.INSTANCE.createLivingData(TargetEntityTypes.SLIME, this);
        setSize(2, false);
        addEffect(new MobEffect(
                MobEffectList.fromId(PotionEffectType.INVISIBILITY.getId()),
                Integer.MAX_VALUE,
                1,
                false,
                false
        ), EntityPotionEffectEvent.Cause.PLUGIN);

        block = new EntityFallingBlock(((CraftWorld) world).getHandle(), 0D, 0D, 0D, Blocks.ICE.getBlockData()) {
            @Override
            public void tick() {
                setLocation(Puni.this.locX, Puni.this.locY, Puni.this.locZ, 0, 0);
                ((WorldServer)this.world).getChunkProvider().broadcast(this, new PacketPlayOutEntityTeleport(this));
            }

            @Override
            public void die() {
                super.die();
                if (!isDead) {
                    isDead = true;
                    Puni.this.die();
                }
            }
        };
    }

    @Override
    public Object get() {
        return livingData;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isSpawnedBlock) {
            isSpawnedBlock = true;
            this.world.addEntity(block);
        }
        block.setLocation(locX, locY, locZ, 0, 0);
        ((WorldServer)block.world).getChunkProvider().broadcast(this, new PacketPlayOutEntityTeleport(this));
    }

    public EntityFallingBlock getBlock() {
        return block;
    }

    @Override
    public void die() {
        VersionUtils.superInvoke("die", this, Entity.class, void.class, new HashMap<>());
        isDead = true;
        block.die();
    }

}
