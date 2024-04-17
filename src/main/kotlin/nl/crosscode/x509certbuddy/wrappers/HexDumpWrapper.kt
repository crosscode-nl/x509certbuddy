@file:JvmName("HexDumpWrapper")
package nl.crosscode.x509certbuddy.wrappers

import java.security.cert.X509Certificate

// getHex returns the hexdump of the certificate
00000000  30 82 05 D2 30 82 04 BA  A0 03 02 01 02 02 10 08  |0...0...........|
fun getHex(cert: X509Certificate): String {
    val address = 0 // Start address
    val data = cert.encoded
    val length = data.size
    val hex = StringBuilder()
    val ascii = StringBuilder()
    var i = 0
    while (i < length) {
        hex.append(String.format("%08X  ", address + i))
        var j = 0
        while (j < 16) {
            if (i + j < length) {
                val b = data[i + j].toInt() and 0xFF
                hex.append(String.format("%02X ", b))
                ascii.append(if (b in 32..126) b.toChar() else '.')
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
