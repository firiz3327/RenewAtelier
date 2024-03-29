package net.firiz.renewatelier.entity.arrow;

import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierSpectralArrow;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftSpectralArrow;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtelierSpectralArrow extends CraftSpectralArrow implements AtelierAbstractArrow, SpectralArrow {

    private final Spigot spigot = new Spigot();
    private final LivingEntity source;
    private final ItemStack bow;
    private final ItemStack arrow;
    private final float force;
    private final boolean isSkill;

    public AtelierSpectralArrow(CraftServer server, NMSAtelierSpectralArrow entity, LivingEntity source, ItemStack bow, ItemStack arrow, float force, boolean isSkill) {
        super(server, entity);
        this.source = source;
        this.bow = bow;
        this.arrow = arrow;
        this.force = force;
        this.isSkill = isSkill;
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
        return ((NMSAtelierSpectralArrow) entity).getLocation().getChunk();
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

    @NotNull
    @Override
    public ItemStack getArrow() {
        return arrow;
    }

    @Override
    public float getForce() {
        return force;
    }

    @Override
    public boolean isSkill() {
        return isSkill;
    }

    @NotNull
    @Override
    public CraftItemStack getItemStack() {
        return CraftItemStack.asCraftCopy(getArrow());
    }
}
