package net.firiz.renewatelier.version.entity.drop;

import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftItem;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PlayerDropItem extends EntityItem {

    private final Packet<?> despawnPacket;

    public PlayerDropItem(
            org.bukkit.entity.Player targetPlayer,
            org.bukkit.Location location,
            org.bukkit.inventory.ItemStack itemStack
    ) {
        super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        this.setOwner(targetPlayer.getUniqueId());
        this.despawnPacket = EntityPacket.getDespawnPacket(this.getId());
        this.age = 4800; // 生存時間 1分 (6000で消滅するため)
        setItemStack(VersionUtils.asNMSCopy(itemStack));
    }

    public static boolean isPlayerDrop(org.bukkit.entity.Item item) {
        return ((CraftItem) item).getHandle() instanceof PlayerDropItem;
    }

    public void drop() {
        final WorldServer world = (WorldServer) this.world;
        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        final PlayerChunkMap.EntityTracker entityTracker = world.getChunkProvider().playerChunkMap.trackedEntities.get(this.getId());
        if (entityTracker != null) {
            entityTracker.trackedPlayers
                    .stream()
                    .filter(player -> player.getUniqueID() != this.getOwner())
                    .forEach(player -> player.playerConnection.sendPacket(despawnPacket));
        }
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (entityhuman.getUniqueID() == this.getOwner()) {
            super.pickup(entityhuman);
        }
    }
}
