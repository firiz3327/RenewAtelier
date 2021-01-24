package net.firiz.renewatelier.version.inject;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.event.AsyncPlayerInteractEntityEvent;
import net.firiz.renewatelier.version.MinecraftVersion;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Field;

public class ChannelHandler extends ChannelDuplexHandler {

    private final EntityPlayer player;
    final Field a;

    @MinecraftVersion("1.16")
    public ChannelHandler(EntityPlayer player) {
        this.player = player;
        try {
            a = PacketPlayInUseEntity.class.getDeclaredField("a");
            a.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("initialize error channel handler.");
        }
    }

    @Override
    @MinecraftVersion("1.16")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("[READ] " + msg.getClass().getSimpleName());
        if (msg instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity packet = (PacketPlayInUseEntity) msg;
            final int mobId = a.getInt(packet);
            final EnumHand enumHand = packet.c();
            final EquipmentSlot hand = enumHand == EnumHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
            final AsyncPlayerInteractEntityEvent event = new AsyncPlayerInteractEntityEvent(
                    player.getBukkitEntity(),
                    mobId,
                    enumHand != null,
                    hand
            );
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

}
