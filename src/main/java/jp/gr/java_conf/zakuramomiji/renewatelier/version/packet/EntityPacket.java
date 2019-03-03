/*
 * SpawnPacket.java
 * 
 * Copyright (c) 2019 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package jp.gr.java_conf.zakuramomiji.renewatelier.version.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.loop.LoopManager;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.ChatModifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public class EntityPacket {

    private static Runnable run;
    private static WrappedWatchableObject wwo;

    protected static void init(final ProtocolManager manager) {
        // 名前付き雪玉をスポーンさせてWrappedWatchableObjectをMinecraft側で生成してもらう
        manager.addPacketListener(new PacketAdapter(AtelierPlugin.getPlugin(), PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent arg0) {
                if (wwo == null) {
                    final List<WrappedWatchableObject> values = arg0.getPacket().getWatchableCollectionModifier().getValues().get(0);
                    values.stream().filter((s) -> (s.getIndex() == 2)).forEachOrdered((s) -> {
                        final Optional opt = (Optional) s.getValue();
                        if (opt.isPresent()) {
                            wwo = s;
                        }
                    });
                }
            }
        });
        /*
            プラグイン有効化時、プレイヤーが存在する場合、
            名前付き雪玉をスポーンさせループから終了するループを追加
         */
        run = () -> {
            if (wwo == null) {
                final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                if(!players.isEmpty()) {
                    final Player player = players.iterator().next();
                    player.getWorld()
                            .spawnEntity(player.getLocation(), EntityType.SNOWBALL)
                            .setCustomName("INIT");
                    LoopManager.INSTANCE.removeLoopEffect(run);
                }
            }
        };
        LoopManager.INSTANCE.addLoopEffect(run);
    }

    public static PacketContainer getSpawnPacket(FakeEntity fakeEntity, Location location) {
        return getSpawnPacket(fakeEntity, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw(), new WrappedDataWatcher());
    }

    public static PacketContainer getSpawnPacket(FakeEntity fakeEntity, Location location, WrappedDataWatcher metadata) {
        return getSpawnPacket(fakeEntity, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw(), metadata);
    }

    public static PacketContainer getSpawnPacket(FakeEntity entity, double x, double y, double z, double pitch, double yaw, WrappedDataWatcher metadata) {
        final PacketContainer packet = new PacketContainer(entity.isObject() ? PacketType.Play.Server.SPAWN_ENTITY : PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getModifier().writeDefaults();
        packet.getUUIDs().write(0, entity.getUniqueId());
        packet.getDoubles().write(0, x);
        packet.getDoubles().write(1, y);
        packet.getDoubles().write(2, z);
        if (entity.isObject()) {
            packet.getIntegers().write(6, entity.getTypeId());
            packet.getIntegers().write(4, (int) (pitch * 256.0F / 360.0F));
            packet.getIntegers().write(5, (int) (yaw * 256.0F / 360.0F));
            packet.getIntegers().write(7, entity.getObjectData());
        } else {
            packet.getIntegers().write(1, entity.getType().ordinal());
            packet.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
            packet.getBytes().write(1, (byte) (pitch * 256.0F / 360.0F));
            packet.getBytes().write(2, (byte) (pitch * 256.0F / 360.0F));
            packet.getIntegers().write(2, 0);
            packet.getIntegers().write(3, 0);
            packet.getIntegers().write(4, 0);
            packet.getDataWatcherModifier().write(0, metadata);
        }
        return packet;
    }

    public static PacketContainer getMetadataPacket(FakeEntity entity, WrappedDataWatcher metadata) {
        return getMetadataPacket(entity, metadata.getWatchableObjects());
    }

    public static PacketContainer getMetadataPacket(FakeEntity entity, List<WrappedWatchableObject> metadata) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packet.getModifier().writeDefaults();
        packet.getModifier().write(0, entity.getEntityId());
        packet.getWatchableCollectionModifier().write(0, metadata);
        return packet;
    }

    public static PacketContainer getPassengerPacket(FakeEntity entity, int[] passengers) {
//        final PacketContainer packet = manager.createPacket(PacketType.Play.Server.MOUNT);
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.MOUNT);
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getIntegerArrays().write(0, passengers);
        return packet;
    }

    public static PacketContainer getDespawnPacket(FakeEntity... entities) {
        final int[] entityIds = new int[entities.length];
        for (int i = 0; i < entities.length; i++) {
            entityIds[i] = entities[i].getEntityId();
        }
        return getDespawnPacket(entityIds);
    }

    public static PacketContainer getDespawnPacket(int... entityIds) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getModifier().writeDefaults();
        packet.getIntegerArrays().write(0, entityIds);
        return packet;
    }

    public static PacketContainer getLookPacket(int entityId, double pitch, double yaw, boolean onGround) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        packet.getModifier().writeDefaults();
        packet.getModifier().write(0, entityId);
        packet.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
        packet.getBytes().write(1, (byte) (pitch * 256.0F / 360.0F));
        packet.getBooleans().write(0, onGround);
        return packet;
    }

    public static PacketContainer getHeadRotationPacket(int entityId, double yaw) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        packet.getModifier().writeDefaults();
        packet.getModifier().write(0, entityId);
        packet.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
        return packet;
    }

    public static WrappedDataWatcher setEntityCustomName(WrappedDataWatcher watcher, String name) {
        final ChatComponentText tcct = (ChatComponentText) ((Optional) wwo.getValue()).get();
        ChatComponentText v = new ChatComponentText(name);
        ChatModifier cm = new ChatModifier();
        cm.setChatModifier(tcct.getChatModifier());
        v.setChatModifier(cm);
        tcct.a().set(0, v);
        watcher.setObject(wwo.getWatcherObject(), wwo.getValue());
        //<editor-fold defaultstate="collapsed" desc="crash">
//        watcher.setObject(
//                2,
//                WrappedDataWatcher.Registry.getChatComponentSerializer(true),
//                Optional.of(cct)
//        );
//        final ChatComponentText cct = new ChatComponentText("") {
//            {
//                final ChatModifier cctcm = new ChatModifier();
//                setChatModifier(cctcm);
//
//                final ChatComponentText namecct = new ChatComponentText(name);
//                namecct.setChatModifier(
//                        new ChatModifier()
//                                .setChatModifier(cctcm)
//                                .setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, "http://".concat(name)))
//                );
//                addSibling(namecct);
//            }
//        };
//        final WrappedDataWatcherObject wdwo = new WrappedDataWatcherObject(
//                2,
//                new WrappedDataWatcher.Serializer(
//                        IChatBaseComponent.class,
//                        DataWatcherRegistry.f,
//                        true
//                )
//        );
//        watcher.setObject(wdwo, Optional.of(cct));
        //</editor-fold>
        return watcher;
    }

}
