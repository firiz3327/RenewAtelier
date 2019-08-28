package net.firiz.renewatelier.inventory

import net.firiz.renewatelier.AtelierPlugin
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.*
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import kotlin.math.floor
import kotlin.math.min

object AnvilManager {

    @JvmStatic
    fun click(e: InventoryClickEvent) {
    }

    @JvmStatic
    fun drag(e: InventoryDragEvent) {
    }

    @JvmStatic
    fun open(e: InventoryOpenEvent) {
        val anvil = e.view.topInventory as AnvilInventory
        val origin = anvil.getItem(0)
        val result = anvil.getItem(2)
        namedColor(anvil, origin, result)
    }

    @JvmStatic
    fun close(e: InventoryCloseEvent) {
        val ex = e.player.inventory.extraContents
        ex.forEach { item ->
            if (item != null && item.hasItemMeta()) {
                val meta = item.itemMeta
                meta?.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.displayName))
                item.itemMeta = meta
            }
        }
        e.player.inventory.setExtraContents(ex)
    }

    @JvmStatic
    fun prepare(e: PrepareAnvilEvent) {
        changeStats(e.inventory)
    }

    private fun changeStats(anvil: AnvilInventory) {
        Bukkit.getScheduler().runTaskLater(
                AtelierPlugin.getPlugin(),
                Runnable {
                    val origin = anvil.getItem(0)
                    val cost = anvil.getItem(1)
                    val result = anvil.getItem(2)
                    namedColor(anvil, origin, result)
                },
                1
        )
    }

    private fun namedColor(anvil: AnvilInventory, origin: ItemStack?, result: ItemStack?) {
        if (origin != null && origin.hasItemMeta()) {
            val meta = origin.itemMeta
            meta?.setDisplayName(meta.displayName.replace("§", "&"))
            origin.itemMeta = meta
        }
        if (result != null && result.hasItemMeta()) {
            val meta = result.itemMeta
            meta?.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.displayName))
            result.itemMeta = meta
        }
        anvil.viewers.forEach { player ->
            val ex = player.inventory.extraContents
            ex.forEach { item ->
                if (item != null && item.hasItemMeta()) {
                    val meta = item.itemMeta
                    meta?.setDisplayName(meta.displayName.replace("§", "&"))
                    item.itemMeta = meta
                }
            }
            player.inventory.setExtraContents(ex)
        }
    }

}