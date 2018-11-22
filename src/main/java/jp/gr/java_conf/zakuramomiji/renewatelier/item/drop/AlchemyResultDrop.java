/*
 * AlchemyResultDrop.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.item.drop;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author firiz
 */
public class AlchemyResultDrop extends AnimatedDrop {
    private final World world;

    public AlchemyResultDrop(Location loc, ItemStack item) {
        super(loc, item, 1000);
        world = loc.getWorld();
        world.playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 0.1f, 1);
    }

    @Override
    protected void run() {
        if (item != null) {
            if (!item.isDead()) {
                if (tick >= 800) {
                    item.setVelocity(new Vector().setY(0.005));
                }
            } else {
                tick = 0;
            }
        } else if (tick <= 900) {
            spawn();
            item.setGravity(false);
            item.setVelocity(new Vector());
            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            world.spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
            world.spawnParticle(Particle.CRIT, loc, 50);
            world.spawnParticle(Particle.CRIT_MAGIC, loc, 50);
            world.spawnParticle(Particle.CLOUD, loc, 10);
        } else if(tick >= 950) {
            world.spawnParticle(Particle.PORTAL, loc, 3);
            world.spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 3);
        }
    }

    @Override
    protected void end() {
        if (!item.isDead()) {
            item.setGravity(true);
        }
    }

}
