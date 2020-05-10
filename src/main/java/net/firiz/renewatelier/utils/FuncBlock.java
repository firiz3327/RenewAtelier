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
