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
import jp.gr.java_conf.zakuramomiji.renewatelier.npc.NPCManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.book.QuestBook;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.ScriptItem;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                if (item != null && item.getType() == Material.WRITTEN_BOOK) {
                    final ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.isUnbreakable()) {
                        e.setCancelled(true);
                        QuestBook.openQuestBook(player, item, e.getHand());
                        return;
                    }
                }

                if (block != null) {
                    final AlchemyInventoryType type = AlchemyInventoryType.search(action, item, block, player);
                    if (type != null) {
                        e.setCancelled(type.run(action, item, block, player));
                        RecipeSelect.openGUI(player, block.getLocation());
                        return;
                    }
                }
                // use_item -------
                if (ScriptItem.start(e)) {
                    return;
                }
                //---
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

//    @EventHandler
//    private void interactEntity(final PlayerInteractEntityEvent e) {
//        final Player player = e.getPlayer();
//        final Entity rightClicked = e.getRightClicked();
//        if (rightClicked instanceof LivingEntity && e.getHand() == EquipmentSlot.HAND) {
//            final LivingEntity entity = (LivingEntity) rightClicked;
//            e.setCancelled(NPCManager.INSTANCE.start(player, entity, player.isSneaking()));
//        }
//    }
    @EventHandler
    private void interactEntity(final PlayerInteractAtEntityEvent e) {
        final Player player = e.getPlayer();
        final Entity rightClicked = e.getRightClicked();
        if (e.getHand() == EquipmentSlot.HAND) {
            final LivingEntity entity = (LivingEntity) rightClicked;
            if (player.isOp() && player.getInventory().getItemInMainHand().getType() == Material.WOODEN_AXE) {
                entity.remove();
            } else {
                e.setCancelled(NPCManager.INSTANCE.start(player, entity, player.isSneaking()));
            }
        }
    }

    @EventHandler
    private void join(final PlayerJoinEvent e) {
        NPCManager.INSTANCE.packet(e.getPlayer());
    }

    @EventHandler
    private void changeWorld(final PlayerChangedWorldEvent e) {
        NPCManager.INSTANCE.packet(e.getPlayer());
    }
    
    @EventHandler
    private void respawn(final PlayerRespawnEvent e) {
        NPCManager.INSTANCE.packet(e.getPlayer());
    }

    @EventHandler
    private void fishing(final PlayerFishEvent e) {
        switch (e.getState()) {
            case BITE:
                break;
            case CAUGHT_ENTITY:
                break;
            case CAUGHT_FISH:
                break;
            case FAILED_ATTEMPT:
                break;
            case FISHING:
                break;
            case IN_GROUND:
                break;
        }
    }

}
