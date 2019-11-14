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
package net.firiz.renewatelier.listener;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.event.PlayerArmorChangeEvent;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.inventory.alchemykettle.AlchemyKettle;
import net.firiz.renewatelier.inventory.alchemykettle.RecipeSelect;
import net.firiz.renewatelier.notification.Notification;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.quest.book.QuestBook;
import net.firiz.renewatelier.script.ScriptItem;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.inject.PlayerInjection;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author firiz
 */
public class PlayerListener implements Listener {

    @EventHandler
    private void interact(final PlayerInteractEvent e) {
        final Action action = e.getAction();
        final ItemStack item = e.getItem();
        final Block block = e.getClickedBlock();
        final Player player = e.getPlayer();

        if (Chore.isRight(action)) {
            if (item != null && item.getType() == Material.WRITTEN_BOOK) {
                final AlchemyMaterial material = AlchemyMaterial.getMaterialOrNull(item);
                if (material != null && material.getId().equalsIgnoreCase("quest_book")) {
                    e.setCancelled(true);
                    QuestBook.openQuestBook(player, e.getHand());
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
                // なんか作る
            }
            //---
        }
    }

    @EventHandler
    private void pickup(final EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            AlchemyKettle.pickup(e);
        }
    }

    @EventHandler
    private void interactEntity(final PlayerInteractEntityEvent e) {
        final Player player = e.getPlayer();
        final Entity rightClicked = e.getRightClicked();
        if (e.getHand() == EquipmentSlot.HAND && rightClicked instanceof LivingEntity) {
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
        PlayerSaveManager.INSTANCE.loadStatus(e.getPlayer());
        PlayerInjection.injectArmorChangeEvent(e.getPlayer());
        NPCManager.INSTANCE.packet(e.getPlayer());
        Notification.loginNotification(e.getPlayer());
    }

    @EventHandler
    private void held(final PlayerItemHeldEvent e) {
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).getCharStats().updateWeapon(e.getNewSlot());
    }

    @EventHandler
    private void armorChange(final PlayerArmorChangeEvent e) {
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).getCharStats().updateEquip();
    }

    @EventHandler
    private void quit(final PlayerQuitEvent e) {
        PlayerSaveManager.INSTANCE.unloadStatus(e.getPlayer().getUniqueId());
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
    private void discoverRecipe(final PlayerRecipeDiscoverEvent e) {
        final Char status = PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId());
        status.discoverRecipe(e.getRecipe().getNamespace() + ":" + e.getRecipe().getKey());
    }

}
