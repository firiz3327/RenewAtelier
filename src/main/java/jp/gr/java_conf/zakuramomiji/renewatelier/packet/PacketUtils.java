package jp.gr.java_conf.zakuramomiji.renewatelier.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.base.Optional;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.ChatModifier;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

/**
 * Class by iso2013 © 2017.
 * <p>
 * Licensed under LGPLv3. See https://opensource.org/licenses/lgpl-3.0.html for
 * more information. You may copy, distribute and modify the software provided
 * that modifications are described and licensed for free under LGPL. Derivative
 * works (including modifications or anything statically linked to the library)
 * can only be redistributed under LGPL, but applications that use the library
 * don't have to be.
 */
public class PacketUtils {

    private static final Map<EntityType, Integer> objects = new HashMap<EntityType, Integer>() {
        {
            put(EntityType.BOAT, 1);
            put(EntityType.DROPPED_ITEM, 2);
            put(EntityType.AREA_EFFECT_CLOUD, 3);
            put(EntityType.MINECART, 10);
            put(EntityType.PRIMED_TNT, 50);
            put(EntityType.ENDER_CRYSTAL, 51);
            put(EntityType.TIPPED_ARROW, 60);
            put(EntityType.SNOWBALL, 61);
            put(EntityType.EGG, 62);
            put(EntityType.FIREBALL, 63);
            put(EntityType.SMALL_FIREBALL, 64);
            put(EntityType.ENDER_PEARL, 65);
            put(EntityType.WITHER_SKULL, 66);
            put(EntityType.SHULKER_BULLET, 67);
            put(EntityType.FALLING_BLOCK, 70);
            put(EntityType.ITEM_FRAME, 71);
            put(EntityType.ENDER_SIGNAL, 72);
            put(EntityType.SPLASH_POTION, 73);
            put(EntityType.THROWN_EXP_BOTTLE, 75);
            put(EntityType.FIREWORK, 76);
            put(EntityType.LEASH_HITCH, 77);
            put(EntityType.ARMOR_STAND, 78);
            put(EntityType.FISHING_HOOK, 90);
            put(EntityType.SPECTRAL_ARROW, 91);
            put(EntityType.DRAGON_FIREBALL, 93);
        }
    };
    private static int lastId = -1;
    private static ProtocolManager manager;
    private static Method getNMSCopy;

    public static void init(final ProtocolManager manager) {
        PacketUtils.manager = manager;
        try {
            getNMSCopy = CraftItemStack.class.getDeclaredMethod("asNMSCopy", ItemStack.class);
        } catch (NoSuchMethodException e) {
            getNMSCopy = null;
        }

        // debug
//        manager.addPacketListener(new PacketAdapter(AtelierPlugin.getPlugin(), PacketType.Play.Server.ENTITY_METADATA) {
//            @Override
//            public void onPacketSending(PacketEvent arg0) {
//                List<WrappedWatchableObject> get = arg0.getPacket().getWatchableCollectionModifier().getValues().get(0);
//                for (WrappedWatchableObject s : get) {
//                    if (s.getIndex() == 2) {
//                        System.out.println();
//                    }
//                }
//            }
//        });
    }

    public static void sendPacket(Player player, PacketContainer packet) {
        try {
            manager.sendServerPacket(player, packet);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PacketUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PacketContainer getSpawnPacket(FakeEntity entity, Location l) {
        return getSpawnPacket(entity, l, new WrappedDataWatcher());
    }

    public static PacketContainer getSpawnPacket(FakeEntity entity, Location l, WrappedDataWatcher metadata) {
        return getSpawnPacket(entity, l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw(), metadata);
    }

    public static PacketContainer getSpawnPacket(FakeEntity entity, double x, double y, double z) {
        return getSpawnPacket(entity, x, y, z, 0.0, 0.0, new WrappedDataWatcher());
    }

    public static PacketContainer getSpawnPacket(FakeEntity entity, double x, double y, double z, WrappedDataWatcher metadata) {
        return getSpawnPacket(entity, x, y, z, 0.0, 0.0, metadata);
    }

    public static PacketContainer getSpawnPacket(FakeEntity entity, double x, double y, double z, double pitch, double yaw) {
        return getSpawnPacket(entity, x, y, z, pitch, yaw, new WrappedDataWatcher());
    }

    public static PacketContainer getSpawnPacket(FakeEntity entity, double x, double y, double z, double pitch, double yaw, WrappedDataWatcher metadata) {
        PacketContainer packet = new PacketContainer(entity.object ? PacketType.Play.Server.SPAWN_ENTITY : PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        packet.getIntegers().write(0, entity.entityId);
        packet.getModifier().writeDefaults();
        packet.getUUIDs().write(0, entity.uniqueId);
        packet.getDoubles().write(0, x);
        packet.getDoubles().write(1, y);
        packet.getDoubles().write(2, z);
        if (entity.object) {
            packet.getIntegers().write(6, entity.typeId);
            packet.getIntegers().write(4, (int) (pitch * 256.0F / 360.0F));
            packet.getIntegers().write(5, (int) (yaw * 256.0F / 360.0F));
            packet.getIntegers().write(7, entity.objectData);
        } else {
            packet.getIntegers().write(1, (int) entity.type.ordinal());
            packet.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
            packet.getBytes().write(1, (byte) (pitch * 256.0F / 360.0F));
            packet.getBytes().write(2, (byte) (pitch * 256.0F / 360.0F));
            packet.getIntegers().write(2, 0);
            packet.getIntegers().write(3, 0);
            packet.getIntegers().write(4, 0);
            packet.getDataWatcherModifier().write(0, metadata);
        }
        return packet;
    }

    public static PacketContainer getMetadataPacket(FakeEntity entity, WrappedDataWatcher metadata) {
        return getMetadataPacket(entity, metadata.getWatchableObjects());
    }

    public static PacketContainer getMetadataPacket(FakeEntity entity, List<WrappedWatchableObject> metadata) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packet.getModifier().writeDefaults();
        packet.getModifier().write(0, entity.entityId);
        packet.getWatchableCollectionModifier().write(0, metadata);
        return packet;
    }

    public static PacketContainer getPassengerPacket(FakeEntity entity, int[] passengers) {
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.MOUNT);
        packet.getIntegers().write(0, entity.entityId);
        packet.getIntegerArrays().write(0, passengers);
        return packet;
    }

    public static PacketContainer getDespawnPacket(FakeEntity... entities) {
        final int[] entityIds = new int[entities.length];
        for (int i = 0; i < entities.length; i++) {
            entityIds[i] = entities[i].entityId;
        }
        return getDespawnPacket(entityIds);
    }

    public static PacketContainer getDespawnPacket(int... entityIds) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getModifier().writeDefaults();
        packet.getIntegerArrays().write(0, entityIds);
        return packet;
    }

    public static WrappedDataWatcher createWatcher(Map<Integer, Object> data) {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        for (Map.Entry<Integer, Object> e : data.entrySet()) {
            if (e.getValue().getClass() == org.bukkit.inventory.ItemStack.class) {
                if (getNMSCopy == null) {
                    throw new IllegalArgumentException("ItemStack serializer could not be found!");
                }
                try {
                    watcher.setObject(e.getKey(), WrappedDataWatcher.Registry.getItemStackSerializer(false), getNMSCopy.invoke(null, e.getValue()));
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
                continue;
            }
            watcher.setObject(e.getKey(), WrappedDataWatcher.Registry.get(e.getValue().getClass()), e.getValue());
        }
        return watcher;
    }

    public static WrappedDataWatcher setEntityCustomName(WrappedDataWatcher wdw, String name) {
        final ChatComponentText cct = new ChatComponentText("") {
            {
                final ChatModifier cctcm = new ChatModifier();
                setChatModifier(cctcm);

                final ChatComponentText namecct = new ChatComponentText(name);
                namecct.setChatModifier(new ChatModifier().setChatModifier(cctcm));
                addSibling(namecct);
            }
        };
        final WrappedDataWatcherObject wdwo = new WrappedDataWatcherObject(
                2,
                new WrappedDataWatcher.Serializer(
                        IChatBaseComponent.class,
                        DataWatcherRegistry.f,
                        true
                )
        );
        wdw.setObject(wdwo, Optional.of(cct));
        return wdw;
    }

    public static class FakeEntity {

        private int entityId;
        private UUID uniqueId;
        private EntityType type;
        private int typeId;
        private boolean object;
        private int objectData;

        public static FakeEntity createNew(EntityType type, int objectData) {
            FakeEntity out = new FakeEntity();
            lastId--;
            out.entityId = lastId;
            out.uniqueId = UUID.randomUUID();
            out.type = type;
            out.typeId = objects.containsKey(type) ? objects.get(type) : type.ordinal();
            out.object = objects.containsKey(type);
            out.objectData = objectData;
            return out;
        }

        public static FakeEntity createNew(EntityType type) {
            return createNew(type, 0);
        }
    }
}
