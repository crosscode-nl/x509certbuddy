package nl.crosscode.x509certbuddy.ui.html.components

fun HtmlBaseLayout.hex(data: ByteArray): Unit {
    val address = 0 // Start address
    val length = data.size
    val hex = StringBuilder()
    val ascii = StringBuilder()
    var i = 0
    while (i < length) {
        hex.append("<b><span style=\"color: #808080\">")
        hex.append(String.format("%08X  ", address + i))
        hex.append("</span></b>")
        var j = 0
        while (j < 16) {
            if (i + j < length) {
                val b = data[i + j].toInt() and 0xFF
                if (b in 32..126) {
                    if (j%2 == 0) {
                        hex.append("<span style=\"color: #DDDDDD\">")
                        ascii.append("<span style=\"color: #DDDDDD\">")
                    } else {
                        hex.append("<span style=\"color: #BEBEBE\">")
                        ascii.append("<span style=\"color: #BEBEBE\">")
                    }
                } else {
                    if (j%2 == 0) {
                        hex.append("<span style=\"color: #DDDD77\">")
                        ascii.append("<span style=\"color: #DDDD77\">")
                    } else {
                        hex.append("<span style=\"color: #BEBE77\">")
                        ascii.append("<span style=\"color: #BEBE77\">")
                    }
                }
                hex.append(String.format("%02X ", b))
                hex.append("</span>")
                ascii.append(if (b in 32..126) b.toChar() else '.')
                ascii.append("</span>")
            } else {
                hex.append("   ")
            }
            if (j == 7) {
                hex.append(" ")
            }
            j++
        }
        hex.append(" |")
        hex.append(ascii)
        hex.append("|\n")
        ascii.setLength(0)
        i += 16
    }
    return hex.toString()
}