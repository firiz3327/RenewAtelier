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
