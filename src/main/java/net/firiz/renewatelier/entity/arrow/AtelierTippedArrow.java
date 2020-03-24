package net.firiz.renewatelier.entity.arrow;

import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierArrow;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftTippedArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtelierTippedArrow extends CraftTippedArrow implements AtelierArrow {

    private final Spigot spigot = new Spigot();
    private final LivingEntity source;
    private final ItemStack bow;
    private final ItemStack arrow;
    private final float force;

    public AtelierTippedArrow(CraftServer server, NMSAtelierArrow entity, LivingEntity source, ItemStack bow, ItemStack arrow, float force) {
        super(server, entity);
        this.source = source;
        this.bow = bow;
        this.arrow = arrow;
        this.force = force;
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return spigot;
    }

    @Nullable
    @Override
    public Location getOrigin() {
        return null;
    }

    @Override
    public boolean fromMobSpawner() {
        return false;
    }

    @NotNull
    @Override
    public Chunk getChunk() {
        return ((NMSAtelierArrow) entity).getLocation().getChunk();
    }

    @NotNull
    @Override
    public CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
        return CreatureSpawnEvent.SpawnReason.CUSTOM;
    }

    @Override
    public LivingEntity getSource() {
        return source;
    }

    @Override
    public ItemStack getBow() {
        return bow;
    }

    @Override
    public ItemStack getArrow() {
        return arrow;
    }

    @Override
    public float getForce() {
        return force;
    }
}
