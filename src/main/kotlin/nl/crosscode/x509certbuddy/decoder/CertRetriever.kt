package nl.crosscode.x509certbuddy.decoder

import com.intellij.openapi.editor.Editor
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.stream.Collectors

class CertRetriever(private val editor: Editor?) {
    private val certFactory: CertificateFactory = CertificateFactory.getInstance("X.509")

    @Throws(CertificateException::class)
    fun retrieveCerts(data: ByteArray): List<RetrievedCert> {
        if (data.size < 64) return listOf()
        if (data[0].toInt() == 0x30) {
            try {
                ByteArrayInputStream(data).use { bais ->
                    return CertificateFactory.getInstance("X.509").generateCertificates(bais).stream()
                        .map { c: Certificate? -> RetrievedCert(null, 0, c as X509Certificate) }
                        .collect(Collectors.toList())
                }
            } catch (e: IOException) {
            } catch (e: CertificateException) {
            }
        }
        return retrieveCerts(String(data))
    }

    @Throws(CertificateException::class)
    fun retrieveCerts(text: String): List<RetrievedCert> {
        val certificates: MutableList<RetrievedCert> = ArrayList()
        getCertsFromText(text, certificates)
        return certificates
    }

    private fun getCertsFromText(text: String, certificates: MutableList<RetrievedCert>) {
        for (potentialCert in findPotentialCerts(text)) {
            try {
                for (cert in certFromBytes(potentialCert.potentialCert)) {
                    certificates.add(RetrievedCert(editor, potentialCert.offset, cert))
                }
            } catch (e: Exception) {
            } // Ignoring it for now due to the brute force nature of cert finding.
        }
    }

    @Throws(CertificateException::class)
    private fun certFromBytes(bytes: ByteArray?): List<X509Certificate> {
        val `in`: InputStream = ByteArrayInputStream(bytes)
        return certFactory.generateCertificates(`in`).stream().filter { c -> c is X509Certificate } .map { c: Certificate -> c as X509Certificate }
            .collect(Collectors.toList())
    }

    private fun findPotentialCerts(text: String): List<PotentialCert> {
        val blockingDecoders: MutableList<Decoder> = ArrayList()
        val potentialCerts: MutableList<PotentialCert> = ArrayList()
        var decoders: MutableList<Decoder> = ArrayList()
        var offset = -1
        for (c in text.toCharArray()) {
            offset++
            if (c == 'M') {
                decoders.add(Decoder(offset))
            }
            for (decoder in decoders) {
                if (decoder.add(c)) { // decoder is certainly done
                    if (blockingDecoders.stream()
                            .noneMatch { blockingDecoder: Decoder -> blockingDecoder.isOffsetInsideRange(decoder.originalOffset) }
                    ) { // decoder is not working inside another decoders range which already succeeded.
                        blockingDecoders.add(decoder)
                        decodeToPotentialCerts(potentialCerts, decoder)
                    }
                }
            }
            decoders = decoders.stream().filter { decoder: Decoder -> !decoder.isDone }
                .collect(Collectors.toList())
        }
        for (decoder in decoders) {
            decodeToPotentialCerts(potentialCerts, decoder)
        }
        return potentialCerts
    }

    companion object {
        private fun decodeToPotentialCerts(potentialCerts: MutableList<PotentialCert>, decoder: Decoder) {
            val seqDerReader = SeqDerReader()
            try {
                val data = decoder.tryDecode()
                for (b in data) {
                    seqDerReader.read(b)
                }
                seqDerReader.setEof()
                if (!seqDerReader.isError) {
                    potentialCerts.add(PotentialCert(seqDerReader.getResult(), decoder.originalOffset))
                }
            } catch (e: OutOfMemoryError) {
            } catch (e: Exception) {
            }
        }
    }
}
