@file:JvmName("OpenSslWrapper")
package nl.crosscode.x509certbuddy.wrappers

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.cert.X509Certificate

fun getCertDetails(cert: X509Certificate): String? {
    return performCommand(cert, "openssl", "x509", "-inform", "DER", "-noout", "-text")
}

fun getPem(cert: X509Certificate): String? {
    return performCommand(cert, "openssl", "x509", "-inform", "DER")
}

fun getAsn1(cert: X509Certificate): String? {
    return performCommand(cert, "openssl", "asn1parse", "-i", "-inform", "DER")
}

fun getValidation(cert: X509Certificate, certificateList: List<X509Certificate>): String? {
    try {
        val trusted: MutableList<X509Certificate> = ArrayList()
        val untrusted: MutableList<X509Certificate> = ArrayList()
        var certToFindParentOf: X509Certificate? = cert
        while (certToFindParentOf != null) {
            val finalCertToFindParentOf: X509Certificate = certToFindParentOf
            val foundParent = certificateList.stream()
                .filter { x: X509Certificate -> x.subjectX500Principal.name == finalCertToFindParentOf.issuerX500Principal.name && x !== finalCertToFindParentOf }
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
        val tmpdir = Files.createTempDirectory("").toFile()
        try {
            writeAllCerts(
                certificateList,
                tmpdir
            )
            val pb = ProcessBuilder()
            val command: MutableList<String> = ArrayList()
            command.add("openssl")
            command.add("verify")
            for (trustedCert in trusted) {
                command.add("-trusted")
                command.add(File(tmpdir, trustedCert.serialNumber.toString(16) + ".pem").absolutePath)
            }
            for (untrustedCert in untrusted) {
                command.add("-untrusted")
                command.add(File(tmpdir, untrustedCert.serialNumber.toString(16) + ".pem").absolutePath)
            }
            command.add("-verbose")
            command.add(File(tmpdir, cert.serialNumber.toString(16) + ".pem").absolutePath)
            pb.command(command)
            try {
                val p = pb.start()
                return String(p.inputStream.readAllBytes(), StandardCharsets.UTF_8)
            } catch (e: IOException) {
                return null
            }
        } finally {
            FileUtils.deleteDirectory(tmpdir)
        }
    } catch (e: Exception) {
        return null
    }
}

@Throws(IOException::class)
private fun writeAllCerts(certsToWrite: List<X509Certificate>, directory: File) {
    for (cert in certsToWrite) {
        val pem = getPem(cert)
        val f = File(directory, cert.serialNumber.toString(16) + ".pem")
        FileUtils.writeStringToFile(f, pem, StandardCharsets.UTF_8)
    }
}
