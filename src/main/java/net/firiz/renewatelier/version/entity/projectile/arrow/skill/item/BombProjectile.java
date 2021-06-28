package net.firiz.renewatelier.version.entity.projectile.arrow.skill.item;

import net.firiz.ateliercommonapi.nms.packet.EntityPacket;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.version.entity.projectile.arrow.skill.SkillProjectile;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BombProjectile extends SkillProjectile implements IBombProjectile {

    private final Consumer<IBombProjectile> effect;
    private final BiConsumer<IBombProjectile, Entity> hitEntity;
    private final BiConsumer<IBombProjectile, Location> hitBlock;

    public BombProjectile(Char player, Consumer<IBombProjectile> effect, BiConsumer<IBombProjectile, Entity> hitEntity, BiConsumer<IBombProjectile, Location> hitBlock, org.bukkit.World world) {
        super(player, world);
        this.effect = effect;
        this.hitEntity = hitEntity;
        this.hitBlock = hitBlock;
        getBukkitEntity().setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        setSilent(true);
        setInvisible(true);
    }

    @Override
    public BombProjectile spawn(Location location) {
        super.spawn(location);
        final Packet<?> despawnPacket = EntityPacket.despawnPacket(getId());
        location.getNearbyPlayers(50).forEach(player -> PacketUtils.sendPacket(player, despawnPacket));
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        effect.accept(this);
    }

    @Override
    protected void hitEntity(Entity entity) {
        hitEntity.accept(this, entity);
    }

    @Override
    protected void hitBlock(Location location) {
        hitBlock.accept(this, location);
    }

    @Override
    protected void moving() {
        effect.accept(this);
    }

    @Override
    public void setVelocity(Vector velocity) {
        setNoGravity(false);
        super.setVelocity(velocity);
    }
}
