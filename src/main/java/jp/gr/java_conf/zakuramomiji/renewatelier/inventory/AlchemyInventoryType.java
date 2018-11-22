/*
 * AlchemyInventoryType.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.inventory;

import jp.gr.java_conf.zakuramomiji.renewatelier.inventory.alchemykettle.RecipeSelect;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;

/**
 *
 * @author firiz
 */
public enum AlchemyInventoryType {
    KETTLE_MAIN_MENU("KETTLE_MAIN_MENU"/*"§k§e§f§f§l§e"*/),
    KETTLE_SELECT_RECIPE("KETTLE_SELECT_RECIPE"/*"§f§e§c§l§b§e"*/, new CheckRunnable() {
        @Override
        public boolean check(Action action, ItemStack item, Block block, Player player) {
            return (block.getType() == Material.CAULDRON
                    && !player.isSneaking()
                    && Chore.isRightOnly(action, true)
                    && ((Cauldron) block.getState().getData()).isFull()
                    && block.getRelative(BlockFace.DOWN).getType() == Material.FIRE);
        }

        @Override
        public boolean run(Action action, ItemStack item, Block block, Player player) {
            RecipeSelect.openGUI(player, block.getLocation());
            return true;
        }
    }),
    KETTLE_SELECT_ITEM("KETTLE_SELECT_ITEM"/*"§l§f§e§3§c§e§l§l§e§c§f"*/),
    KETTLE_SELECT_CATALYST("KETTLE_SELECT_CATALYST"/*"§c§1§1§a§l§2§2§1"*/);

    private final String check;
    private final CheckRunnable cr;

    private AlchemyInventoryType(final String check) {
        this.check = check;
        this.cr = null;
    }

    private AlchemyInventoryType(final String check, final CheckRunnable cr) {
        this.check = check;
        this.cr = cr;
    }

    public final static AlchemyInventoryType search(final Inventory inv) {
        for (final AlchemyInventoryType type : values()) {
            if (inv.getName().contains(type.check)) {
                return type;
            }
        }
        return null;
    }

    public final static AlchemyInventoryType search(final Action action, final ItemStack item, final Block block, final Player player) {
        for (final AlchemyInventoryType type : values()) {
            if (type.cr != null && type.cr.check(action, item, block, player)) {
                return type;
            }
        }
        return null;
    }

    public final String getCheck() {
        return check;
    }

    public final boolean run(final Action action, final ItemStack item, final Block block, final Player player) {
        return cr.run(action, item, block, player);
    }

    public interface CheckRunnable {

        public boolean check(final Action action, final ItemStack item, final Block block, final Player player);

        public boolean run(final Action action, final ItemStack item, final Block block, final Player player);

    }

}
