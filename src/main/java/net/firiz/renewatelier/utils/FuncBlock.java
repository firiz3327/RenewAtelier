/*
 * FuncBlock.java
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

package net.firiz.renewatelier.utils;

/**
 *
 * @author firiz
 */
public enum FuncBlock {
    CHEST,
    TRAPPED_CHEST,
    WORKBENCH,
    BED_BLOCK,
    FURNACE,
    WOODEN_DOOR,
    IRON_DOOR_BLOCK,
    SPRUCE_DOOR,
    BIRCH_DOOR,
    JUNGLE_DOOR,
    ACACIA_DOOR,
    DARK_OAK_DOOR,
    LEVER,
    WOOD_BUTTON,
    STONE_BUTTON,
    TRAP_DOOR,
    IRON_TRAPDOOR,
    FENCE_GATE,
    SPRUCE_FENCE_GATE,
    BIRCH_FENCE_GATE,
    JUNGLE_FENCE_GATE,
    ACACIA_FENCE_GATE,
    DARK_OAK_FENCE_GATE,
    DROPPER,
    DISPENSER,
    HOPPER,
    NOTE_BLOCK,
    DIODE, //REDSTONE REPEATER
    REDSTONE_COMPARATOR,
    DAYLIGHT_DETECTOR,
    BEACON,
    JUKEBOX,
    ENCHANTMENT_TABLE,
    ANVIL,
    ENDER_PORTAL_FRAME,
    ENDER_CHEST,
    WHITE_SHULKER_BOX,
    ORANGE_SHULKER_BOX,
    MAGENTA_SHULKER_BOX,
    LIGHT_BLUE_SHULKER_BOX,
    YELLOW_SHULKER_BOX,
    LIME_SHULKER_BOX,
    PINK_SHULKER_BOX,
    GRAY_SHULKER_BOX,
    SILVER_SHULKER_BOX,
    CYAN_SHULKER_BOX,
    PURPLE_SHULKER_BOX,
    BLUE_SHULKER_BOX,
    BROWN_SHULKER_BOX,
    GREEN_SHULKER_BOX,
    RED_SHULKER_BOX,
    BLACK_SHULKER_BOX,
    CAULDRON;

    public static boolean searth(String str) {
        FuncBlock[] values = FuncBlock.values();
        for (FuncBlock value : values) {
            if (str.equals(value.toString())) {
                return true;
            }
        }
        return false;
    }
}
