package net.firiz.renewatelier.constants.runs

fun main() {
    var v = 20L
    val maxLevel = 100
    var sb = StringBuilder("0, ")
    for (i in 0 until maxLevel) {
        if ((i + 1) % 10 == 0) {
            println(sb.toString() + "// " + (i + 1) + "lv")
            sb = StringBuilder()
        }
        v += (i * 0.1).toLong()
        sb.append("$v, ")
//        println(v)
    }
}