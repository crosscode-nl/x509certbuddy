package nl.crosscode.x509certbuddy.wrappers

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.cert.CertificateEncodingException
import java.security.cert.X509Certificate

fun performCommand(cert: X509Certificate, vararg command: String): String? {
    val pb = ProcessBuilder().apply {
        command(*command)
        redirectErrorStream(true)
    }
    try {
        val p = pb.start()
        p.outputStream.write(cert.encoded)
        p.outputStream.flush()
        p.outputStream.close()
        return String(p.inputStream.readAllBytes(), StandardCharsets.UTF_8)
    } catch (e: IOException) {
        return null
    } catch (e: CertificateEncodingException) {
        return null
    }
}
