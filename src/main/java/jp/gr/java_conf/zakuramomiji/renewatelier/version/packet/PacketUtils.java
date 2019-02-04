package jp.gr.java_conf.zakuramomiji.renewatelier.version.packet;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.nms.VItemStack;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PacketUtils {

    private static ProtocolManager manager;

    public static void init(final ProtocolManager manager) {
        PacketUtils.manager = manager;
        EntityPacket.init(manager);
    }

    public static void sendPacket(final Player player, final PacketContainer packet) {
        try {
            manager.sendServerPacket(player, packet);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PacketUtils.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public static VItemStack asNMSCopy(final ItemStack item) {
        return new VItemStack(CraftItemStack.asNMSCopy(item));
    }

}
