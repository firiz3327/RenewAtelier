package jp.gr.java_conf.zakuramomiji.renewatelier.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.loop.LoopManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.ChatModifier;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

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
    private static WrappedWatchableObject wwo;
    private static Runnable run;

    public static void init(final ProtocolManager manager) {
        PacketUtils.manager = manager;
        try {
            getNMSCopy = CraftItemStack.class.getDeclaredMethod("asNMSCopy", ItemStack.class);
        } catch (NoSuchMethodException e) {
            getNMSCopy = null;
        }

        // debug
        manager.addPacketListener(new PacketAdapter(AtelierPlugin.getPlugin(), PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent arg0) {
                if (wwo == null) {
                    List<WrappedWatchableObject> get = arg0.getPacket().getWatchableCollectionModifier().getValues().get(0);
                    for (WrappedWatchableObject s : get) {
                        if (s.getIndex() == 2) {
                            System.out.println();

                            final Optional opt = (Optional) s.getValue();
                            if (opt.isPresent()) {
                                wwo = s;
                                System.out.println(opt);
//                            final ChatComponentText cct = (ChatComponentText) opt.get();
//                            System.out.println(cct.getText());
//                            System.out.println(cct.a().get(0));
                            }
                        }
                    }
                }
            }
        });
//        manager.addPacketListener(new PacketAdapter(AtelierPlugin.getPlugin(), PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
//            @Override
//            public void onPacketSending(PacketEvent arg0) {
//                try {
//                    System.out.println("Parameter ---");
//                    String a[] = {"x", "y", "z", "yaw", "pitch"};
//                    int i = 0;
//                    for (int zzz : arg0.getPacket().getIntegers().getValues()) {
//                        System.out.println(a[i] + ": " + zzz);
//                        i++;
//                    }
//                    for (byte o : arg0.getPacket().getBytes().getValues()) {
//                        System.out.println(a[i] + ": " + o);
//                        i++;
//                    }
//                } catch (ArrayIndexOutOfBoundsException e) {
//                }
//                for (List<WrappedWatchableObject> get : arg0.getPacket().getWatchableCollectionModifier().getValues()) {
//                    for (WrappedWatchableObject s : get) {
//                        System.out.println(s.getIndex() + " " + s.getValue());
//                    }
//                }
//            }
//        });

        run = () -> {
            if (wwo == null) {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    player.getWorld()
                            .spawnEntity(player.getLocation(), EntityType.SNOWBALL)
                            .setCustomName("INIT");
                    LoopManager.INSTANCE.removeLoopEffect(run);
                    break;
                }
            }
        };
        LoopManager.INSTANCE.addLoopEffect(run);
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
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    Chore.log(Level.SEVERE, null, ex);
                }
                continue;
            }
            watcher.setObject(e.getKey(), WrappedDataWatcher.Registry.get(e.getValue().getClass()), e.getValue());
        }
        return watcher;
    }

    public static WrappedDataWatcher setEntityCustomName(WrappedDataWatcher watcher, String name) {
        final ChatComponentText tcct = (ChatComponentText) ((Optional) wwo.getValue()).get();
        ChatComponentText v = new ChatComponentText(name);
        ChatModifier cm = new ChatModifier();
        cm.setChatModifier(tcct.getChatModifier());
        v.setChatModifier(cm);
        tcct.a().set(0, v);
        watcher.setObject(wwo.getWatcherObject(), wwo.getValue());
//        watcher.setObject(
//                2,
//                WrappedDataWatcher.Registry.getChatComponentSerializer(true),
//                Optional.of(cct)
//        );
//        final ChatComponentText cct = new ChatComponentText("") {
//            {
//                final ChatModifier cctcm = new ChatModifier();
//                setChatModifier(cctcm);
//
//                final ChatComponentText namecct = new ChatComponentText(name);
//                namecct.setChatModifier(
//                        new ChatModifier()
//                                .setChatModifier(cctcm)
//                                .setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, "http://".concat(name)))
//                );
//                addSibling(namecct);
//            }
//        };
//        final WrappedDataWatcherObject wdwo = new WrappedDataWatcherObject(
//                2,
//                new WrappedDataWatcher.Serializer(
//                        IChatBaseComponent.class,
//                        DataWatcherRegistry.f,
//                        true
//                )
//        );
//        watcher.setObject(wdwo, Optional.of(cct));
        return watcher;
    }

    public static PacketContainer getLookPacket(int entityId, double pitch, double yaw, boolean onGround) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        packet.getModifier().writeDefaults();
        packet.getModifier().write(0, entityId);
        packet.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
        packet.getBytes().write(1, (byte) (pitch * 256.0F / 360.0F));
        packet.getBooleans().write(0, onGround);
        return packet;
    }

    public static PacketContainer getHeadRotationPacket(int entityId, double yaw) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        packet.getModifier().writeDefaults();
        packet.getModifier().write(0, entityId);
        packet.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
        return packet;
    }

    public static void openBook(final Player player, final EquipmentSlot hand) {
        final ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) (hand == EquipmentSlot.HAND ? 0 : 1)); // hand
        buf.writerIndex(1);
        final PacketPlayOutCustomPayload payload = new PacketPlayOutCustomPayload(
                new MinecraftKey("minecraft:book_open"),
                new PacketDataSerializer(buf)
        );
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(payload);
    }

    public static class FakeEntity {

        private int entityId;
        private UUID uniqueId;
        private EntityType type;
        private int typeId;
        private boolean object;
        private int objectData;

        public static FakeEntity createNew(int entityId, EntityType type, int objectData) {
            FakeEntity out = new FakeEntity();
            out.entityId = entityId;
            out.uniqueId = UUID.randomUUID();
            out.type = type;
            out.typeId = objects.containsKey(type) ? objects.get(type) : type.ordinal();
            out.object = objects.containsKey(type);
            out.objectData = objectData;
            return out;
        }

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
