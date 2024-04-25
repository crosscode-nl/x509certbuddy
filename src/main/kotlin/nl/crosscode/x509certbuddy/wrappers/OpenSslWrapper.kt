@file:JvmName("OpenSslWrapper")
package nl.crosscode.x509certbuddy.wrappers

import com.jetbrains.rd.util.string.printToString
import nl.crosscode.x509certbuddy.ui.html.components.cell
import nl.crosscode.x509certbuddy.ui.html.components.row
import nl.crosscode.x509certbuddy.ui.html.components.table
import org.apache.commons.io.FileUtils
import org.bouncycastle.asn1.util.ASN1Dump
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.security.PublicKey
import java.security.cert.*
import java.util.*
import java.security.cert.X509Certificate as JavaX509Certificate

fun getCertDetails(cert: JavaX509Certificate): String {
    val doc = StringBuilder()
    doc.append("<html>" +
            "<head>" +
            "<style>" +
            "body { font-family: monospace; background-color: #222222; }" +
            "b { color: #0000FF; }" +
            "td { vertical-align: top; text-style: strong; }" +
            "</style></head>")
    doc.append("<body>")

    var validityCol = "red"
    val now = Calendar.getInstance().time
    if (cert.notBefore <= now && now <= cert.notAfter) {
        validityCol = "green"
    }
    val kv = listOf(
        "Version" to cert.version,
        "Subject" to "<span style=\"font-size: large; color: white; \">${cert.subjectX500Principal.name}</span>",
        "Issuer" to cert.issuerX500Principal.name,
        "SerialNumber" to cert.serialNumber.toString(16),
        "PubKey" to "<pre>${getPem(cert.publicKey)}</pre>",
        "Validity" to "<span style=\"color: $validityCol;\">${cert.notBefore} to ${cert.notAfter}</span>",
        "Signature Algorithm" to cert.sigAlgName,
        "Raw" to "<pre>${cert.printToString()}</pre>"
    )

    table{
        for ((k, v) in kv) {
            row {
                cell(k) {}
                cell(v.toString()) {}
            }
        }
    }.serialize(doc)

    doc.append("</body></html>")
    return doc.printToString()
}



fun getPem(publicKey: PublicKey): String {
    val stringWriter = StringWriter()
    val pemWriter = JcaPEMWriter(stringWriter)
    pemWriter.writeObject(publicKey)
    pemWriter.close()
    return stringWriter.toString()
}


fun getPem(cert: JavaX509Certificate): String {
    val stringWriter = StringWriter()
    val pemWriter = JcaPEMWriter(stringWriter)
    pemWriter.writeObject(cert)
    pemWriter.close()
    return stringWriter.toString()
}

fun getAsn1(cert: JavaX509Certificate): String? {
    return ASN1Dump.dumpAsString(X509CertificateHolder(cert.encoded).toASN1Structure(), true)
}

fun getValidation(cert: JavaX509Certificate, certificateList: List<JavaX509Certificate>): String {
    try {
        val trusted: MutableList<JavaX509Certificate> = ArrayList()
        val untrusted: MutableList<JavaX509Certificate> = ArrayList()
        var certToFindParentOf: JavaX509Certificate? = cert
        while (certToFindParentOf != null) {
            val finalCertToFindParentOf: JavaX509Certificate = certToFindParentOf
            val foundParent = certificateList.stream()
                .filter { x: JavaX509Certificate -> x.subjectX500Principal.name == finalCertToFindParentOf.issuerX500Principal.name && x !== finalCertToFindParentOf }
                .findFirst()
            if (foundParent.isPresent) {
                if (foundParent.get().subjectX500Principal.name == foundParent.get().issuerX500Principal.name) {
                    trusted.add(foundParent.get())
                } else {
                    untrusted.add(foundParent.get())
                }
                certToFindParentOf = foundParent.get()
            } else {
                certToFindParentOf = null
            }
        }
        return getValidationBC(cert, trusted, untrusted)
    } catch (e: Exception) {
        return e.printToString()
    }
}
// getValidationBC returns the validation of the certificate using Bouncy Castle
fun getValidationBC(certToValidate: JavaX509Certificate, trustedRootCerts: List<JavaX509Certificate>, intermediateCerts: List<JavaX509Certificate>): String {
        try {
            // Create the selector that specifies the starting certificate
            val selector = X509CertSelector()
            selector.certificate = certToValidate

            // Create a set of trust anchors (trusted root CA certificates)
            val trustAnchors = HashSet<TrustAnchor>()
            for (trustedRootCert in trustedRootCerts) {
                trustAnchors.add(TrustAnchor(trustedRootCert, null))
            }

            if (trustAnchors.isEmpty()) {
                return "Not validated. Please provide a trusted root certificate and the intermediate certificates."
            }

            // Configure the PKIX certificate builder algorithm parameters
            val pkixParams = PKIXBuilderParameters(trustAnchors, selector)

            // Disable CRL checks (this is done manually as additional path validation step)
            pkixParams.isRevocationEnabled = false

            // Specify a list of intermediate certificates
            val intermediateCertStore = CollectionCertStoreParameters(intermediateCerts)
            pkixParams.addCertStore(CertStore.getInstance("Collection", intermediateCertStore))

            // Build and verify the certification path
            val builder = CertPathBuilder.getInstance("PKIX", BouncyCastleProvider())
            val pkixCertPathBuilderResult = builder.build(pkixParams) as PKIXCertPathBuilderResult
            return "<html>" +
                    "<body>" +
                    "<h1>Validation successful</h1>" +
                    "<pre>"+
                    pkixCertPathBuilderResult.printToString() +
                    "</body>" +
                    "</html>"
        } catch (ex: CertPathBuilderException) {
            // Handling exception
            return ex.printToString()
        }
}

@Throws(IOException::class)
private fun writeAllCerts(certsToWrite: List<JavaX509Certificate>, directory: File) {
    for (cert in certsToWrite) {
        val pem = getPem(cert)
        val f = File(directory, cert.serialNumber.toString(16) + ".pem")
        FileUtils.writeStringToFile(f, pem, StandardCharsets.UTF_8)
    }
}
