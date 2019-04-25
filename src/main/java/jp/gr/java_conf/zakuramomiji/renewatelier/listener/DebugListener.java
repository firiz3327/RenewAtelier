/*
 * DebugListener.java
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

import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.AlchemyRecipe;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.RecipeStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.megaphone.Megaphone;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerSaveManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class DebugListener implements Listener {

    private boolean nonbreak = true;

    @EventHandler
    private void debug(final AsyncPlayerChatEvent e) {
        if (!e.getPlayer().isOp()) {
            return;
        }
        e.setCancelled(true);

        if (!e.isCancelled()) {
            e.setCancelled(true);
            final String msg = e.getMessage();
            if (msg.contains("%item%")) {
                final ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
                if (mainHand != null && mainHand.getType() != Material.AIR) {
                    Megaphone.itemMegaPhone(
                            e.getPlayer(),
                            msg,
                            mainHand
                    );
                    return;
                }
            }
            Megaphone.megaPhone(e.getPlayer(), msg);
        }
    }

    @EventHandler
    private void debugServer(final ServerCommandEvent e) {
        final String[] strs = e.getCommand().split(" ");
        switch (strs[0].trim().toLowerCase()) {
            case "debugdrop": {
                final Player player = Bukkit.getServer().getPlayer(strs[1]);
                final int val = Integer.parseInt(strs[2]);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
                    for (int i = 0; i < val; i++) {
                        final ItemStack item = AlchemyItemStatus.getItem("kaen_stone", new ItemStack(Material.STONE));
                        Chore.drop(player.getLocation(), item);
                    }
                }, 20);
                break;
            }
            case "addrecipe": {
                final Player player = Bukkit.getServer().getPlayer(UUID.fromString(strs[1]));
                final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(player.getUniqueId());
                final AlchemyRecipe search = AlchemyRecipe.search(strs[2]);
                if (search != null) {
                    final RecipeStatus rs = status.getRecipeStatus(strs[3]);
                    if (rs == null) {
                        status.addRecipeExp(player, true, search, 0);
                    } else if (strs.length > 4) {
                        status.addRecipeExp(player, true, search, Integer.parseInt(strs[4]));
                    }
                }
                break;
            }
        }
    }

    @EventHandler
    public final void blockbreak(BlockBreakEvent e) {
        e.setCancelled(nonbreak);
    }

    private String[] split(final String str) {
        if (str.contains(",")) {
            return str.split(",");
        }
        return new String[]{str};
    }

}
