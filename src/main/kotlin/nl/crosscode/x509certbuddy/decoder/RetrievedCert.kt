package nl.crosscode.x509certbuddy.decoder

import com.intellij.openapi.editor.Editor
import java.security.cert.X509Certificate

class RetrievedCert(val editor: Editor?, val offset: Int, @JvmField val certificate: X509Certificate)
