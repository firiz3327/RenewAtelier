package net.firiz.renewatelier.listener;

import java.util.UUID;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.debug.DebugManager;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.megaphone.Megaphone;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredListener;

/**
 * @author firiz
 */
public class DebugListener implements Listener {

    private final DebugManager debug = new DebugManager(this);
    private boolean nonBreak = true;
    private boolean logHandle = false;
    private final RegisteredListener alarmListener = new RegisteredListener(
            this,
            (listener, event) -> {
                if (logHandle && !(event instanceof PlayerStatisticIncrementEvent
                        || event instanceof EntityAirChangeEvent
                        || event instanceof VehicleUpdateEvent
                        || event instanceof VehicleBlockCollisionEvent
                )) {
                    Chore.log(event.getEventName());
                }
            },
            EventPriority.NORMAL,
            AtelierPlugin.getPlugin(),
            false
    );

    public boolean changeAllHandles() {
        logHandle = !logHandle;
        if(logHandle) {
            for (final HandlerList handler : HandlerList.getHandlerLists()) {
                handler.register(alarmListener);
            }
        } else {
            for (final HandlerList handler : HandlerList.getHandlerLists()) {
                handler.unregister(alarmListener);
            }
        }
        return logHandle;
    }

    @EventHandler
    private void debug(final AsyncPlayerChatEvent e) {
        if (!e.getPlayer().isOp()) {
            return;
        }
        e.setCancelled(true);
        debug.command(e.getPlayer(), e.getMessage());

        if (!e.isCancelled()) {
            e.setCancelled(true);
            final String msg = e.getMessage();
            if (msg.contains("%item%")) {
                final ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
                if (mainHand.getType() != Material.AIR) {
                    Megaphone.itemMegaPhone(
                            e.getPlayer(),
                            msg,
                            mainHand
                    );
                    return;
                }
            }
            Megaphone.megaPhone(e.getPlayer(), msg);
        }
    }

    @EventHandler
    private void debugServer(final ServerCommandEvent e) {
        final String[] strs = e.getCommand().split(" ");
        switch (strs[0].trim().toLowerCase()) {
            case "debugdrop": {
                final Player player = Bukkit.getServer().getPlayer(strs[1]);
                final int val = Integer.parseInt(strs[2]);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
                    for (int i = 0; i < val; i++) {
                        final ItemStack item = AlchemyItemStatus.getItem("kaen_stone", new ItemStack(Material.STONE));
                        Chore.drop(player.getLocation(), item);
                    }
                }, 20);
                break;
            }
            case "addrecipe": {
                final Player player = Bukkit.getServer().getPlayer(UUID.fromString(strs[1]));
                final Char status = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId());
                final AlchemyRecipe search = AlchemyRecipe.search(strs[2]);
                if (search != null) {
                    final RecipeStatus rs = status.getRecipeStatus(strs[3]);
                    if (rs == null) {
                        status.addRecipeExp(player, true, search, 0);
                    } else if (strs.length > 4) {
                        status.addRecipeExp(player, true, search, Integer.parseInt(strs[4]));
                    }
                }
                break;
            }
            default:
                // 想定されない
                break;
        }
    }

    @EventHandler
    public final void blockbreak(BlockBreakEvent e) {
        e.setCancelled(
                nonBreak || e.getPlayer().getInventory().getItemInMainHand().getType() == Material.DEBUG_STICK
        );
    }

    public boolean isNonBreak() {
        return nonBreak;
    }

    public void setNonBreak(boolean nonBreak) {
        this.nonBreak = nonBreak;
    }
}
