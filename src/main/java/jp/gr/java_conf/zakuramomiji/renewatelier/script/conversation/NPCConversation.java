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

import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.delivery.DeliveryInventory;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.delivery.DeliveryObject;
import jp.gr.java_conf.zakuramomiji.renewatelier.npc.NPCManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.nms.VEntityPlayer;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.EntityPacket;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.FakeEntity;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.Invocable;
import java.util.*;

/**
 * @author firiz
 */
public final class NPCConversation extends ScriptConversation {

    private final LivingEntity npc;
    private final VEntityPlayer npcPlayer; // EntityPlayer class
    private FakeEntity fakeEntity;
    private BukkitTask runTaskLater;

    public NPCConversation(@NotNull LivingEntity npc, @NotNull String scriptName, @NotNull Player player) {
        super(scriptName, player);
        this.npc = npc;
        this.npcPlayer = null;
    }

    public NPCConversation(@NotNull VEntityPlayer npcPlayer, @NotNull String scriptName, @NotNull Player player) {
        super(scriptName, player);
        this.npc = null;
        this.npcPlayer = npcPlayer;
    }

    @NotNull
    public Invocable getIv() {
        return iv;
    }

    @Nullable
    public LivingEntity getNPC() {
        return npc;
    }

    @Nullable
    public VEntityPlayer getPlayerNPC() {
        return npcPlayer;
    }

    @NotNull
    public String getNPCName() {
        return npc == null ? npcPlayer.getName() : npc.getCustomName();
    }

    @NotNull
    public Location getLocation() {
        return (npc == null ? npcPlayer.getLocation() : npc.getLocation()).clone();
    }

    @NotNull
    public List<DeliveryObject> listDeliveryOf() {
        return new ArrayList<>();
    }

    @NotNull
    public DeliveryObject createDeliveryObject(@NotNull AlchemyMaterial material, int reqAmount) {
        return createDeliveryObject(material, reqAmount, 0, new ArrayList<>(), new ArrayList<>());
    }

    @NotNull
    public DeliveryObject createDeliveryObject(@NotNull AlchemyMaterial material, int reqAmount, int reqQuality) {
        return createDeliveryObject(material, reqAmount, reqQuality, new ArrayList<>(), new ArrayList<>());
    }

    @NotNull
    public DeliveryObject createDeliveryObject(@NotNull AlchemyMaterial material, int reqAmount, int reqQuality, List<Characteristic> reqCharacteristic, List<AlchemyIngredients> reqIngredients) {
        return new DeliveryObject(material, reqAmount, reqQuality, reqCharacteristic, reqIngredients);
    }

    public void openDeliveryInventory(
            @NotNull final String title,
            final int line_size,
            @NotNull final List<DeliveryObject> deliveryObjects
    ) {
        DeliveryInventory.INSTANCE.openInventory(player, title, line_size, deliveryObjects);
    }

    public void removeNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final EntityType type) {
        removeNPC(name, script, new Location(player.getWorld(), x, y, z), type);
    }

    public void removeNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final EntityType type) {
        NPCManager.INSTANCE.removeNPC(location, type, name, script);
    }

    public void createNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final EntityType type) {
        createNPC(name, script, new Location(player.getWorld(), x, y, z), type);
    }

    public void createNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final EntityType type) {
        NPCManager.INSTANCE.createNPC(location, type, name, script);
    }

    public void removePlayerNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final UUID uuid) {
        removePlayerNPC(name, script, new Location(player.getWorld(), x, y, z), uuid);
    }

    public void removePlayerNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final UUID uuid) {
        NPCManager.INSTANCE.removeNPCPlayer(location, name, uuid, script);
    }

    public void createPlayerNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final UUID uuid) {
        createPlayerNPC(name, script, new Location(player.getWorld(), x, y, z), uuid);
    }

    public void createPlayerNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final UUID uuid) {
        NPCManager.INSTANCE.createNPCPlayer(location, name, uuid, script);
    }

    public void dispose() {
        NPCManager.INSTANCE.dispose(player.getUniqueId());
    }

    public void sendNext(@NotNull final String text) {
        sendNext("", text);
    }

    public void sendNext(@NotNull final String prefix, @NotNull final String text) {
        sendNext(prefix, "", text);
    }

    public void sendNext(@NotNull final String prefix, @NotNull final String suffix, @NotNull final String text) {
        sendNext(prefix, suffix, text, 60); // 3 sec
    }

    public void sendNext(@NotNull final String prefix, @NotNull final String suffix, @NotNull final String text, final int time) {
        player.sendMessage(prefix + text + suffix);
        messagePacket(chatColor(text), time);
    }

    private void messagePacket(@NotNull final String text, final int time) {
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
                () -> MessageType.DESTROY_FAKE_ENTITY.boardcast(this),
                time
        );
    }

    private enum MessageType {
        FAKE_ENTITY(EntityType.class, Location.class, String.class) {
            @Override
            FakeEntity run(NPCConversation conv, Object... args) {
                // https://wiki.vg/Protocol#Spawn_Mob
                // https://wiki.vg/Entity_metadata#Entity_Metadata_Format
                final FakeEntity fakeEntity = new FakeEntity(-1, (EntityType) args[0], 0);
                conv.fakeEntity = fakeEntity;
                PacketUtils.sendPacket(conv.player, EntityPacket.getSpawnPacket(
                        fakeEntity,
                        (Location) args[1]
                ));
                PacketUtils.sendPacket(conv.player, EntityPacket.getMetadataPacket(
                        fakeEntity,
                        EntityPacket.setEntityCustomName(
                                PacketUtils.createWatcher(new HashMap<Integer, Object>() {
                                    {
                                        put(0, (byte) (0x20)); // invisible
                                        put(3, true); // customname visible
                                    }
                                }),
                                (String) args[2]
                        )
                ));
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

        MessageType(Class<?>... clasz) {
            this.clasz = clasz;
        }

        public <T> T boardcast(@NotNull final NPCConversation conv, @NotNull final Object... args) {
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

}
