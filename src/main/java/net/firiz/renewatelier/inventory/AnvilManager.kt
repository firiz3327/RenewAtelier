package net.firiz.renewatelier.inventory

import net.firiz.renewatelier.AtelierPlugin
import net.firiz.renewatelier.item.tool.ToolDamage
import net.firiz.renewatelier.utils.Chore
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack

object AnvilManager {

    fun toolRepair(anvil: AnvilInventory) {
            Bukkit.getScheduler().runTaskLater(
                    AtelierPlugin.getPlugin(),
                    Runnable {
                        val origin = anvil.getItem(0)
                        val cost = anvil.getItem(1)
                        if (anvil.getItem(0) != null
                                && cost != null
                                && origin != null) {
                            val result = origin.clone()
                            Chore.setDamage(
                                    result,
                                    ToolDamage.damage(
                                            -ToolDamage.getDamage(cost),
                                            result,
                                            false
                                    )
                            )
//                return result
                            anvil.setItem(2, result)
                        }
                    },
                    1
            );
    }

    @JvmStatic
    fun click(e: InventoryClickEvent) {
        toolRepair(e.inventory as AnvilInventory)
    }

    @JvmStatic
    fun drag(e: InventoryDragEvent) {
        toolRepair(e.inventory as AnvilInventory)
    }

    @JvmStatic
    fun prepare(e: PrepareAnvilEvent) {
//        toolRepair(e.inventory)
    }

}