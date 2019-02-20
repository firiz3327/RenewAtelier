package jp.gr.java_conf.zakuramomiji.renewatelier.version.packet;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PacketUtils {

    private static ProtocolManager manager;

    public static void init(final ProtocolManager manager) {
        PacketUtils.manager = manager;
        EntityPacket.init(manager);

        /*
        manager.addPacketListener(new PacketAdapter(
                AtelierPlugin.getPlugin(),
                PacketType.Play.Server.ADVANCEMENTS
        ) {
            @Override
            public void onPacketSending(PacketEvent e) {
                for (Object v : e.getPacket().getModifier().getValues()) {
                    System.out.print(v.getClass() + " ： ");
                    if (v instanceof Map) {
                        System.out.println();
                        Map map = (Map) v;
                        for (Object o : map.keySet()) {
                            Object val = map.get(o);
                            if (val instanceof AdvancementProgress) {
                                for (Field f : val.getClass().getDeclaredFields()) {
                                    f.setAccessible(true);
                                    try {
                                        System.out.println(f.getName() + " ---");
                                        Object v2 = f.get(val);
                                        if (v2 instanceof Map) {
                                            Map<?, ?> a = (Map<?, ?>) v2;
                                            for (Object k : a.keySet()) {
                                                System.out.println("  " + k + " ---");
                                                Object z = a.get(k);
                                                for (Field f2 : z.getClass().getDeclaredFields()) {
                                                    f2.setAccessible(true);
                                                    Object v3 = f2.get(z);
                                                    System.out.print("    " + f2.getName() + " - ");
                                                    if (v3 instanceof SimpleDateFormat) {
                                                        System.out.println(((SimpleDateFormat) v3).toPattern());
                                                    } else {
                                                        System.out.println(v3);
                                                    }
                                                }
                                            }
                                        } else {
                                            for (String[] v3 : (String[][]) v2) {
                                                for (String v4 : v3) {
                                                    System.out.println("  " + v4);
                                                }
                                            }
                                        }
                                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                                        Logger.getLogger(PacketUtils.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println(v);
                    }
                    System.out.println("--------------");
                }
            }
        });
        */
    }

    public static void sendPacket(final Player player, final PacketContainer packet) {
        try {
            manager.sendServerPacket(player, packet);
        } catch (InvocationTargetException ex) {
            Chore.log(ex);
        }
    }

    public static void sendPacket(final Player player, final Packet<?> packet) {
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(packet);
    }

    public static void sendPackets(final Player player, final Packet<?>... packets) {
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        for (final Packet<?> packet : packets) {
            playerConnection.sendPacket(packet);
        }
    }

    public static void sendPackets(final PlayerConnection playerConnection, final Packet<?>... packets) {
        for (final Packet<?> packet : packets) {
            playerConnection.sendPacket(packet);
        }
    }

    public static WrappedDataWatcher createWatcher(final Map<Integer, Object> data) {
        final WrappedDataWatcher watcher = new WrappedDataWatcher();
        for (final Map.Entry<Integer, Object> e : data.entrySet()) {
            if (e.getValue().getClass() == ItemStack.class) {
                watcher.setObject(
                        e.getKey(),
                        WrappedDataWatcher.Registry.getItemStackSerializer(false),
                        CraftItemStack.asNMSCopy((ItemStack) e.getValue())
                );
                continue;
            }
            watcher.setObject(
                    e.getKey(),
                    WrappedDataWatcher.Registry.get(e.getValue().getClass()),
                    e.getValue()
            );
        }
        return watcher;
    }

    public static int getPing(final Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

}
