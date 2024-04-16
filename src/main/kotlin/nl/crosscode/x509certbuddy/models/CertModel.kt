package nl.crosscode.x509certbuddy.models

import java.security.cert.CertificateEncodingException
import java.security.cert.X509Certificate
import java.util.*

class CertModel(cert: X509Certificate) {
    private val der: String
    private val serial: String
    private val subject: String
    private val issuer: String
    private val notBefore: Date
    private val notAfter: Date

    init {
        val der = try {
            Base64.getEncoder().encodeToString(cert.encoded)
        } catch (e: CertificateEncodingException) {
            ""
        }
        this.der = der
        serial = cert.serialNumber.toString(16)
        subject = cert.subjectX500Principal.name
        issuer = cert.issuerX500Principal.name
        notBefore = cert.notBefore
        notAfter = cert.notAfter
    }
}
