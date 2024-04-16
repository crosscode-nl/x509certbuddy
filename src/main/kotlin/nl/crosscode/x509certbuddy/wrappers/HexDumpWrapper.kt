@file:JvmName("HexDumpWrapper")
package nl.crosscode.x509certbuddy.wrappers

import java.security.cert.X509Certificate

fun getHex(cert: X509Certificate): String? {
    return performCommand(cert, "hexdump", "-C")
}
