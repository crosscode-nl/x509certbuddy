package nl.crosscode.x509certbuddy.utils

import java.security.cert.X509Certificate
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun getBase64(cert: X509Certificate): String {
    return Base64.encode(cert.encoded)
}
