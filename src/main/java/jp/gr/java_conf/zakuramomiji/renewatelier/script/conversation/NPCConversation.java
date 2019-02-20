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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.script.Invocable;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.DeliveryInventory;
import jp.gr.java_conf.zakuramomiji.renewatelier.npc.NPCManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.PacketUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.FakeEntity;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.nms.VEntityPlayer;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.EntityPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private final VEntityPlayer npcPlayer; // EntityPlayer class
    private FakeEntity fakeEntity;
    private BukkitTask runTaskLater;

    public NPCConversation(LivingEntity npc, String scriptName, Player player) {
        super(scriptName, player);
        this.npc = npc;
        this.npcPlayer = null;
    }

    public NPCConversation(VEntityPlayer npcPlayer, String scriptName, Player player) {
        super(scriptName, player);
        this.npc = null;
        this.npcPlayer = npcPlayer;
    }

    public Invocable getIv() {
        return iv;
    }

    public LivingEntity getNPC() {
        return npc;
    }

    public VEntityPlayer getPlayerNPC() {
        return npcPlayer;
    }

    public String getNPCName() {
        return npc == null ? npcPlayer.getName() : npc.getCustomName();
    }

    public Location getLocation() {
        return (npc == null ? npcPlayer.getLocation() : npc.getLocation()).clone();
    }

    public void openDeliveryInventory(final String title, final int req_amount, final AlchemyMaterial material) {
        openDeliveryInventory(title, 2, req_amount, material);
    }

    public void openDeliveryInventory(final String title, final int line_size, final int req_amount, final AlchemyMaterial material) {
        openDeliveryInventory(title, line_size, req_amount, material, new ArrayList<>(), new ArrayList<>());
    }

    public void openDeliveryInventory(final String title, final int line_size, final int req_amount, final AlchemyMaterial material, final Characteristic[] characteristics, final AlchemyIngredients[] ingredients) {
        openDeliveryInventory(title, line_size, req_amount, material, Arrays.asList(characteristics), Arrays.asList(ingredients));
    }

    public void openDeliveryInventory(final String title, final int line_size, final int req_amount, final AlchemyMaterial material, final List<Characteristic> characteristics, final List<AlchemyIngredients> ingredients) {
        DeliveryInventory.openInventory(
                player,
                ChatColor.translateAlternateColorCodes('&', title),
                line_size,
                req_amount,
                material,
                characteristics,
                ingredients
        );
    }

    public void removeNPC(final String name, final String script, final double x, final double y, final double z, final EntityType type) {
        removeNPC(name, script, new Location(player.getWorld(), x, y, z), type);
    }

    public void removeNPC(final String name, final String script, final Location location, final EntityType type) {
        NPCManager.INSTANCE.removeNPC(location, type, name, script);
    }

    public void createNPC(final String name, final String script, final double x, final double y, final double z, final EntityType type) {
        createNPC(name, script, new Location(player.getWorld(), x, y, z), type);
    }

    public void createNPC(final String name, final String script, final Location location, final EntityType type) {
        NPCManager.INSTANCE.createNPC(location, type, name, script);
    }

    public void removePlayerNPC(final String name, final String script, final double x, final double y, final double z, final UUID uuid) {
        removePlayerNPC(name, script, new Location(player.getWorld(), x, y, z), uuid);
    }
    
    public void removePlayerNPC(final String name, final String script, final Location location, final UUID uuid) {
        NPCManager.INSTANCE.removeNPCPlayer(location, name, uuid, script);
    }

    public void createPlayerNPC(final String name, final String script, final double x, final double y, final double z, final UUID uuid) {
        createPlayerNPC(name, script, new Location(player.getWorld(), x, y, z), uuid);
    }

    public void createPlayerNPC(final String name, final String script, final Location location, final UUID uuid) {
        NPCManager.INSTANCE.createNPCPlayer(location, name, uuid, script);
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
        sendNext(prefix, suffix, text, 60); // 3 sec
    }

    public void sendNext(final String prefix, final String suffix, final String text, final int time) {
        player.sendMessage(prefix + text + suffix);
        messagePacket(chatColor(text), time);
    }

    private void messagePacket(final String text, final int time) {
        final Location balloonLoc = getLocation();
        balloonLoc.setY(balloonLoc.getY() + 0.25);

        MessageType.FAKE_ENTITY.boardcast(
                this,
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
                    MessageType.DESTROY_FAKE_ENTITY.boardcast(this);
                },
                time
        );
    }

    private enum MessageType {
        FAKE_ENTITY(EntityType.class, Location.class, String.class) {
            @Override
            FakeEntity run(NPCConversation conv, Object... args) {
                // https://wiki.vg/Protocol#Spawn_Mob
                final FakeEntity fakeEntity = new FakeEntity(-1, (EntityType) args[0], 0);
                conv.fakeEntity = fakeEntity;

                final PacketContainer packet1 = EntityPacket.getSpawnPacket(
                        fakeEntity,
                        (Location) args[1]
                );
                PacketUtils.sendPacket(conv.player, packet1);

                // https://wiki.vg/Entity_metadata#Entity_Metadata_Format
                final PacketContainer packet2 = EntityPacket.getMetadataPacket(
                        fakeEntity,
                        EntityPacket.setEntityCustomName(PacketUtils.createWatcher(new HashMap<Integer, Object>() {
                            {
                                put(0, (byte) (0x20)); // invisible
                                put(3, true); // customname visible
                            }
                        }), (String) args[2])
                );
                PacketUtils.sendPacket(conv.player, packet2);

                return fakeEntity;
            }
        },
        DESTROY_FAKE_ENTITY {
            @Override
            Object run(NPCConversation conv, Object... args) {
                PacketUtils.sendPacket(conv.player, EntityPacket.getDespawnPacket(conv.fakeEntity));
                return null;
            }
        };

        abstract <T> T run(NPCConversation conv, Object... args);
        private final Class<?>[] clasz;

        private MessageType(Class<?>... clasz) {
            this.clasz = clasz;
        }

        public <T> T boardcast(final NPCConversation conv, final Object... args) {
            if (clasz.length != args.length) {
                throw new IllegalArgumentException("The value of args Length is different.");
            }
            for (int i = 0; i < clasz.length; i++) {
                if (!clasz[i].isInstance(args[i])) {
                    throw new IllegalArgumentException("The argument class is different.");
                }
            }
            return run(conv, args);
        }
    }

    private interface MessageRunnable {

        public DoubleData<Object, Class<?>> run(NPCConversation conv, Object... args);
    }

    private interface ResultEmpty {
    }
}
