/*
 * PlayerListener.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.listener;

import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.AlchemyInventoryType;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle.AlchemyKettle;
import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle.RecipeSelect;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.bag.AlchemyBagItem;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.ScriptItem;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class PlayerListener implements Listener {

    @EventHandler
    private void interact(final PlayerInteractEvent e) {
        final Action action = e.getAction();
        final ItemStack item = e.getItem();
        final Block block = e.getClickedBlock();
        final Player player = e.getPlayer();

        if (action != null && player != null) {
            if (Chore.isRight(action)) {
                if (block != null) {
                    final AlchemyInventoryType type = AlchemyInventoryType.search(action, item, block, player);
                    if (type != null) {
                        e.setCancelled(type.run(action, item, block, player));
                        RecipeSelect.openGUI(player, block.getLocation());
                        return;
                    }
                }
                // use_item -------
                if (item != null && player.getCooldown(item.getType()) == 0) {
                    if (ScriptItem.start(e)) {
                        return;
                    }
                    //---
                }
            }
        }
    }

    @EventHandler
    private void pickup(final EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            AlchemyKettle.pickup(e);
            AlchemyBagItem.pickup(e);
        }
    }

}
