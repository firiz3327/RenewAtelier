/*
 * NPCManager.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.script.ScriptException;
import jp.gr.java_conf.zakuramomiji.renewatelier.loop.LoopManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.packet.PacketUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.conversation.NPCConversation;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.execution.ScriptManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author firiz
 */
public enum NPCManager {
    INSTANCE;

    public static final String CHECK = "§n§b§c";
    private final Map<UUID, NPCConversation> scriptPlayers = new HashMap<>();

    public void setup() {
        LoopManager.INSTANCE.addLoopEffect(() -> {
            Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
                player.getNearbyEntities(10, 5, 10).stream().filter((entity) -> (entity instanceof LivingEntity)).forEachOrdered((entity) -> {
                    final String name = ((LivingEntity) entity).getEquipment().getBoots().getItemMeta().getDisplayName();
                    if (name != null && name.contains("§k§k§k")) {
                        final String[] datas = name.split("§k§k§k");
                        if (datas[0].equals(NPCManager.CHECK)) {
                            final Location origin = entity.getLocation();
                            final Vector target = player.getLocation().toVector();
                            origin.setDirection(target.subtract(origin.toVector()));
                            
                            final double yaw = origin.getYaw();
                            final double pitch = origin.getPitch();
                            PacketUtils.sendPacket(player, PacketUtils.getLookPacket(
                                    entity.getEntityId(),
                                    pitch,
                                    yaw,
                                    entity.isOnGround()
                            ));
                            PacketUtils.sendPacket(player, PacketUtils.getHeadRotationPacket(
                                    entity.getEntityId(),
                                    yaw
                            ));
                        }
                    }
                });
            });
        });
    }

    public boolean start(final Player player, final LivingEntity entity, final boolean shift) {
        final String name = entity.getEquipment().getBoots().getItemMeta().getDisplayName();
        if (name != null && name.contains("§k§k§k")) {
            final String[] datas = name.split("§k§k§k");
            if (datas[0].equals(CHECK)) {
                final UUID uuid = player.getUniqueId();
                if (scriptPlayers.containsKey(uuid) && scriptPlayers.get(uuid).getNPC().equals(entity)) {
                    final NPCConversation npcc = scriptPlayers.get(uuid);
                    try {
                        npcc.getIv().invokeFunction("action", shift);
                    } catch (ScriptException ex) {
                        Chore.log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                    }
                } else {
                    final String script = "npc/".concat(Chore.getStridColor(datas[1]));
                    System.out.println("");
                    final NPCConversation conversation = new NPCConversation(entity, script, player);
                    ScriptManager.INSTANCE.start(script, player, conversation, "action", shift);
                    scriptPlayers.put(uuid, conversation);
                }
                return true;
            }
        }
        return false;
    }

    public void dispose(final UUID uuid) {
        scriptPlayers.remove(uuid);
    }

}
