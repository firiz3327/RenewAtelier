package net.firiz.renewatelier.version.inject;

import io.netty.channel.Channel;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
//        injectArmorChangeEvent(player);
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

    /*
    @MinecraftVersion("1.15")
    private static void injectArmorChangeEvent(EntityPlayer player) {
        try {
            final Field armorField = PlayerInventory.class.getDeclaredField("armor");
            armorField.setAccessible(true);
            final NonNullList<ItemStack> armorList = (NonNullList<ItemStack>) armorField.get(player.inventory);
            final Field a = NonNullList.class.getDeclaredField("a");
            a.setAccessible(true);

            // reload時に更新処理が入らないためjoin時のみinjectすれば問題ない
            a.set(armorList, new ObjectArrayList<>((List<ItemStack>) a.get(armorList)) {
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
            CommonUtils.log("inject successfully");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            CommonUtils.logWarning("It may have been changed by the version upgrade.", e);
        }
    }
     */

}
