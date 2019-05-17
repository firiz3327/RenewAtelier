package net.firiz.renewatelier.debug

import net.firiz.renewatelier.debug.annotations.Cmd
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.LinkedHashSet

class DebugManager {
    private val debugCommand = DebugCommands()

    fun command(player: Player, str: String) {
        val split = split(player, str)
        try {
            val invokeArgs = arrayListOf<Any>()
            for (s in split) {
                if (s is SB) {
                    for (v in s.getValues()) {
                        if (v.isNotEmpty()) {
                            invokeArgs.add(s)
                        }
                    }
                } else {
                    invokeArgs.add(s)
                }
            }
            player.sendMessage(invoke(player, invokeArgs, true).toString())
        } catch (ex: IllegalArgumentException) {
            player.sendMessage("Not found command for ${split.single()}.")
        }
    }

    fun split(player: Player, str: String): LinkedHashSet<Any> {
        var list = linkedSetOf<Any>()
        val stack = Stack<LinkedHashSet<Any>>()

        var sb = SB()
        for (c in str) {
            list.add(sb)
            if (c == '{') {
                stack.push(list)

                list = linkedSetOf()
                sb = SB()
                continue
            }
            if (c == '}') {
                val v = stack.pop()
                for (k in LinkedHashSet(list)) {
                    if (k.toString().isEmpty()) {
                        list.remove(k)
                    }
                }
                if (list.isNotEmpty()) {
                    v.add(UniqueWrap(invoke(player, list, false)!!))
                }
                list = v
                sb = SB()
                continue
            }
            sb.sb.append(c)
        }
        return list
    }

    fun invoke(player: Player, list: Collection<Any>, shape: Boolean): Any? {
        val lhs = linkedSetOf<Any>()
        for (v in list) {
            if (v is SB) {
                for (v2 in v.getValues()) {
                    lhs.add(v2)
                }
            } else {
                lhs.add(v)
            }
        }

        val args = arrayListOf<Any>()
        var cmd = ""
        lhs.withIndex().forEach {
            when {
                it.index == 0 -> cmd = it.value.toString()
                it.value is UniqueWrap -> args.add((it.value as UniqueWrap).data)
                else -> args.add(it.value)
            }
        }

        if (cmd.isNotEmpty()) {
            val result = run {
                var r: Any? = null
                var notFound = true
                for (it in DebugCommands::class.java.methods) {
                    if (it.isAnnotationPresent(Cmd::class.java)
                            && it.name.equals(cmd, true)) {
                        r = it.invoke(debugCommand, player, args)
                        notFound = false
                        break
                    }
                }
                if(notFound) {
                    throw IllegalArgumentException("not found command for $cmd.")
                }
                r
            }
            if (shape) {
                return InvokeResult(
                        if (result == null) InvokeResult.Type.VOID else InvokeResult.Type.DATA,
                        result
                )
            }
            return result!!
        }
        return lhs
    }
}

class InvokeResult(val type: Type, val data: Any?) {
    enum class Type {
        VOID,
        DATA
    }

    override fun toString(): String {
        return "[$type]: $data"
    }
}

class UniqueWrap(val data: Any) {
    private val uuid: UUID = UUID.randomUUID()

    override fun toString(): String {
        return data.toString()
    }
}

class SB(val sb: StringBuilder = StringBuilder()) {
    private val uuid: UUID = UUID.randomUUID()

    override fun toString(): String {
        return sb.toString().trim()
    }

    fun getValues(): List<String> {
        return toString().split(" ")
    }
}