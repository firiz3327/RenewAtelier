package net.firiz.renewatelier.version.entity.projectile.arrow.skill;

import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.ateliercommonapi.nms.packet.EntityPacket;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.renewatelier.entity.player.Char;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@MinecraftVersion("1.16")
public abstract class SkillProjectile extends EntityArrow implements ISkillProjectile {

    private final Char player;
    private CraftArrow bukkitEntity;

    protected SkillProjectile(Char player, org.bukkit.World world) {
        super(EntityTypes.d, ((CraftWorld) world).getHandle());
        this.player = player;
        setShooter(((CraftPlayer) player.getPlayer()).getHandle());
        setNoGravity(true);
        setSilent(true);
        setInvisible(true);
    }

    @Override
    public SkillProjectile spawn(Location location, CreatureSpawnEvent.SpawnReason reason) {
        setLocation(location);
        getWorld().addEntity(this, reason);
        final Packet<?> despawnPacket = EntityPacket.despawnPacket(getId());
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
        hitBlock(new Location(getWorld().getWorld(), pos.getX(), pos.getY(), pos.getZ()));
        this.die();
    }

    @Override
    protected void a(@NotNull MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        moving();
    }

    protected abstract void hitEntity(org.bukkit.entity.Entity entity);

    protected abstract void hitBlock(Location location);

    protected void moving() {
    }

    @Override
    public void setLocation(Location location) {
        setPositionRotation(location.getX(), location.getY(), location.getZ(), getXRot(), getYRot());
    }

    @Override
    public Location getLocation() {
        return new Location(getWorld().getWorld(), locX(), locY(), locZ());
    }

    @Override
    public void setVelocity(Vector velocity) {
        getBukkitEntity().setVelocity(velocity);
    }

    @Override
    public Vector getVelocity() {
        return getBukkitEntity().getVelocity();
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
    public @NotNull ItemStack getItemStack() {
        return new ItemStack(Items.mh);
    }

    @Override
    public @NotNull CraftArrow getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new CraftArrow(getWorld().getCraftServer(), this);
        }
        return bukkitEntity;
    }
}
