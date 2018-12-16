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
package jp.gr.java_conf.zakuramomiji.renewatelier.script.conversation;

import java.util.List;
import java.util.UUID;
import javax.script.Invocable;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    
    public void setIv(final Invocable iv) {
        this.iv = iv;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getPlayer(final UUID uuid) {
        return AtelierPlugin.getPlugin().getServer().getPlayer(uuid);
    }

    public UUID getUUID(final String uuid) {
        return UUID.fromString(uuid);
    }

    public String createStridColor(final String str) {
        return Chore.createStridColor(str);
    }

    public String getStridColor(final String str) {
        return Chore.getStridColor(str);
    }

    public String chatColor(final String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public void log(final String str) {
        Chore.log(str);
    }

    public List<String> getLores(final String check, final ItemStack item) {
        return AlchemyItemStatus.getLores(check, item);
    }
}
