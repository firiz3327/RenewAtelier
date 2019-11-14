package net.firiz.renewatelier.entity.arrow;

import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierArrow;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftTippedArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AtelierTippedArrow extends CraftTippedArrow implements AtelierArrow {

    public AtelierTippedArrow(CraftServer server, NMSAtelierArrow entity, LivingEntity source, ItemStack bow, ItemStack arrow) {
        super(server, entity);
        this.source = source;
        this.bow = bow;
        this.arrow = arrow;
    }

    private final Spigot spigot = new Spigot();
    private final LivingEntity source;
    private final ItemStack bow;
    private final ItemStack arrow;

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
}
