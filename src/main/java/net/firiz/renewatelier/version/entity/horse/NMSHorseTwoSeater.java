package net.firiz.renewatelier.version.entity.horse;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHorseTwoSeater extends EntityArmorStand {

    private final NMSAtelierHorse horse;

    protected NMSHorseTwoSeater(World world, NMSAtelierHorse horse) {
        super(EntityTypes.c, world);
        this.horse = horse;
        init();
    }

    private void init() {
        setNoGravity(true);
        setInvisible(true);
        setSmall(true);
    }

    public boolean hasRider() {
        return !getPassengers().isEmpty();
    }

    @Override
    public void tick() {
        this.entityBaseTick();
    }

    protected void spawn(Player player) {
        final Location location = player.getLocation();
        setPosition(location.getX(), location.getY(), location.getZ());
        getWorld().addEntity(this);
        ((CraftPlayer) player).getHandle().startRiding(this);
    }
}
