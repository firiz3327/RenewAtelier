package net.firiz.renewatelier.entity.arrow;

import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierTippedArrow;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftTippedArrow;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtelierTippedArrow extends CraftTippedArrow implements AtelierAbstractArrow, Arrow {

    private final Spigot spigot = new Spigot();
    private final LivingEntity source;
    private final ItemStack bow;
    private final ItemStack arrow;
    private final float force;
    private final boolean isSkill;

    public AtelierTippedArrow(CraftServer server, NMSAtelierTippedArrow entity, LivingEntity source, ItemStack bow, ItemStack arrow, float force, boolean isSkill) {
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
        return ((NMSAtelierTippedArrow) entity).getLocation().getChunk();
    }

    @NotNull
    @Override
    public CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
        return CreatureSpawnEvent.SpawnReason.CUSTOM;
    }

    @Override
    public boolean isInWater() {
        return getHandle().isInWater();
    }

    @Override
    public boolean isInRain() {
        return getHandle().isInRain();
    }

    @Override
    public boolean isInBubbleColumn() {
        return getHandle().isInBubbleColumn();
    }

    @Override
    public boolean isInWaterOrRain() {
        return getHandle().isInWaterOrRain();
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        return getHandle().isInWaterOrBubbleColumn();
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        return getHandle().isInWaterOrRainOrBubble();
    }

    @Override
    public boolean isInLava() {
        return getHandle().isInLava();
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
