package net.firiz.renewatelier.version.inject;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.event.AsyncPlayerInteractEntityEvent;
import net.firiz.renewatelier.version.MinecraftVersion;
import net.minecraft.server.v1_16_R1.*;
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
        } else if (msg instanceof PacketPlayInBlockDig
                && ArrowManager.INSTANCE.handleDigPacketCrossbow(player.getBukkitEntity())) {
            return;
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        System.out.println("[WRITE] " + msg.getClass().getSimpleName());
        /*
        if (msg instanceof PacketPlayOutEntity) {
            final Field entityIdField = VersionUtils.getField(PacketPlayOutEntity.class, "a");
            final Entity entity = player.world.getEntity((Integer) VersionUtils.getFieldValue(entityIdField, msg));
            if (entity instanceof Puni) {
                final Entity block = ((Puni) entity).getBlock();
                if (msg instanceof PacketPlayOutEntity.PacketPlayOutEntityLook) {
                    player.playerConnection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(
                            block.getId(),
                            (byte) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "e"),
                            (byte) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "f"),
                            (boolean) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "g")
                    ));
                } else if (msg instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove) {
                    player.playerConnection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                            block.getId(),
                            (short) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "b"),
                            (short) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "c"),
                            (short) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "d"),
                            (boolean) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "g")
                    ));
                } else if (msg instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
                    player.playerConnection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                            block.getId(),
                            (short) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "b"),
                            (short) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "c"),
                            (short) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "d"),
                            (byte) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "e"),
                            (byte) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "f"),
                            (boolean) VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "g")
                    ));
                } else if (msg.getClass() == PacketPlayOutEntity.class) {
                    final PacketPlayOutEntity packetEntity = new PacketPlayOutEntity(block.getId());
                    VersionUtils.setFieldValue(PacketPlayOutEntity.class, packetEntity, "b", VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "b"));
                    VersionUtils.setFieldValue(PacketPlayOutEntity.class, packetEntity, "c", VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "c"));
                    VersionUtils.setFieldValue(PacketPlayOutEntity.class, packetEntity, "d", VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "d"));
                    VersionUtils.setFieldValue(PacketPlayOutEntity.class, packetEntity, "e", VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "e"));
                    VersionUtils.setFieldValue(PacketPlayOutEntity.class, packetEntity, "f", VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "f"));
                    VersionUtils.setFieldValue(PacketPlayOutEntity.class, packetEntity, "g", VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "g"));
                    VersionUtils.setFieldValue(PacketPlayOutEntity.class, packetEntity, "h", VersionUtils.getFieldValue(PacketPlayOutEntity.class, msg, "h"));
                    player.playerConnection.sendPacket(packetEntity);
                }
            }
        } else if (msg instanceof PacketPlayOutEntityVelocity) {
            final Entity entity = player.world.getEntity((Integer) VersionUtils.getFieldValue(PacketPlayOutEntityVelocity.class, msg, "a"));
            if (entity instanceof Puni) {
                final EntityFallingBlock block = ((Puni) entity).getBlock();
                final PacketPlayOutEntityVelocity packetEntity = new PacketPlayOutEntityVelocity(block);
                VersionUtils.setFieldValue(PacketPlayOutEntityVelocity.class, packetEntity, "b", VersionUtils.getFieldValue(PacketPlayOutEntityVelocity.class, msg, "b"));
                VersionUtils.setFieldValue(PacketPlayOutEntityVelocity.class, packetEntity, "c", VersionUtils.getFieldValue(PacketPlayOutEntityVelocity.class, msg, "c"));
                VersionUtils.setFieldValue(PacketPlayOutEntityVelocity.class, packetEntity, "d", VersionUtils.getFieldValue(PacketPlayOutEntityVelocity.class, msg, "d"));
                player.playerConnection.sendPacket(packetEntity);
            }
        }
        */
        super.write(ctx, msg, promise);
    }

}
