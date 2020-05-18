package net.firiz.renewatelier.version.packet;

import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public class EntityPacket {

    private EntityPacket() {
    }

    public static PacketPlayOutSpawnEntity getSpawnPacket(FakeEntity entity, Location loc) {
        return new PacketPlayOutSpawnEntity(
                entity.getEntityId(),
                entity.getUniqueId(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw(),
                entity.getType().getTypeNms(),
                (int) loc.getYaw(),
                new Vec3D(0, 0, 0)
        );
    }

    public static PacketPlayOutEntityDestroy getDespawnPacket(int... entityIds) {
        return new PacketPlayOutEntityDestroy(entityIds);
    }

    public static PacketPlayOutEntity.PacketPlayOutEntityLook getLookPacket(int entityId, double pitch, double yaw, boolean onGround) {
        return new PacketPlayOutEntity.PacketPlayOutEntityLook(
                entityId,
                (byte) (yaw * 256.0F / 360.0F),
                (byte) (pitch * 256.0F / 360.0F),
                onGround
        );
    }

    public static PacketPlayOutEntityHeadRotation getHeadRotationPacket(int entityId, double yaw) {
        final PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "a"), packet, entityId);
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "b"), packet, (byte) (yaw * 256.0F / 360.0F));
        return packet;
    }

    public static PEMeta getEmptyMeta() {
        return getEmptyMeta(null);
    }

    public static PEMeta getEmptyMeta(final Entity entity) {
        return new PEMeta(new DataWatcher(entity), false);
    }

    public static PEMeta getMeta(final DataWatcher dataWatcher) {
        return new PEMeta(dataWatcher, false);
    }

    public static PEMeta getMessageStandMeta(final org.bukkit.World world, final String name) {
        return getMessageStandMeta(world, name, false);
    }

    public static PEMeta getMessageStandMeta(final org.bukkit.World world, final String name, final boolean small) {
        final WorldServer worldServer = ((CraftWorld) world).getHandle();
        final EntityArmorStand armorStand = new EntityArmorStand(worldServer.getMinecraftWorld(), 0, 0, 0);
        armorStand.setCustomName(new ChatComponentText(name));
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setSmall(small);
        armorStand.setMarker(true); // 当たり判定がなくなる
        return new PEMeta(
                armorStand.getDataWatcher(),
                false
        );
    }

    public static PacketPlayOutEntityTeleport getTeleportPacket(final int entityId, final Location location, final boolean onGround) {
        final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "a"), packet, entityId);
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "b"), packet, location.getX());
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "c"), packet, location.getY());
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "d"), packet, location.getZ());
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "e"), packet, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "f"), packet, (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        VersionUtils.setFieldValue(VersionUtils.getField(packet, "g"), packet, onGround);
        return packet;
    }

}
