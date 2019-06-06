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

    fun toolRepair(anvil: AnvilInventory): ItemStack? {
        if (anvil.getItem(0) != null
                && anvil.getItem(1) != null
                && anvil.getItem(2) != null
                && ToolDamage.hasDamagedItem(anvil.getItem(2))) {

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
                                            Math.max(0, (result.type.maxDurability - ToolDamage.getDamage(result)) - ToolDamage.getDamage(cost)),
                                            result,
                                            false
                                    )
                            )
//                            return result
                            anvil.setItem(2, result)
                        }
                    },
                    10
            )
        }
        return anvil.getItem(2)
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
    }

}