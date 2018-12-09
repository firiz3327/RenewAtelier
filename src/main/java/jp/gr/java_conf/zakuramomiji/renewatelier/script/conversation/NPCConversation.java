/*
 * QuestConversation.java
 * 
 * Copyright (c) 2018 firiz.
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
package jp.gr.java_conf.zakuramomiji.renewatelier.script.conversation;

import com.comphenix.protocol.events.PacketContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.packet.PacketUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.packet.PacketUtils.FakeEntity;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public final class NPCConversation extends ScriptConversation {

    private final LivingEntity npc;
    private final Map<Player, List<FakeEntity>> entitys;
    private int index;

    public NPCConversation(LivingEntity npc, String scriptName, Player player, Object iv) {
        super(scriptName, player, iv);
        this.npc = npc;
        this.entitys = new HashMap<>();
        this.index = 0;
    }

    public void sendNext(final String text) {
        messagePacket(text, 3000);
        index++;
    }

    private void messagePacket(final String text, final int time) {
        final Location balloonLoc = npc.getEyeLocation();
        balloonLoc.setY(balloonLoc.getY() + 0.5);
        player.sendMessage(text);
        final DoubleData<Object, Class<?>> broadcastMessage = broadcastMessage(
                MessageType.FAKE_ENTITY,
                player,
                EntityType.ARMOR_STAND,
                balloonLoc,
                text
        );
        Bukkit.getScheduler().runTaskLater(
                AtelierPlugin.getPlugin(),
                () -> {
                    PacketUtils.getDespawnPacket(
                            (FakeEntity) broadcastMessage.getLeft()
                    );
                },
                time
        );
        index++;
    }

    private DoubleData<Object, Class<?>> broadcastMessage(final MessageType type, final Object... datas) {
        return type.run(this, datas);
    }

    private enum MessageType {
        FAKE_ENTITY((NPCConversation conv, Object... args) -> {
            // https://wiki.vg/Protocol#Spawn_Mob
            final FakeEntity fakeEntity = FakeEntity.createNew((EntityType) args[0]);
            if (conv.entitys.containsKey(conv.player)) {
                conv.entitys.get(conv.player).add(fakeEntity);
            } else {
                conv.entitys.put(conv.player, new ArrayList<>() {
                    {
                        add(fakeEntity);
                    }
                });
            };
            final PacketContainer packet1 = PacketUtils.getSpawnPacket(
                    fakeEntity,
                    (Location) args[1]
            );
            PacketUtils.sendPacket(conv.player, packet1);

            // https://wiki.vg/Entity_metadata#Entity_Metadata_Format
            final PacketContainer packet2 = PacketUtils.getMetadataPacket(
                    fakeEntity,
                    PacketUtils.setEntityCustomName(PacketUtils.createWatcher(new HashMap<>() {
                        {
                            put(0, (byte) (0x20)); // invisible
                            put(3, true); // customname visible
                        }
                    }), (String) args[2])
            );
            PacketUtils.sendPacket(conv.player, packet2);

            return new DoubleData<>(fakeEntity, FakeEntity.class);
        }, EntityType.class, Location.class, String.class),
        DESTROY_ENTITY((NPCConversation conv, Object... args) -> {
            final Player player = (Player) args[0];
            final FakeEntity entity = (FakeEntity) args[1];
            if (conv.entitys.containsKey(player)) {
                final List<FakeEntity> p_entitys = conv.entitys.get(player);
                if (p_entitys.contains(entity)) {
                    PacketUtils.getDespawnPacket(entity);
                }
            }
            return new DoubleData<>(null, ResultEmpty.class);
        }, Player.class, FakeEntity.class);

        private final MessageRunnable run;
        private final Class<?>[] clasz;

        private MessageType(MessageRunnable run, Class<?>... clasz) {
            this.run = run;
            this.clasz = clasz;
        }

        public DoubleData<Object, Class<?>> run(final NPCConversation conv, final Object... args) {
            if (clasz.length != args.length) {
                return null;
            }
            for (int i = 0; i < clasz.length; i++) {
                if (clasz[i] != args[i].getClass()) {
                    return null;
                }
            }
            return run.run(conv, args);
        }
    }

    private interface MessageRunnable {

        public DoubleData<Object, Class<?>> run(NPCConversation conv, Object... args);
    }

    private interface ResultEmpty {
    }
}
