package net.firiz.renewatelier.tools

fun main() {
    val maxLevel = 300
    var v = 100L
    var up = 1.01
    val down = 0.6
    var sb = StringBuilder("0, ")
    for (i in 0 until maxLevel) {
        if (i != 0 && i != 1) {
            if ((i + 1) % 10 == 0) {
                v = v.times(1.1).toLong()
                println(sb.toString() + "// " + (i + 1) + "lv")
                sb = StringBuilder()
            } else if ((i + 1) % 10 == 1) {
                v = v.times(down).toLong()
                up += 0.004
            }
        }
        sb.append("$v, ")
        v = v.times(up).toLong()
        v += (i * 5).toLong()
    }
}