/*
 * AnimatedDrop.java
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
package net.firiz.renewatelier.item.drop;

import net.firiz.renewatelier.loop.LoopManager;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public abstract class AnimatedDrop {

    private final LoopManager loop;
    protected final Location loc;
    private final ItemStack itemstack;
    protected Item item = null;
    protected int tick;
    private boolean isGet;

    public AnimatedDrop(Location loc, ItemStack itemstack, int tick) {
        this.loop = LoopManager.INSTANCE;
        loc.setX(loc.getX() + 0.5);
        loc.setZ(loc.getZ() + 0.5);
        this.loc = loc;
        this.itemstack = itemstack;
        this.tick = tick;
        this.isGet = true;
    }

    public final void start() {
        addLoop();
    }

    protected final void spawn() {
        item = loc.getWorld().dropItem(loc, itemstack);
    }

    protected final void setGet(boolean isGet) {
        this.isGet = isGet;
    }

    public final boolean isGet() {
        return isGet;
    }

    public final void anim() {
        if (tick > 0) {
            run();
            tick -= 1;
        } else {
            loop.removeAnimatedDrop(this);
            end();
        }
    }

    public final Item getDrop() {
        return item;
    }

    private void addLoop() {
        loop.addAnimatedDrop(this);
    }

    protected abstract void run();

    protected abstract void end();

}
