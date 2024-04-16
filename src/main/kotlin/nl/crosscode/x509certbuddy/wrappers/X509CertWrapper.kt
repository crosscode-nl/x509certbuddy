package nl.crosscode.x509certbuddy.wrappers

import java.security.cert.X509Certificate

class X509CertWrapper(val cert: X509Certificate) {
    override fun toString(): String {
        return cert.subjectX500Principal.name + " (0x" + cert.serialNumber.toString(16) + ")"
    }
}
