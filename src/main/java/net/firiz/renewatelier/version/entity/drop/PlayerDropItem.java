package net.firiz.renewatelier.version.entity.drop;

import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PlayerDropItem extends EntityItem {

    private final EntityPlayer targetPlayer;
    private final Packet<?> despawnPacket;

    public PlayerDropItem(
            org.bukkit.entity.Player targetPlayer,
            org.bukkit.Location location,
            org.bukkit.inventory.ItemStack itemStack
    ) {
        super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        this.targetPlayer = ((CraftPlayer) targetPlayer).getHandle();
        this.despawnPacket = EntityPacket.getDespawnPacket(this.getId());
        setItemStack(VersionUtils.asNMSCopy(itemStack));
    }

    private void update(EntityPlayer entityPlayer) {
        if (entityPlayer != targetPlayer) {
            entityPlayer.playerConnection.sendPacket(despawnPacket);
        }
    }

    public void drop() {
        final WorldServer world = (WorldServer) this.world;
        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        final PlayerChunkMap.EntityTracker entityTracker = world.getChunkProvider().playerChunkMap.trackedEntities.get(this.getId());
        if (entityTracker != null) {
            entityTracker.trackedPlayers.forEach(this::update);
        }
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (entityhuman == targetPlayer) {
            super.pickup(entityhuman);
        }
    }
}
