package net.firiz.renewatelier.version.entity.projectile.arrow.skill;

import com.google.common.base.Preconditions;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.PacketUtils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

public abstract class SkillProjectile extends EntityArrow implements ISkillProjectile {

    private final Char player;

    protected SkillProjectile(Char player, org.bukkit.World world) {
        super(EntityTypes.ARROW, ((CraftWorld) world).getHandle());
        this.player = player;
        setShooter(((CraftPlayer) player.getPlayer()).getHandle());
        setNoGravity(true);
        setSilent(true);
        setInvisible(true);
    }

    @Override
    public SkillProjectile spawn(Location location, CreatureSpawnEvent.SpawnReason reason) {
        setLocation(location);
        world.addEntity(this, reason);
        final Packet<?> despawnPacket = EntityPacket.getDespawnPacket(getId());
        location.getNearbyPlayers(50).forEach(p -> PacketUtils.sendPacket(p, despawnPacket));
        return this;
    }

    @Override
    protected final void a(MovingObjectPositionEntity var0) {
        hitEntity(var0.getEntity().getBukkitEntity());
        this.die();
    }

    @Override
    protected void a(MovingObjectPositionBlock var0) {
        final BlockPosition pos = var0.getBlockPosition();
        hitBlock(new Location(world.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
        this.die();
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        moving();
    }

    protected abstract void hitEntity(org.bukkit.entity.Entity entity);

    protected abstract void hitBlock(Location location);

    protected void moving() {
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public void setLocation(Location location) {
        setLocation(location.getX(), location.getY(), location.getZ(), this.yaw, this.pitch);
    }

    @Override
    public Location getLocation() {
        return new Location(this.world.getWorld(), locX(), locY(), locZ());
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
    public final void remove() {
        die();
    }

    @Override
    public Char getPlayer() {
        return player;
    }

    @Override
    protected ItemStack getItemStack() {
        return new ItemStack(Items.ARROW);
    }
}
