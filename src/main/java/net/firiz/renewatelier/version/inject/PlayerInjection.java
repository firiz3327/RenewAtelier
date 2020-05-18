package net.firiz.renewatelier.version.inject;

import io.netty.channel.Channel;
import net.firiz.renewatelier.event.PlayerArmorChangeEvent;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerInjection {

    private static final String KEY_HANDLER = "packet_handler";
    private static final String KEY_PLAYER = "packet_listener_player";
    private static final String KEY_SERVER = "packet_listener_server";
    private static final Executor addChannelExecutor = Executors.newSingleThreadExecutor();

    public static void inject(Player bukkitPlayer) {
        final EntityPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        injectPacketPipeline(player);
        injectArmorChangeEvent(player);
    }

    private static void injectPacketPipeline(EntityPlayer player) {
        removePacketPipeline(player);
        final NetworkManager networkManager = player.playerConnection.networkManager;

        // netty
        final Channel channel = networkManager.channel;
        addChannelExecutor.execute(() -> {
            try {
                channel.pipeline().addBefore(KEY_HANDLER, KEY_PLAYER, new ChannelHandler(player));
            } catch (Exception var2) {
                throw new RuntimeException(var2);
            }
        });
    }

    private static void removePacketPipeline(EntityPlayer player) {
        final NetworkManager networkManager = player.playerConnection.networkManager;
        final Channel channel = networkManager.channel;
        if (channel.pipeline().get(ChannelHandler.class) != null) {
            channel.pipeline().remove(ChannelHandler.class);
        }
    }

    private static void injectArmorChangeEvent(EntityPlayer player) {
        try {
            final Field armorField = PlayerInventory.class.getDeclaredField("armor");
            armorField.setAccessible(true);
            final NonNullList<ItemStack> armorList = (NonNullList<ItemStack>) armorField.get(player.inventory);
            final Field a = NonNullList.class.getDeclaredField("a");
            a.setAccessible(true);

            // reload時に更新処理が入らないためjoin時のみinjectすれば問題ない
            a.set(armorList, new ArrayList<>((List<ItemStack>) a.get(armorList)) {
                @Override
                public boolean add(ItemStack itemStack) {
                    final boolean result = super.add(itemStack);
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player.getBukkitEntity(), VersionUtils.asItem(itemStack), PlayerArmorChangeEvent.ChangeType.ADD));
                    return result;
                }

                @Override
                public ItemStack set(int slot, ItemStack itemStack) {
                    final ItemStack result = super.set(slot, itemStack);
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player.getBukkitEntity(), VersionUtils.asItem(itemStack), PlayerArmorChangeEvent.ChangeType.SET));
                    return result;
                }

                @Override
                public ItemStack remove(int slot) {
                    final ItemStack result = super.remove(slot);
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player.getBukkitEntity(), VersionUtils.asItem(result), PlayerArmorChangeEvent.ChangeType.REMOVE));
                    return result;
                }

                @Override
                public void clear() {
                    super.clear();
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player.getBukkitEntity(), null, PlayerArmorChangeEvent.ChangeType.CLEAR));
                }
            });
            Chore.log("inject successfully");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Chore.logWarning("It may have been changed by the version upgrade.", e);
        }
    }

}
