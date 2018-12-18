/*
 * ScriptItem.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.script;

import jp.gr.java_conf.zakuramomiji.renewatelier.script.execution.ScriptManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.conversation.ItemConversation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class ScriptItem {

    public static boolean start(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final ItemStack item = e.getItem();

        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            final AlchemyMaterial material = AlchemyMaterial.getMaterial(item);
            if (material != null && material.getScript() != null) {
                final String scriptName = "item/".concat(material.getScript());
                ScriptManager.INSTANCE.start(scriptName, player, new ItemConversation(
                        scriptName,
                        player
                ));
                e.setCancelled(true);
                return true;
            }
        }
        return false;
    }

}
