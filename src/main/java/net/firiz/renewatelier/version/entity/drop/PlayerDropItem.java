package net.firiz.renewatelier.version.entity.drop;

import net.firiz.ateliercommonapi.nms.MinecraftConverter;
import net.firiz.ateliercommonapi.nms.packet.EntityPacket;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PlayerDropItem extends EntityItem {

    private final Packet<?> despawnPacket;

    public PlayerDropItem(
            org.bukkit.entity.Player targetPlayer,
            org.bukkit.Location location,
            org.bukkit.inventory.ItemStack itemStack
    ) {
        super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack));
        this.setOwner(targetPlayer.getUniqueId());
        this.despawnPacket = EntityPacket.despawnPacket(this.getId());
        this.ao = 4800; // ao = age 生存時間 1分 (6000で消滅するため)
    }

    public static boolean isPlayerDrop(org.bukkit.entity.Item item) {
        return ((CraftItem) item).getHandle() instanceof PlayerDropItem;
    }

    public void drop() {
        final WorldServer world = (WorldServer) getWorld();
        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        PacketUtils.trackPlayer(this).stream()
                .filter(player -> player.getUniqueId() != this.getOwner())
                .forEach(player -> MinecraftConverter.connection(player).sendPacket(despawnPacket));
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (entityhuman.getUniqueID() == this.getOwner()) {
            super.pickup(entityhuman);
        }
    }
}
