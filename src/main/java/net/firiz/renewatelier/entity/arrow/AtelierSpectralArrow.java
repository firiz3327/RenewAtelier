package net.firiz.renewatelier.entity.arrow;

import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierSpectralArrow;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSpectralArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AtelierSpectralArrow extends CraftSpectralArrow implements AtelierArrow {

    private final Spigot spigot = new Spigot();
    private final LivingEntity source;
    private final ItemStack bow;
    private final ItemStack arrow;
    private final float force;

    public AtelierSpectralArrow(CraftServer server, NMSAtelierSpectralArrow entity, LivingEntity source, ItemStack bow, ItemStack arrow, float force) {
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
