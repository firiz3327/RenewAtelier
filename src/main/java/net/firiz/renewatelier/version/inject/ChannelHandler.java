package net.firiz.renewatelier.version.inject;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.entity.atelier.Puni;
import net.minecraft.server.v1_15_R1.*;

import java.lang.reflect.Field;

public class ChannelHandler extends ChannelDuplexHandler {

    private final EntityPlayer player;

    public ChannelHandler(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println(msg.getClass().getSimpleName());
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
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
        super.write(ctx, msg, promise);
    }

}
