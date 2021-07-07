package net.firiz.renewatelier.server.listener;

import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
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
                case CAULDRON, OAK_DOOR, IRON_DOOR, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR, ACACIA_DOOR, DARK_OAK_DOOR:
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

    @EventHandler
    private void placeBlock(final BlockPlaceEvent e) {
        final Block block = e.getBlock();
        final Material type = block.getType();
        if (type == Material.FIRE || type == Material.CAMPFIRE || type == Material.SOUL_FIRE || type == Material.SOUL_CAMPFIRE) {
            final Block upBlock = block.getRelative(BlockFace.UP);
            if (upBlock.getBlockData() instanceof final Levelled cauldron) {
                if (cauldron.getLevel() == cauldron.getMaximumLevel()) {
                    PlayerSaveManager.INSTANCE.getChar(e.getPlayer()).completionAlchemyKettleAdvancement(upBlock.getLocation());
                }
            }
        }
    }

}
