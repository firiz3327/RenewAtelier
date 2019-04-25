package jp.gr.java_conf.zakuramomiji.renewatelier.version.packet;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.primitives.Primitives;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PacketUtils {

    protected static final Map<String, DataWatcherSerializer<?>> dataWatcherSerializers;
    protected static final Map<String, String> dataWatchers;

    static {
        dataWatcherSerializers = new HashMap<>();
        dataWatchers = new HashMap<>();

        for (final Field f : DataWatcherRegistry.class.getFields()) {
            try {
                dataWatcherSerializers.put(f.getName(), (DataWatcherSerializer<?>) f.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (final Field f : DataWatcherRegistry.class.getFields()) {
            final Class<?> generic = getGenericType(f);
            if (generic != null && generic.getSimpleName() != null) {
                dataWatchers.put(generic.getSimpleName(), f.getName());
            }
        }
        dataWatchers.put("oIChatBaseComponent", "f");
        dataWatchers.put("oChatBaseComponentText", "f");
        dataWatchers.put("oIBlockData", "h");
        dataWatchers.put("oBlockData", "h");
        dataWatchers.put("oBlockPosition", "m");
        dataWatchers.put("oUUID", "o");
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

    public static DataWatcher createWatcher(final Map<Integer, Object> data) {
        final DataWatcher watcher = new DataWatcher(null);
        for (final Map.Entry<Integer, Object> e : data.entrySet()) {
            if (e.getValue().getClass() == ItemStack.class) {
                watcher.set(
                        DataWatcherRegistry.g.a(e.getKey()),
                        CraftItemStack.asNMSCopy((ItemStack) e.getValue())
                );
                continue;
            }
            watcher.set(
                    getObject(e.getValue(), e.getKey().byteValue()),
                    e.getValue()
            );
        }
        return watcher;
    }

    public static int getPing(final Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    protected static Field getField(Packet<?> packet, String name) {
        try {
            final Field field = packet.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static void setField(Packet<?> packet, String name, Object value) {
        try {
            getField(packet, name).set(packet, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected static Class<?> getGenericType(Field f) {
        final ParameterizedType t = (ParameterizedType) f.getGenericType();
        final Type type = t.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    protected static <T> Optional<DataWatcherSerializer<T>> getSerializer(T val) {
        if (Optional.class.isAssignableFrom(val.getClass())) {
            final Optional optional = (Optional) val;
            if (optional.isPresent()) {
                final Object object = optional.get();
                final DataWatcherSerializer<T> serializer = (DataWatcherSerializer<T>) dataWatcherSerializers.get(dataWatchers.get("o" + object.getClass().getSimpleName()));
                return Optional.of(serializer);
            }
        } else {
            final DataWatcherSerializer<T> serializer = (DataWatcherSerializer<T>) dataWatcherSerializers.get(dataWatchers.get(Primitives.wrap(val.getClass()).getSimpleName()));
            return Optional.of(serializer);
        }
        return Optional.empty();
    }

    protected static <T> DataWatcherObject<T> getObject(T val, byte id) {
        final Optional<DataWatcherSerializer<T>> dataWatcherSerializer = getSerializer(val);
        return dataWatcherSerializer.map(tDataWatcherSerializer -> tDataWatcherSerializer.a(id)).orElse(null);
    }

}
