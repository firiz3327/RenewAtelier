package net.firiz.renewatelier.version.packet;

import net.firiz.ateliercommonapi.FakeId;
import net.firiz.renewatelier.version.MinecraftVersion;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.nms.VEntity;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

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

    @MinecraftVersion("1.16")
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

    @MinecraftVersion("1.16")
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

    @MinecraftVersion("1.16")
    public static int createFakeId(Entity entity) {
        final int fakeId = FakeId.createId();
        entity.e(fakeId); // Entity.id = fakeId
        return fakeId;
    }

    public static VEntity<EntityVillager> createVillager(Location location, String name, Villager.Type type, Villager.Profession profession) {
        final CraftWorld craftWorld = (CraftWorld) location.getWorld();
        final EntityVillager entityVillager = (EntityVillager) craftWorld.createEntity(location, Villager.class);
        final Villager villager = (Villager) entityVillager.getBukkitEntity();
        entityVillager.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entityVillager.setCustomNameVisible(true);
        villager.setCustomName(name);
        villager.setVillagerType(type);
        villager.setProfession(profession);
        final int fakeId = createFakeId(entityVillager);
        return new VEntity<>(entityVillager, fakeId, name, location);
    }

    public static VEntity<Entity> createEntity(Location location, String name, EntityType type) {
        final CraftWorld craftWorld = (CraftWorld) location.getWorld();
        final Entity entity = craftWorld.createEntity(location, type.getEntityClass());
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setCustomNameVisible(true);
        entity.getBukkitEntity().setCustomName(name);
        final int fakeId = createFakeId(entity);
        return new VEntity<>(entity, fakeId, name, location);
    }

    @MinecraftVersion("1.16")
    public static void sendSpawnPacketLiving(Player player, VEntity<?> entity) {
        if (!(entity.getEntity() instanceof EntityLiving)) {
            throw new IllegalArgumentException("entity is not EntityLiving class");
        }
        final EntityLiving living = (EntityLiving) entity.getEntity();
        PacketUtils.sendPackets(
                player,
                new PacketPlayOutSpawnEntityLiving(living),
                new PacketPlayOutEntityMetadata(
                        entity.getEntityId(),
                        living.getDataWatcher(),
                        true
                )
        );
    }
}
