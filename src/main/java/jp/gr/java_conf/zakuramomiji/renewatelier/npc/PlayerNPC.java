/*
 * NPC.java
 * 
 * Copyright (c) 2019 firiz.
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

import net.minecraft.server.v1_13_R2.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author firiz
 */
public class PlayerNPC {

    private final EntityPlayer entity;

    public PlayerNPC(EntityPlayer entity) {
        this.entity = entity;
    }
    
    public EntityPlayer getEntity() {
        return entity;
    }
    
    public String getName() {
        return entity.getName();
    }
    
    public Location getLocation() {
        return entity.getBukkitEntity().getLocation();
    }
    
    public World getWorld() {
        return entity.getBukkitEntity().getWorld();
    }

}
