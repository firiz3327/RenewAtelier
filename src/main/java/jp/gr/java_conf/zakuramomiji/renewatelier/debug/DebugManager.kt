package jp.gr.java_conf.zakuramomiji.renewatelier.debug

import jp.gr.java_conf.zakuramomiji.renewatelier.debug.annotations.Cmd

class DebugManager {
    private val debugCommand = DebugCommands()

    // tp ${getPlayer,firiz} > ${getLocation,1,2,3}

    fun command(args: Array<String>) {
        val invokeArgs = arrayListOf<Any>(args.copyOfRange(1, args.size))
        IntRange(0, invokeArgs.size).forEach { i ->
            val str = invokeArgs[i] as String
            if (str.startsWith("\${") && str.endsWith("}")) {
                str.substring(2, str.length - 1).split(".")
            }
        }
    }

    private fun invoke(args: Array<String>): Any {
        val methods = DebugCommands::class.java.declaredMethods
        return run loop@{
            methods.forEach {
                it.isAccessible = true
                if (it.isAnnotationPresent(Cmd::class.java)) {
                    return@loop it.invoke(debugCommand, args)
                }
            }
        }
    }
}