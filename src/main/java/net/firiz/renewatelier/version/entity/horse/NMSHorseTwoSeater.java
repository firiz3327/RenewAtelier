package net.firiz.renewatelier.version.entity.horse;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHorseTwoSeater extends EntityArmorStand {

    private final NMSAtelierHorse horse;

    protected NMSHorseTwoSeater(World world, NMSAtelierHorse horse) {
        super(EntityTypes.ARMOR_STAND, world);
        this.horse = horse;
        init();
    }

    private void init() {
        setNoGravity(true);
        setInvisible(true);
        setSmall(true);
    }

    public boolean hasRider() {
        return !passengers.isEmpty();
    }

    @Override
    public void tick() {
        // entity.class tick
        if (!this.world.isClientSide) {
            this.setFlag(6, this.bE());
        }
        this.entityBaseTick();
    }

    protected void spawn(Player player) {
        final Location location = player.getLocation();
        setPosition(location.getX(), location.getY(), location.getZ());
        world.addEntity(this);
        ((CraftPlayer) player).getHandle().startRiding(this);
    }
}
