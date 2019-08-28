package net.firiz.renewatelier.debug

import net.firiz.renewatelier.AtelierPlugin
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus
import net.firiz.renewatelier.config.ConfigManager
import net.firiz.renewatelier.debug.annotations.Cmd
import net.firiz.renewatelier.item.AlchemyItemStatus
import net.firiz.renewatelier.listener.DebugListener
import net.firiz.renewatelier.nodification.Nodification
import net.firiz.renewatelier.npc.NPCManager
import net.firiz.renewatelier.player.PlayerSaveManager
import net.firiz.renewatelier.utils.Chore
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import java.util.*

class DebugCommands(private val debugListener: DebugListener) {

    @Cmd(
            desc = ["Name?"],
            args = [String::class],
            examples = ["help", "help 2", "help getUUID"],
            text = "ヘルプを表示します。",
            hideReturn = true
    )
    fun help(sender: Player, args: ArrayList<Any>) {
        var cmd: String? = null
        var page = 0
        if (args.size != 0) {
            val temp = args[0].toString().toIntOrNull()
            if (temp == null) cmd = args[0].toString() else page = if (temp <= 0) 0 else (temp - 1) * 9
        }
        var next = 0
        var notFound = true
        var message = arrayListOf<String>()
        for (it in DebugCommands::class.java.methods) {
            if (it.isAnnotationPresent(Cmd::class.java)) {
                if (cmd == null) {
                    if (next >= page) {
                        if (next > page + 8) {
                            break
                        }
                        if (next == page) {
                            sender.sendMessage("")
                            message.add("${ChatColor.DARK_AQUA}help ${page / 9 + 1} page infos:")
                        }
                        message.add("${ChatColor.DARK_GRAY}command ${ChatColor.GREEN}'${it.name}' ${ChatColor.RESET}- ${it.getAnnotation(Cmd::class.java).text}")
                        notFound = false
                    }
                    next++
                } else {
                    if (it.name.equals(cmd, true)) {
                        val anno = it.getAnnotation(Cmd::class.java)
                        notFound = false
                        val datas = arrayListOf(
                                "",
                                "${ChatColor.DARK_AQUA}$cmd Info:",
                                "${ChatColor.DARK_AQUA}Description:",
                                "- ${anno.text}",
                                "${ChatColor.DARK_AQUA}Args:",
                                "- [${anno.desc.joinToString(separator = ", ")}]"
                        )
                        message.addAll(datas.toTypedArray())
                        if (anno.examples.isNotEmpty()) {
                            message.add("${ChatColor.DARK_AQUA}Examples:")
                            anno.examples.forEach {
                                message.add("- $it")
                            }
                        }
                        break
                    }
                }
            }
        }
        if (notFound) {
            sender.sendMessage("")
            message.add("${ChatColor.DARK_AQUA}help: not found command${if (cmd == null) "s" else ""} for ${cmd
                    ?: "${(page / 9) + 1} page"}.")
            if (cmd != null) {
                val ccmds = arrayListOf<String>()
                for (it in DebugCommands::class.java.methods) {
                    if (it.isAnnotationPresent(Cmd::class.java)) {
                        if (it.name.contains(cmd, true)) {
                            ccmds.add("${ChatColor.DARK_GRAY}command ${ChatColor.GREEN}'${it.name}' ${ChatColor.RESET}- ${it.getAnnotation(Cmd::class.java).text}")
                        }
                    }
                }
                if (ccmds.isNotEmpty()) {
                    message.add("${ChatColor.DARK_AQUA}did you mean:")
                    message.addAll(ccmds.toTypedArray())
                }
            }
        }
        sender.sendMessage(message.toTypedArray())
    }

    @Cmd(
            desc = ["Player?"],
            args = [Player::class],
            examples = ["getLocation", "getLocation {getPlayer {getUUID d9c1fa1c-6662-429a-b0cf-ac8b0df4dc26}}"],
            text = "Locationクラスを返します。"
    )
    fun getLocation(sender: Player, args: ArrayList<Any>): Location {
        return if (args.size == 0) sender.location else {
            (args[0] as Player).location
        }
    }

    @Cmd(
            desc = ["UUID?"],
            args = [UUID::class],
            examples = ["getPlayer", "getPlayer {getUUID d9c1fa1c-6662-429a-b0cf-ac8b0df4dc26}"],
            text = "プレイヤークラスを返します。"
    )
    fun getPlayer(sender: Player, args: ArrayList<Any>): Player {
        return if (args.size == 0) sender else {
            sender.server.getPlayer(args[0] as UUID)!!
        }
    }

    @Cmd(
            desc = ["Name?"],
            args = [String::class],
            examples = ["getUUID", "getUUID d9c1fa1c-6662-429a-b0cf-ac8b0df4dc26"],
            text = "UUIDクラスを返します。"
    )
    fun getUUID(sender: Player, args: ArrayList<Any>): UUID {
        return if (args.size == 0) sender.uniqueId else UUID.fromString(args[0].toString())
    }

    @Cmd(
            desc = ["Id"],
            args = [String::class],
            examples = ["getMaterial STONE", "getMaterial minecraft:stone"],
            text = "SpigotのMaterialクラスを返します。"
    )
    fun getMaterial(sender: Player, args: ArrayList<Any>): Material {
        return Chore.getMaterial(args[0] as String)
    }

    @Cmd(
            desc = ["Id"],
            args = [String::class],
            examples = ["getEntityType CREEPER", "getentitytype skeleton"],
            text = "SpigotのEntityTypeクラスを返します。"
    )
    fun getEntityType(sender: Player, args: ArrayList<Any>): EntityType {
        return EntityType.valueOf((args[0] as String).toUpperCase())
    }

    @Cmd(
            desc = ["Location", "EntityType", "Name", "Script"],
            args = [Location::class, EntityType::class, String::class, String::class],
            examples = ["npc {getLocation} {getEntityType skeleton} example test"],
            text = "NPCをスポーンさせます"
    )
    fun npc(sender: Player, args: ArrayList<Any>) {
        npc(
                args[0] as Location,
                args[1] as EntityType,
                args[2] as String,
                args[3] as String,
                false
        )
    }

    @Cmd(
            desc = ["Location", "EntityType", "Name", "Script"],
            args = [Location::class, EntityType::class, String::class, String::class],
            examples = ["npcSave {getLocation} {getEntityType skeleton} example test"],
            text = "NPCをスポーンさせ、保存します。"
    )
    fun npcSave(sender: Player, args: ArrayList<Any>) {
        npc(
                args[0] as Location,
                args[1] as EntityType,
                args[2] as String,
                args[3] as String,
                true
        )
    }

    private fun npc(loc: Location, type: EntityType, name: String, script: String, save: Boolean) {
        NPCManager.INSTANCE.createNPC(loc, type, name, script, save);
    }

    @Cmd(
            desc = ["Location", "Name", "UUID", "Script"],
            args = [Location::class, String::class, UUID::class, String::class],
            examples = ["playerNpc {getLocation} onamae {getUUID} test"],
            text = "プレイヤーNPCをスポーンさせます。"
    )
    fun playerNpc(sender: Player, args: ArrayList<Any>) {
        playerNpc(
                args[0] as Location,
                args[1] as String,
                args[2] as UUID,
                args[3] as String,
                false
        )
        Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), Runnable {
            sender.kickPlayer("プレイヤーNPCを設置しました。再ログインしてください。")
        })
    }

    @Cmd(
            desc = ["Location", "Name", "UUID", "Script"],
            args = [Location::class, String::class, UUID::class, String::class],
            examples = ["playerNpcSave {getLocation} onamae {getUUID} test"],
            text = "プレイヤーNPCをスポーンさせ、保存します。"
    )
    fun playerNpcSave(sender: Player, args: ArrayList<Any>) {
        playerNpc(
                args[0] as Location,
                args[1] as String,
                args[2] as UUID,
                args[3] as String,
                true
        )
        Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), Runnable {
            sender.kickPlayer("プレイヤーNPCを設置しました。再ログインしてください。")
        })
    }

    private fun playerNpc(loc: Location, name: String, uuid: UUID, script: String, save: Boolean) {
        NPCManager.INSTANCE.createNPCPlayer(loc, name, uuid, script, save)
    }

    @Cmd(
            desc = ["Material"],
            args = [Material::class],
            examples = ["packetRecipe {getMaterial cauldron}"],
            text = "自身にレシピ開放パケットを送ります。"
    )
    fun packetRecipe(sender: Player, args: ArrayList<Any>) {
        Nodification.recipeNodification(sender, args[0] as Material)
    }

    @Cmd(
            desc = ["Id"],
            args = [String::class],
            examples = ["getAMaterial atelier_book"],
            text = "錬金マテリアルを返します。"
    )
    fun getAMaterial(sender: Player, args: ArrayList<Any>): AlchemyMaterial {
        return AlchemyMaterial.getMaterial(args[0] as String)
    }

    @Cmd(
            desc = ["AlchemyMaterial or String"],
            args = [Object::class],
            examples = ["item {getAMaterial atelier_book}", "item atelier_book"],
            text = "手に持っているアイテムに錬金マテリアルを適用します。"
    )
    fun item(sender: Player, args: ArrayList<Any>) {
        if (sender.inventory.itemInMainHand != null) {
            if (args[0] is AlchemyMaterial) {
                AlchemyItemStatus.getItem(
                        args[0] as AlchemyMaterial,
                        sender.inventory.itemInMainHand
                )
            } else {
                AlchemyItemStatus.getItem(
                        args[0].toString(),
                        sender.inventory.itemInMainHand
                )
            }
        } else {
            sender.sendMessage("ERROR: Your main hand is null.")
        }
    }

    @Cmd(
            text = "ブロックの破壊をON・OFFします。"
    )
    fun Break(sender: Player, args: ArrayList<Any>): Boolean {
        debugListener.nonbreak = !debugListener.nonbreak
        return debugListener.nonbreak
    }

    @Cmd(
            desc = ["Id"],
            args = [String::class],
            examples = ["getRecipe flam"],
            text = "錬金レシピを返します。"
    )
    fun getRecipe(sender: Player, args: ArrayList<Any>): AlchemyRecipe {
        return AlchemyRecipe.search(args[0].toString())
    }

    @Cmd(
            desc = ["Player", "AlchemyRecipe", "level", "exp"],
            args = [Player::class, AlchemyRecipe::class, Int::class, Int::class],
            examples = ["recipeExp {getPlayer} {getRecipe flam} 2 10"],
            text = "プレイヤーの錬金レシピ情報を書き換えます。"
    )
    fun recipeExp(sender: Player, args: ArrayList<Any>) {
        val status = PlayerSaveManager.INSTANCE.getStatus((args[0] as Player).uniqueId)
        status.setRecipeStatus(RecipeStatus(
                (args[1] as AlchemyRecipe).id,
                (args[2] as String).toInt(),
                (args[3] as String).toInt()
        ))
    }

    @Cmd(
            desc = ["CustomModelData"],
            args = [Int::class],
            examples = ["customModel 1514"],
            text = "テクスチャアイテムの取得"
    )
    fun customModel(sender: Player, args: ArrayList<Any>) {
        val item = sender.inventory.itemInMainHand
        val value = args[0].toString().toInt()
        val meta = item.itemMeta
        Chore.setCustomModelData(meta, value)
        item.itemMeta = meta
    }

    @Cmd(
            desc = ["Config Reload"],
            examples = ["configReload"],
            text = "AtelierPluginのコンフィグをリロード"
    )
    fun configReload(sender: Player, args: ArrayList<Any>) {
        ConfigManager.INSTANCE.reloadConfigs()
    }

    @Cmd(
            desc = ["Test"],
            examples = ["test"],
            text = "・・・"
    )
    fun test(sender: Player, args: ArrayList<Any>) {

        Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), Runnable {
            val stand: ArmorStand = sender.world.spawnEntity(sender.location, EntityType.ARMOR_STAND) as ArmorStand
            stand.setArms(true)

            val item = Chore.createCustomModelItem(Material.DIAMOND_AXE, 1, 1524)
            val meta = item.itemMeta!!
            meta.isUnbreakable = true
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            item.itemMeta = meta

            stand.setItemInHand(item)
            stand.isVisible = false
        })

    }

}