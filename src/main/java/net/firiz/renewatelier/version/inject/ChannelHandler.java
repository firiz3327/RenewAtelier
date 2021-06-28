package net.firiz.renewatelier.version.inject;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.renewatelier.server.event.AsyncPlayerInteractEntityEvent;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import org.bukkit.Bukkit;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Field;
import java.util.Optional;

public class ChannelHandler extends ChannelDuplexHandler {

    private final EntityPlayer player;
    final Field a;

    @MinecraftVersion("1.17")
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
    @MinecraftVersion("1.17")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("[READ] " + msg.getClass().getSimpleName());
        if (msg instanceof final PacketPlayInUseEntity packet) {
            final int mobId = a.getInt(packet);
            final var b = VersionUtils.getFieldValue(PacketPlayInUseEntity.class, packet, "b");
            final Optional<Field> aField = VersionUtils.getFieldNoSuchField(b, "a");
            if (aField.isPresent()) {
                final EnumHand enumHand = (EnumHand) VersionUtils.getFieldValue(aField.get(), b);
                final EquipmentSlot hand = enumHand == EnumHand.a ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
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
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

}
