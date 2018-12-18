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
import java.util.HashMap;
import javax.script.Invocable;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.npc.NPCManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.packet.PacketUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.packet.PacketUtils.FakeEntity;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author firiz
 */
public final class NPCConversation extends ScriptConversation {

    private final LivingEntity npc;
    private FakeEntity fakeEntity;
    private BukkitTask runTaskLater;

    public NPCConversation(LivingEntity npc, String scriptName, Player player) {
        super(scriptName, player);
        this.npc = npc;
    }

    public Invocable getIv() {
        return iv;
    }

    public LivingEntity getNPC() {
        return npc;
    }
    
    public String getNPCName() {
        return npc.getCustomName();
    }

    public void dispose() {
        NPCManager.INSTANCE.dispose(player.getUniqueId());
    }
    
    public void sendNext(final String text) {
        sendNext("", text);
    }
    
    public void sendNext(final String prefix, final String text) {
        sendNext(prefix, "", text);
    }
    public void sendNext(final String prefix, final String suffix, final String text) {
        sendNext(prefix, suffix, text, 3000);
    }

    public void sendNext(final String prefix, final String suffix, final String text, final int time) {
        player.sendMessage(prefix + text + suffix);
        messagePacket(chatColor(text), 20 * 3);
    }

    private void messagePacket(final String text, final int time) {
        final Location balloonLoc = npc.getLocation();
        balloonLoc.setY(balloonLoc.getY() + 0.2);

        broadcastMessage(
                MessageType.FAKE_ENTITY,
                EntityType.ARMOR_STAND,
                balloonLoc,
                text
        );
        if (runTaskLater != null) {
            runTaskLater.cancel();
        }
        runTaskLater = Bukkit.getScheduler().runTaskLater(
                AtelierPlugin.getPlugin(),
                () -> {
                    broadcastMessage(
                            MessageType.DESTROY_FAKE_ENTITY
                    );
                },
                time
        );
    }

    private DoubleData<Object, Class<?>> broadcastMessage(final MessageType type, final Object... datas) {
        return type.run(this, datas);
    }

    private enum MessageType {
        FAKE_ENTITY((NPCConversation conv, Object... args) -> {
            // https://wiki.vg/Protocol#Spawn_Mob
            final FakeEntity fakeEntity = FakeEntity.createNew(-1, (EntityType) args[0], 0);
            conv.fakeEntity = fakeEntity;

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
        DESTROY_FAKE_ENTITY((NPCConversation conv, Object... args) -> {
            PacketUtils.sendPacket(conv.player, PacketUtils.getDespawnPacket(conv.fakeEntity));
            return new DoubleData<>(null, ResultEmpty.class);
        });

        private final MessageRunnable run;
        private final Class<?>[] clasz;

        private MessageType(MessageRunnable run, Class<?>... clasz) {
            this.run = run;
            this.clasz = clasz;
        }

        public DoubleData<Object, Class<?>> run(final NPCConversation conv, final Object... args) {
            if (clasz.length != args.length) {
                throw new IllegalArgumentException("The value of args Length is different.");
            }
            for (int i = 0; i < clasz.length; i++) {
                if (!clasz[i].isInstance(args[i])) {
                    throw new IllegalArgumentException("The argument class is different.");
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
