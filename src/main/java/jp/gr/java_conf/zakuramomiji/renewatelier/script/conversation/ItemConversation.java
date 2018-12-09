/*
 * ItemConversation.java
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

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import java.util.UUID;
import java.util.logging.Level;
import javax.script.Invocable;
import javax.script.ScriptException;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.ConfirmInventory;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.world.MyRoomManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public final class ItemConversation extends ScriptConversation {

    public ItemConversation(String scriptName, Player player, Object iv) {
        super(scriptName, player, iv);
    }

    public void warpRoom(final Player player) {
        MyRoomManager.INSTANCE.warpRoom(player);
    }

    public void warpRoom(final Player player, final UUID uuid) {
        MyRoomManager.INSTANCE.warpRoom(player, uuid);
    }

    public boolean hasRoom(final Player player) {
        return MyRoomManager.INSTANCE.hasRoom(player.getUniqueId());
    }

    public void createRoom(final Player player) {
        MyRoomManager.INSTANCE.createRoom(player.getUniqueId());
    }

    public void openConfirmInventory(final String title, final String yes, final String no, final String confirmFunctionName, final String cancelFunctionName) {
        openConfirmInventory(title, yes, no, scriptName, confirmFunctionName, cancelFunctionName);
    }

    public void openConfirmInventory(final String title, final String yes, final String no, final String script, final String confirmFunctionName, final String cancelFunctionName) {
        ConfirmInventory.openInventory(
                player,
                ChatColor.translateAlternateColorCodes('&', title),
                yes,
                no,
                (final Player player1, final boolean confirm) -> {
                    final String functionName = confirm ? confirmFunctionName : cancelFunctionName;
                    if (functionName != null) {
                        if (iv instanceof Invocable) {
                            try {
                                ((Invocable) iv).invokeFunction(functionName);
                            } catch (ScriptException ex) {
                                Chore.log(Level.SEVERE, null, ex);
                            } catch (NoSuchMethodException ex) {
                            }
                        } else if (iv instanceof GraalJSScriptEngine) {
                            try {
                                ((GraalJSScriptEngine) iv).invokeFunction(functionName);
                            } catch (ScriptException ex) {
                                Chore.log(Level.SEVERE, null, ex);
                            } catch (NoSuchMethodException ex) {
                            }
                        }
                    }
                }
        );
    }

}
