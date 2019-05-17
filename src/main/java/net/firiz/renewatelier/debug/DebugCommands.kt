package net.firiz.renewatelier.debug

import net.firiz.renewatelier.debug.annotations.Cmd
import net.firiz.renewatelier.nodification.Nodification
import net.firiz.renewatelier.npc.NPCManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

class DebugCommands {

    @Cmd
    fun getLocation(sender: Player, args: ArrayList<Any>): Location {
        return sender.location
    }

    @Cmd
    fun getPlayer(sender: Player, args: ArrayList<Any>): Player {
        return sender
    }

    @Cmd(
            desc = ["Name?"],
            args = [String::class]
    )
    fun getUUID(sender: Player, args: ArrayList<Any>): UUID {
        return if(args.size == 0) sender.uniqueId else UUID.fromString(args[0].toString())
    }

    @Cmd(
            desc = ["Name"],
            args = [String::class]
    )
    fun getMaterial(sender: Player, args: ArrayList<Any>): Material {
        return Material.valueOf((args[0] as String).toUpperCase())
    }

    @Cmd(
            desc = ["Name"],
            args = [String::class]
    )
    fun getEntityType(sender: Player, args: ArrayList<Any>): EntityType {
        return EntityType.valueOf((args[0] as String).toUpperCase())
    }

    @Cmd(
            desc = ["Location", "EntityType", "Name", "Script"],
            args = [Location::class, EntityType::class, String::class, String::class]
    )
    fun npc(sender: Player, args: ArrayList<Any>) {
        NPCManager.INSTANCE.createNPC(
                args[0] as Location,
                args[1] as EntityType,
                args[2] as String,
                args[3] as String
        )
    }

    @Cmd(
            desc = ["Location", "Name", "UUID", "Script"],
            args = [Location::class, String::class, UUID::class, String::class]
    )
    fun playerNpc(sender: Player, args: ArrayList<Any>) {
        NPCManager.INSTANCE.createNPCPlayer(
                args[0] as Location,
                args[1] as String,
                args[2] as UUID,
                args[3] as String
        )
    }

    @Cmd(
            desc = ["Material"],
            args = [Material::class]
    )
    fun packetRecipe(sender: Player, args: ArrayList<Any>) {
        Nodification.recipeNodification(sender, args[0] as Material)
    }

}