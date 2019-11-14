/*
 * BlockListener.java
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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.List;

/**
 * @author firiz
 */
public class BlockListener implements Listener {

    @EventHandler
    private void pistonExtend(final BlockPistonExtendEvent e) {
        cancelPiston(e, e.getBlocks());
    }

    @EventHandler
    private void pistonRetract(final BlockPistonRetractEvent e) {
        cancelPiston(e, e.getBlocks());
    }

    private void cancelPiston(BlockPistonEvent e, List<Block> blocks) {
        for (final Block block : blocks) {
            switch (block.getType()) {
                case CAULDRON:
                case OAK_DOOR:
                case IRON_DOOR:
                case SPRUCE_DOOR:
                case BIRCH_DOOR:
                case JUNGLE_DOOR:
                case ACACIA_DOOR:
                case DARK_OAK_DOOR:
                    e.setCancelled(true);
                    break;
                default:
                    // 想定されない
                    break;
            }
        }
    }

    @EventHandler
    public void onVineSpread(BlockSpreadEvent e) {
        if (e.getSource().getType() == Material.VINE) {
            e.setCancelled(true);
        }
    }

}
