package net.firiz.renewatelier.debug

import net.firiz.renewatelier.AtelierPlugin
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus
import net.firiz.renewatelier.config.ConfigManager
import net.firiz.renewatelier.debug.annotations.Cmd
import net.firiz.renewatelier.item.AlchemyItemStatus
import net.firiz.renewatelier.listener.DebugListener
import net.firiz.renewatelier.notification.Notification
import net.firiz.renewatelier.npc.NPCManager
import net.firiz.renewatelier.entity.player.PlayerSaveManager
import net.firiz.renewatelier.utils.Chore
import net.firiz.renewatelier.version.entity.living.monster.C
import net.firiz.renewatelier.version.entity.living.horse.CaneHorse
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils
import net.firiz.renewatelier.version.entity.atelier.Puni
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

class DebugCommands(private val debugListener: DebugListener) {

    private val messagePlayerNpc = "プレイヤーNPCを設置しました。再ログインしてください。";

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
                    if (it.isAnnotationPresent(Cmd::class.java) && it.name.contains(cmd, true)) {
                        ccmds.add("${ChatColor.DARK_GRAY}command ${ChatColor.GREEN}'${it.name}' ${ChatColor.RESET}- ${it.getAnnotation(Cmd::class.java).text}")
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
            examples = ["npc {getLocation} {getEntityType skeleton} example test.js"],
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
            examples = ["npcSave {getLocation} {getEntityType skeleton} example test.js"],
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
            examples = ["playerNpc {getLocation} onamae {getUUID} test.js"],
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
            sender.kickPlayer(messagePlayerNpc)
        })
    }

    @Cmd(
            desc = ["Location", "Name", "UUID", "Script"],
            args = [Location::class, String::class, UUID::class, String::class],
            examples = ["playerNpcSave {getLocation} name {getUUID} test.js"],
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
            sender.kickPlayer(messagePlayerNpc)
        })
    }

    private fun playerNpc(loc: Location, name: String, uuid: UUID, script: String, save: Boolean) {
        NPCManager.INSTANCE.createNPCPlayer(loc, name, uuid, script, save)
    }

    @Cmd(
            desc = ["Name"],
            args = [String::class],
            examples = ["testNpc name", "testNpc name player"],
            text = "プレイヤーNPCをスポーンさせます。"
    )
    fun testNpc(sender: Player, args: ArrayList<Any>) {
        if (args.size >= 2
                && args[1] is String
                && (args[1] as String).equals("player", true)
        ) {
            playerNpc(
                    sender.location,
                    args[0] as String,
                    sender.uniqueId,
                    "test.js",
                    false
            )
            Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), Runnable {
                sender.kickPlayer("プレイヤーNPCを設置しました。再ログインしてください。")
            })
        } else {
            npc(
                    sender.location,
                    EntityType.VILLAGER,
                    args[0] as String,
                    "test.js",
                    false
            )
        }
    }

    @Cmd(
            desc = ["Material"],
            args = [Material::class],
            examples = ["packetRecipe {getMaterial cauldron}"],
            text = "自身にレシピ開放パケットを送ります。"
    )
    fun packetRecipe(sender: Player, args: ArrayList<Any>) {
        Notification.recipeNotification(sender, args[0] as Material)
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
            text = "錬金マテリアルを適用もしくは適用されたアイテムを取得します。"
    )
    fun item(sender: Player, args: ArrayList<Any>) {
        val item = AlchemyItemStatus.getItem(
                if (args[0] is AlchemyMaterial) args[0] as AlchemyMaterial else AlchemyMaterial.getMaterial(args[0].toString()),
                sender.inventory.itemInMainHand
        )
        if (sender.inventory.itemInMainHand.type == Material.AIR) {
            sender.inventory.setItemInMainHand(item)
        }
    }

    @Cmd(
            desc = ["Integer"],
            args = [Integer::class],
            examples = ["quality 1", "quality 100"],
            text = "錬金マテリアルに指定した品質を適用します。"
    )
    fun quality(sender: Player, args: ArrayList<Any>) {
        val item = AlchemyItemStatus.load(sender.inventory.itemInMainHand)
        if (item != null) {
            item.quality = (args[0] as String).toInt()
            sender.inventory.setItemInMainHand(item.create())
        }
    }

    @Cmd(
            text = "ブロックの破壊をON・OFFします。"
    )
    fun cbreak(sender: Player, args: ArrayList<Any>): Boolean {
        debugListener.isNonBreak = !debugListener.isNonBreak
        return debugListener.isNonBreak
    }

    @Cmd(
            text = "全てのイベントのログを表示します。"
    )
    fun chandle(sender: Player, args: ArrayList<Any>): Boolean {
        return debugListener.changeAllHandles()
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
        val status = PlayerSaveManager.INSTANCE.getChar((args[0] as Player).uniqueId)
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
            desc = ["Stats"],
            examples = ["stats"],
            text = "ステータスを表示"
    )
    fun stats(sender: Player, args: ArrayList<Any>) {
        sender.sendMessage("")
        sender.sendMessage("level ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.level}")
        sender.sendMessage("maxHp ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.maxHp}")
        sender.sendMessage("hp ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.hp}")
        sender.sendMessage("maxMp ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.maxMp}")
        sender.sendMessage("mp ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.mp}")
        sender.sendMessage("atk ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.atk}")
        sender.sendMessage("def ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.def}")
        sender.sendMessage("speed ${PlayerSaveManager.INSTANCE.getChar(sender.uniqueId).charStats.speed}")
    }

    @Cmd(
            desc = ["Lore"],
            examples = ["lore"],
            text = "Loreを表示"
    )
    fun lore(sender: Player, args: ArrayList<Any>) {
        val item = sender.inventory.itemInMainHand
        if(item.hasItemMeta()) {
            val meta = item.itemMeta!!
            if(meta.hasLore()) {
                for(str in meta.lore!!) {
                    Chore.log(str)
                    sender.sendMessage(str)
                }
            }
        }
    }

    @Cmd(
            desc = ["Test"],
            examples = ["test"],
            text = "・・・"
    )
    fun test(sender: Player, args: ArrayList<Any>) {
        if (args.size != 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin()) {
                when (args[0]) {
                    "c" -> C(sender.world, sender.player)
                    "caneHorse" -> CaneHorse(sender.world, sender.player)
                    "z" -> AtelierEntityUtils.INSTANCE.spawn(TargetEntityTypes.valueOf(args[1].toString().toUpperCase()), sender.location)
                    "puni" -> AtelierEntityUtils.INSTANCE.spawn(Puni(sender.world), sender.location)
                }
            }
        }
    }

}