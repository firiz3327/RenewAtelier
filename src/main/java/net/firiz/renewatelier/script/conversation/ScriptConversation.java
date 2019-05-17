/*
 * ScriptConversation.java
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
package net.firiz.renewatelier.script.conversation;

import java.util.List;
import java.util.UUID;
import javax.script.Invocable;
import javax.script.ScriptException;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.inventory.ConfirmInventory;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.player.PlayerSaveManager;
import net.firiz.renewatelier.player.PlayerStatus;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.world.MyRoomManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.graalvm.polyglot.HostAccess.Export;

/**
 *
 * @author firiz
 */
public class ScriptConversation {

    protected final String scriptName;
    protected final Player player;
    protected Invocable iv;

    public ScriptConversation(String scriptName, Player player) {
        this.scriptName = scriptName;
        this.player = player;
    }

    @Export
    public void log(final Object str) {
        Chore.log(str);
    }

    @Export
    public void debug(final Object str) {
        Chore.log(str);
        player.sendMessage(str.toString());
    }

    @Export
    public void setIv(final Invocable iv) {
        this.iv = iv;
    }

    @Export
    public Player getPlayer() {
        return player;
    }

    @Export
    public Player getPlayer(final UUID uuid) {
        return AtelierPlugin.getPlugin().getServer().getPlayer(uuid);
    }

    @Export
    public PlayerStatus getStatus() {
        return getStatus(player.getUniqueId());
    }

    @Export
    public PlayerStatus getStatus(final UUID uuid) {
        return PlayerSaveManager.INSTANCE.getStatus(uuid);
    }

    @Export
    public QuestStatus getQuestStatus(final String questId) {
        return getQuestStatus(player.getUniqueId(), questId);
    }

    @Export
    public QuestStatus getQuestStatus(final UUID uuid, final String questId) {
        return getStatus(uuid).getQuestStatus(questId);
    }

    @Export
    public boolean hasQuest(final String questId) {
        return hasQuestUUID(player.getUniqueId(), questId);
    }

    @Export
    public boolean hasQuestUUID(final UUID uuid, final String questId) {
        return getQuestStatus(uuid, questId) != null;
    }

    @Export
    public boolean isQuestClear(final String questId) {
        return isQuestClear(player.getUniqueId(), questId);
    }

    @Export
    public boolean isQuestClear(final UUID uuid, final String questId) {
        return getQuestStatus(uuid, questId).isClear();
    }

    @Export
    public void questStart(final String questId) {
        questStart(questId, true);
    }

    @Export
    public void questStart(final String questId, final boolean view) {
        if (view) {
            getStatus().addQuest(player, questId);
        } else {
            getStatus().addQuest(new QuestStatus(questId));
        }
    }

    @Export
    public void questClear(final String questId) {
        questClear(questId, true);
    }

    @Export
    public void questClear(final String questId, final boolean view) {
        getStatus().questClear(player, questId, view);
    }

    @Export
    public String getQuestName(final String questId) {
        return Quest.getQuest(questId).getName();
    }

    @Export
    public UUID getUUID(final String uuid) {
        return UUID.fromString(uuid);
    }

    @Export
    public String createStridColor(final String str) {
        return Chore.createStridColor(str);
    }

    @Export
    public String getStridColor(final String str) {
        return Chore.getStridColor(str);
    }

    @Export
    public String chatColor(final String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Export
    public List<String> getLores(final String check, final ItemStack item) {
        return AlchemyItemStatus.getLores(check, item);
    }

    @Export
    public void warpRoom(final Player player) {
        MyRoomManager.INSTANCE.warpRoom(player);
    }

    @Export
    public void warpRoom(final Player player, final UUID uuid) {
        MyRoomManager.INSTANCE.warpRoom(player, uuid);
    }

    @Export
    public boolean hasRoom(final Player player) {
        return MyRoomManager.INSTANCE.hasRoom(player.getUniqueId());
    }

    @Export
    public void createRoom(final Player player) {
        MyRoomManager.INSTANCE.createRoom(player.getUniqueId());
    }

    @Export
    public void openConfirmInventory(final String title, final String yes, final String no, final String confirmFunctionName, final String cancelFunctionName) {
        openConfirmInventory(title, yes, no, confirmFunctionName, cancelFunctionName, null);
    }

    @Export
    public void openConfirmInventory(final String title, final String yes, final String no, final String confirmFunctionName, final String cancelFunctionName, final String closeFunctionName) {
        ConfirmInventory.openInventory(
                player,
                ChatColor.translateAlternateColorCodes('&', title),
                yes,
                no,
                (final Player player1, final int select) -> {
                    final String functionName = select == 1 ? confirmFunctionName : select == 0 ? cancelFunctionName : closeFunctionName;
                    if (functionName != null) {
                        try {
                            iv.invokeFunction(functionName);
                        } catch (ScriptException ex) {
                            Chore.logWarning(ex);
                        } catch (NoSuchMethodException ignored) {
                        }
                    }
                }
        );
    }

    @Export
    public AlchemyMaterial getMaterial(final String material_id) {
        return AlchemyMaterial.getMaterial(material_id);
    }

    @Export
    public AlchemyMaterial getMaterial(final ItemStack item) {
        return AlchemyMaterial.getMaterial(item);
    }

    @Export
    public ItemStack createItemStack(final Material material, int amount) {
        return new ItemStack(material, amount);
    }

    @Export
    public Material getItemMaterial(final String material_id) {
        return Material.valueOf(material_id.toUpperCase());
    }

    @Export
    public EntityType getEntityType(final String entityType) {
        return EntityType.valueOf(entityType.toUpperCase());
    }
    
}
