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

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import java.util.*;

/**
 * @author firiz
 */
public class EntityPacket {

    public static Packet<?> getSpawnPacket(FakeEntity entity, Location loc) {
        return new PacketPlayOutSpawnEntity(
                entity.getEntityId(),
                entity.getUniqueId(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getPitch(),
                loc.getYaw(),
                entity.getType().getTypeNms(),
                (int) loc.getYaw(),
                new Vec3D(0, 0, 0)
        );
    }

    public static Packet<?> getDespawnPacket(int... entityIds) {
        return new PacketPlayOutEntityDestroy(entityIds);
    }

    public static Packet<?> getLookPacket(int entityId, double pitch, double yaw, boolean onGround) {
        return new PacketPlayOutEntity.PacketPlayOutEntityLook(
                entityId,
                (byte) (yaw * 256.0F / 360.0F),
                (byte) (pitch * 256.0F / 360.0F),
                onGround
        );
    }

    public static Packet<?> getHeadRotationPacket(int entityId, double yaw) {
        final PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        PacketUtils.setField(packet, "a", entityId);
        PacketUtils.setField(packet, "b", (byte) (yaw * 256.0F / 360.0F));
        return packet;
    }

    public static DataWatcher setEntityCustomName(DataWatcher watcher, String name) {
        final ChatComponentText wrap = new ChatComponentText("");
        final ChatComponentText text = new ChatComponentText(name);
        wrap.addSibling(text);
        watcher.set(DataWatcherRegistry.f.a(0), Optional.of(wrap));
        return watcher;
    }

    public static <T> Packet<?> getMetadataPacket(FakeEntity entity, byte id, T metadata) {
        final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
        PacketUtils.setField(packet, "a", entity.getEntityId());
        PacketUtils.setField(packet, "b", Collections.singletonList(
                new DataWatcher.Item<>(PacketUtils.getObject(metadata, id), metadata)
        ));
        return packet;
    }

}
