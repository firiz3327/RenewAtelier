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
//        if (!(msg instanceof PacketPlayOutUpdateAttributes
//                || msg instanceof PacketPlayOutEntityHeadRotation
//                || msg instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove
//                || msg instanceof PacketPlayOutEntityVelocity
//                || msg instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook
//                || msg instanceof PacketPlayOutEntityTeleport
//                || msg instanceof PacketPlayOutUpdateTime
//                || msg instanceof PacketPlayOutLightUpdate
//                || msg instanceof PacketPlayOutSpawnEntityLiving
//                || msg instanceof PacketPlayOutEntityDestroy
//                || msg instanceof PacketPlayOutMapChunk
//                || msg instanceof PacketPlayOutWorldEvent)) {
//            System.out.println("[WRITE] " + msg.getClass().getSimpleName());
//        }
//        if (msg instanceof PacketPlayOutWindowItems) {
//            final int windowId = CommonUtils.castInt(VersionUtils.getFieldValue(PacketPlayOutWindowItems.class, msg, "a"));
//            final List<net.minecraft.server.v1_16_R1.ItemStack> items = CommonUtils.cast(VersionUtils.getFieldValue(PacketPlayOutWindowItems.class, msg, "b"));
//            System.out.println(windowId + " " + player.activeContainer.windowId);
//            if (items != null
//                    && windowId == player.activeContainer.windowId
//                    && player.activeContainer instanceof ContainerLectern
//                    && items.size() == 1) {
//                final ItemStack item = VersionUtils.asItem(items.get(0)).clone();
//                final AlchemyMaterial material = QuestBook.getQuestBookMaterial(item);
//                if (material != null) {
//                    final BookMeta meta = (BookMeta) item.getItemMeta();
//                    QuestBook.changeMeta(player.getBukkitEntity(), meta);
//                    item.setItemMeta(meta);
//                }
//                items.set(0, VersionUtils.asNMSCopy(item));
//                System.out.println(VersionUtils.asItem(items.get(0)));
//            }
//        } else if (msg instanceof PacketPlayOutSetSlot) {
//            final int windowId = CommonUtils.castInt(VersionUtils.getFieldValue(PacketPlayOutSetSlot.class, msg, "a"));
//            final int slot = CommonUtils.castInt(VersionUtils.getFieldValue(PacketPlayOutSetSlot.class, msg, "b"));
//            final ItemStack item = VersionUtils.asItem((net.minecraft.server.v1_16_R1.ItemStack) VersionUtils.getFieldValue(PacketPlayOutSetSlot.class, msg, "c")).clone();
////            System.out.println(windowId + " " + player.activeContainer.windowId);
////            System.out.println(slot);
////            System.out.println(item);
//            if (windowId == player.activeContainer.windowId
//                    && player.activeContainer instanceof ContainerLectern
//                    && slot == 0
//                    && item.getType() == Material.WRITTEN_BOOK) {
//                final AlchemyMaterial material = QuestBook.getQuestBookMaterial(item);
//                if (material != null) {
//                    final BookMeta meta = (BookMeta) item.getItemMeta();
//                    QuestBook.changeMeta(player.getBukkitEntity(), meta);
//                    item.setItemMeta(meta);
//                    VersionUtils.setFieldValue(PacketPlayOutSetSlot.class, msg, "c", VersionUtils.asNMSCopy(item));
//                }
//            }
//        }
//        if(msg instanceof PacketPlayOutTileEntityData) {
//            final BlockPosition pos = (BlockPosition) VersionUtils.getFieldValue(PacketPlayOutTileEntityData.class, msg, "a");
//            final int b = CommonUtils.castInt(VersionUtils.getFieldValue(PacketPlayOutTileEntityData.class, msg, "b"));
//            final NBTTagCompound nbt = (NBTTagCompound) VersionUtils.getFieldValue(PacketPlayOutTileEntityData.class, msg, "c");
//            CommonUtils.log(pos);
//            CommonUtils.log(b);
//            CommonUtils.log(nbt);
//        }
        super.write(ctx, msg, promise);
    }

}
