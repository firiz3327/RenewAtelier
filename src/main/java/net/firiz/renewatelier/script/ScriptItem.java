package net.firiz.renewatelier.script;

import net.firiz.renewatelier.script.execution.ScriptManager;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.script.conversation.ItemConversation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class ScriptItem {

    private ScriptItem() {
    }

    public static boolean start(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final ItemStack item = e.getItem();

        if (item != null && player.getCooldown(item.getType()) == 0 && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            final AlchemyMaterial material = AlchemyMaterial.getMaterialOrNull(item);
            if (material != null && material.getScript() != null) {
                final String scriptName = "item/".concat(material.getScript());
                ScriptManager.INSTANCE.start(scriptName, player, new ItemConversation(
                        scriptName,
                        player
                ));
                e.setCancelled(true);
                return true;
            }
        }
        return false;
    }

}
