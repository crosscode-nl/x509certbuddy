@file:JvmName("HexDumpWrapper")
package nl.crosscode.x509certbuddy.wrappers

import nl.crosscode.x509certbuddy.ui.html.components.hex
import nl.crosscode.x509certbuddy.ui.html.components.htmlLayoutBody
import java.security.cert.X509Certificate

fun getHex(cert: X509Certificate): String {
    return htmlLayoutBody {
        hex(cert.encoded)
    }.serialize()
}
