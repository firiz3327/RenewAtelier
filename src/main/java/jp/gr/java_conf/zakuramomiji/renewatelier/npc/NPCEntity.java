/*
 * NPCEntity.java
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

import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author firiz
 */
public class NPCEntity {

    public static void createNPC(final World world, final Location location, final EntityType type, final String name, final String script) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
            final LivingEntity entity = (LivingEntity) world.spawnEntity(location, type);
            entity.setCustomName(name);
            entity.setCustomNameVisible(true);
            entity.setRemoveWhenFarAway(false);
            entity.setAI(false);

            final ItemStack item = new ItemStack(Material.STONE_BUTTON);
            final ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(NPCManager.CHECK.concat("§k§k§k").concat(Chore.createStridColor(script)));
            item.setItemMeta(meta);
            entity.getEquipment().setBoots(item);
            entity.getEquipment().setBootsDropChance(0);
        });
    }

}
