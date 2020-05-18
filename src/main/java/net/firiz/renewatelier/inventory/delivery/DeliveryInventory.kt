package net.firiz.renewatelier.inventory.delivery

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial
import net.firiz.renewatelier.characteristic.Characteristic
import net.firiz.renewatelier.item.json.AlchemyItemStatus
import net.firiz.renewatelier.npc.NPCManager
import net.firiz.renewatelier.utils.Chore
import net.firiz.renewatelier.version.LanguageItemUtil
import net.firiz.renewatelier.version.packet.InventoryPacket
import net.firiz.renewatelier.version.packet.InventoryPacket.InventoryPacketType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType.SlotType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*
import javax.script.ScriptException

/**
 *
 * @author firiz
 */
object DeliveryInventory {

    private const val DELI = "-Delivery"

    @JvmStatic
    fun isDeliveryInventory(view: InventoryView): Boolean {
        return view.title.endsWith(DELI)
    }

    // slots
    // 00 01 02 03 04 05 06 07 08
    // 09 10 11 12 13 14 15 16 17
    // 18 19 20 21 22 23 24 25 26
    @JvmStatic
    fun openInventory(
            player: Player,
            title: String,
            lineSize: Int,
            deliveryObjects: List<DeliveryObject>
    ) {
        if (lineSize < 2) {
            throw IllegalArgumentException("The lineSize variable must be 2 or more.")
        }

        // {id:material.id, a:amount, q:quality, c:[characteristics] or none, i:[ingredients] or none}-Delivery
        val text = buildString {
            append("{")
            for (dObj in deliveryObjects) {
                append("id:").append(dObj.material.id).append(",")
                append("a:").append(dObj.reqAmount).append(",")
                append("q:").append(dObj.reqQuality).append(",")

                var first = true
                for (c in dObj.reqCharacteristic) {
                    append(if (first) "c:[" else ",").append(c.toString())
                    first = false
                }
                append(if (dObj.reqCharacteristic.isEmpty()) "c:none," else "],")

                first = true
                for (ing in dObj.reqIngredients) {
                    append(if (first) "i:[" else ",").append(ing.toString())
                    first = false
                }
                append(if (dObj.reqIngredients.isEmpty()) "i:none&" else "]&")
            }
            deleteCharAt(length - 1)
            append("}").append(DELI)
        }

        val size = lineSize * 9
        val inv = Bukkit.createInventory(player, lineSize * 9, text)
        for (i in size - 9 until size) {
            when (i) {
                size - 4 -> inv.setItem(i, getConfirmItem(text, inv, player))
                size - 6 -> inv.setItem(i, Chore.ci(Material.RED_WOOL, 0, ChatColor.RED.toString() + "キャンセル", null))
                else -> inv.setItem(i, Chore.ci(Material.BARRIER, 0, "§r", null))
            }
        }
        player.openInventory(inv)
        InventoryPacket.update(player, title, InventoryPacketType.CHEST)
    }

    private fun getConfirmItem(title: String, inv: Inventory, player: Player): ItemStack {
        // load valuations
        val valuations = mutableMapOf<ItemStack, DeliveryValuation>()
        for (i in 0..inv.size - 10) {
            val item = inv.contents[i]
            if (item != null && item.type != Material.AIR) {
                valuations[item] = checkItem(title, item, true, AlchemyMaterial.getMaterialOrNull(item)).second
            }
        }

        // creating lore
        val lore = arrayListOf<String>()
        lore.add("")
        valuations.forEach { (item, valuation) ->
            val itemMeta = item.itemMeta!!
            val name = if (itemMeta.hasDisplayName()) {
                itemMeta.displayName
            } else {
                LanguageItemUtil.getLocalizeName(item, player)
            }
            val ve = valuation.isEmpty()
            lore.add("${if(ve) ChatColor.GRAY else ChatColor.GREEN}・$name${if(ve) "" else "${ChatColor.GRAY} - ${ChatColor.GREEN}${valuation.getAllValue()}"}")
        }
        lore.add("")

        return Chore.ci(Material.LIME_WOOL, 0, ChatColor.GREEN.toString() + "納品", lore)
    }

    @JvmStatic
    fun click(e: InventoryClickEvent) {
        val player: Player = e.whoClicked as Player
        val inv: Inventory = e.inventory
        val confirmPos = inv.size - 4
        val cancelPos = inv.size - 6

        when {
            e.rawSlot == confirmPos -> { // confirm
                e.isCancelled = true
                confirm(player, inv, e.view)
                return
            }
            e.rawSlot == cancelPos -> { // cancel
                e.isCancelled = true
                invokeFunction(player.uniqueId, "cancel", null)
                player.closeInventory() // -> invokeFunction(close)
                return
            }
            e.slotType == SlotType.CONTAINER && e.rawSlot <= inv.size - 9 -> // click inv
                if (e.cursor != null) {
                    val cursor = e.cursor!!
                    e.isCancelled = cursor.type != Material.AIR && !checkItem(e.view, cursor)
                }
            e.slotType == SlotType.CONTAINER && e.rawSlot <= inv.size -> { // click inv bar(not quick bar)
                e.isCancelled = true
                return
            }
            e.isShiftClick -> { // other slot shift click Item
                e.isCancelled = true
                if (e.currentItem != null && checkItem(e.view, e.currentItem!!)) {
                    e.currentItem = Chore.addItemNotDrop(inv, e.currentItem!!)
                }
                return
            }
        }
        if(!e.isCancelled) {
            inv.setItem(inv.size - 4, getConfirmItem(e.view.title, inv, player))
        }
    }

    private fun confirm(player: Player, inv: Inventory, view: InventoryView) {
        val valuation = DeliveryValuation()
        for (i in 0..inv.size - 10) {
            val item = inv.contents[i]
            if (item != null && item.type != Material.AIR) {
                val checkItemData = checkItem(view, item, true, AlchemyMaterial.getMaterialOrNull(item))
                if (checkItemData.first) {
                    valuation.add(checkItemData.second)
                } else {
                    valuation.reset()
                    break
                }
            }
        }
        if (valuation.qualityValue != 0) {
            invokeFunction(player.uniqueId, "confirm", valuation)
            player.closeInventory()
        } else {
            player.playSound(player.eyeLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1f)
        }
    }

    private fun checkItem(view: InventoryView, item: ItemStack): Boolean {
        return checkItem(view, item, false, AlchemyMaterial.getMaterial(item)).first
    }

    private fun checkItem(view: InventoryView, item: ItemStack, isReqAmount: Boolean, am: AlchemyMaterial?): Pair<Boolean, DeliveryValuation> {
        return checkItem(view.title, item, isReqAmount, am)
    }

    /**
     * return DoubleData[true or false, DeliveryValuation]
     */
    private fun checkItem(title: String, item: ItemStack, isReqAmount: Boolean, am: AlchemyMaterial?): Pair<Boolean, DeliveryValuation> {
        if (am == null) {
            return Pair(false, DeliveryValuation())
        }
        val itemQuality = AlchemyItemStatus.getQuality(item)
        val deliveryArray = run {
            val titleAllValues = title.substring(1 until title.length - DELI.length - 1).split(Regex("[ :,&]")).filterIndexed { index, _ -> index % 2 != 0 }
            val titleValues = arrayListOf<ArrayList<String>>()
            for (i in 0 until titleAllValues.count() step 5) {
                titleValues.add(arrayListOf(
                        titleAllValues[i], // -> material.id
                        titleAllValues[i + 1], // -> amount
                        titleAllValues[i + 2], // -> quality
                        titleAllValues[i + 3], // -> characteristic
                        titleAllValues[i + 4] // -> ingredients
                ))
            }
            titleValues
        }

        var characteristicValue = 0
        var ingredientsValue = 0
        val checkList = run {
            val list = arrayListOf<Boolean>()
            for (deliveryData in deliveryArray) {
                val reqCharacteristics = deliveryData[3].split(",").filter { it != "none" }.map { Characteristic.getCharacteristic(it) }
                val reqIngredients = deliveryData[4].split(",").filter { it != "none" }.map { AlchemyIngredients.valueOf(it) }
                list.add(run {
                    var result = 0
                    if (reqCharacteristics.isEmpty()) {
                        result++
                    } else {
                        for (c in reqCharacteristics) {
                            if (AlchemyItemStatus.getCharacteristics(item).contains(c)) {
                                result++
                                characteristicValue++
                                break
                            }
                        }
                    }
                    if (reqIngredients.isEmpty()) {
                        result++
                    } else {
                        for (ing in reqIngredients) {
                            if (AlchemyItemStatus.getIngredients(item).contains(ing)) {
                                result++
                                ingredientsValue++
                                break
                            }
                        }
                    }
                    result == 2
                })
            }
            list.contains(true)
        }

        // material_id, req_amount, req_quality, req_characteristic, rec_ingredients
        for (deliveryData in deliveryArray) {
            val material = AlchemyMaterial.getMaterial(deliveryData[0])
            val reqAmount = deliveryData[1].toInt()
            val reqQuality = deliveryData[2].toInt()

            if ((!isReqAmount || reqAmount <= item.amount)
                    && material === am
                    && reqQuality <= itemQuality
                    && checkList
            ) {
                return Pair(true, DeliveryValuation(
                        qualityValue = reqQuality - itemQuality, // 品質評価
                        amountValue = item.amount - reqAmount, // 個数評価
                        characteristicValue = characteristicValue, // 特性評価
                        ingredientsValue = ingredientsValue // 錬金成分評価
                ))
            }
        }
        return Pair(false, DeliveryValuation())
    }

    private fun invokeFunction(uuid: UUID, method: String, value: Any?) {
        val conv = NPCManager.INSTANCE.getNPCConversation(uuid) ?: return
        try {
            if (value == null) {
                conv.iv!!.invokeFunction(method)
            } else {
                conv.iv!!.invokeFunction(method, value)
            }
        } catch (ex: ScriptException) {
            Chore.logWarning(ex)
        } catch (ex: NoSuchMethodException) {
            Chore.logWarning(ex)
        }
    }

    @JvmStatic
    fun drag(e: InventoryDragEvent) {
        val inv = e.inventory
        val confirmPos = inv.size - 6
        val cancelPos = inv.size - 4
        for (raw in e.rawSlots) {
            if (raw == confirmPos || raw == cancelPos || raw <= inv.size - 9) {
                e.isCancelled = true
                break
            }
        }
    }

    @JvmStatic
    fun close(e: InventoryCloseEvent) {
        val player = e.player as Player
        val uuid = player.uniqueId
        invokeFunction(uuid, "close", null)
    }

}
