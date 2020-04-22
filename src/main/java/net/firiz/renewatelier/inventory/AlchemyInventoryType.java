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
package net.firiz.renewatelier.inventory;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.inventory.alchemykettle.RecipeSelect;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * @author firiz
 */
public enum AlchemyInventoryType {
    KETTLE_MAIN_MENU("KETTLE_MAIN_MENU"),
    KETTLE_SELECT_RECIPE("KETTLE_SELECT_RECIPE", new CheckRunnable() {
        @Override
        public boolean check(Action action, ItemStack item, Block block, Player player) {
            final BlockData blockData = block.getBlockData();
            if (blockData instanceof Levelled) {
                final Levelled cauldron = (Levelled) blockData;
                boolean typeCheck;
                switch (block.getRelative(BlockFace.DOWN).getType()) {
                    case FIRE:
                    case CAMPFIRE:
                        typeCheck = true;
                        break;
                    default:
                        typeCheck = false;
                        break;
                }
                return block.getType() == Material.CAULDRON
                        && !player.isSneaking()
                        && Chore.isRightOnly(action, true)
                        && cauldron.getLevel() == cauldron.getMaximumLevel()
                        && typeCheck;
            }
            return false;
        }

        @Override
        public boolean run(Action action, ItemStack item, Block block, Player player) {
            AtelierPlugin.getPlugin().getInventoryManager()
                    .getInventory(RecipeSelect.class).open(player, block.getLocation());
            return true;
        }
    }),
    KETTLE_SELECT_ITEM("KETTLE_SELECT_ITEM"),
    KETTLE_SELECT_CATALYST("KETTLE_SELECT_CATALYST");
    private final String check;
    private final CheckRunnable cr;

    AlchemyInventoryType(final String check) {
        this.check = check;
        this.cr = null;
    }

    AlchemyInventoryType(final String check, final CheckRunnable cr) {
        this.check = check;
        this.cr = cr;
    }

    public static AlchemyInventoryType search(final InventoryView invView) {
        for (final AlchemyInventoryType type : values()) {
            if (invView.getTitle().contains(type.check)) {
                return type;
            }
        }
        return null;
    }

    public static AlchemyInventoryType search(final Action action, final ItemStack item, final Block block, final Player player) {
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
        if (cr == null) {
            throw new IllegalStateException("click runnable is not found.");
        }
        return cr.run(action, item, block, player);
    }

    public interface CheckRunnable {

        boolean check(final Action action, final ItemStack item, final Block block, final Player player);

        boolean run(final Action action, final ItemStack item, final Block block, final Player player);

    }

}
