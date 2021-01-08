package net.firiz.renewatelier.version.entity.projectile.arrow;

import com.google.common.base.Preconditions;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.PacketUtils;
import net.minecraft.server.v1_16_R3.EntityTippedArrow;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class BombProjectile extends EntityTippedArrow implements IBombProjectile {

    private final Consumer<IBombProjectile> data;

    private BombProjectile(Consumer<IBombProjectile> data, org.bukkit.World world) {
        super(EntityTypes.ARROW, ((CraftWorld) world).getHandle());
        this.data = data;
        this.fromPlayer = PickupStatus.DISALLOWED;
        setSilent(true);
        setInvisible(true);
    }

    public static IBombProjectile spawn(Consumer<IBombProjectile> data, Location location) {
        final BombProjectile bombProjectile = new BombProjectile(data, location.getWorld());
        bombProjectile.setLocation(location);
        final WorldServer worldServer = (WorldServer) bombProjectile.world;
        worldServer.addEntity(bombProjectile, CreatureSpawnEvent.SpawnReason.CUSTOM);
        final Packet<?> despawnPacket = EntityPacket.getDespawnPacket(bombProjectile.getId());
        location.getNearbyPlayers(50).forEach(player -> PacketUtils.sendPacket(player, despawnPacket));
        return bombProjectile;
    }

    @Override
    public void tick() {
        super.tick();
        data.accept(this);
    }

    @Override
    public Location getLocation() {
        return new Location(this.world.getWorld(), locX(), locY(), locZ());
    }

    @Override
    public void setLocation(Location location) {
        setLocation(location.getX(), location.getY(), location.getZ(), this.yaw, this.pitch);
    }

    @Override
    public void setVelocity(Vector velocity) {
        Preconditions.checkArgument(velocity != null, "velocity");
        velocity.checkFinite();
        setMot(CraftVector.toNMS(velocity));
        velocityChanged = true;
    }

    @Override
    public Vector getVelocity() {
        return CraftVector.toBukkit(getMot());
    }

    @Override
    public boolean isGround() {
        return inGround;
    }

    @Override
    public void remove() {
        die();
    }
}
