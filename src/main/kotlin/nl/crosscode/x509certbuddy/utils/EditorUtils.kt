package nl.crosscode.x509certbuddy.utils

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.wm.ToolWindowManager
import nl.crosscode.x509certbuddy.decoder.CertRetriever
import nl.crosscode.x509certbuddy.decoder.RetrievedCert
import nl.crosscode.x509certbuddy.ui.CertEditorElementRender
import nl.crosscode.x509certbuddy.X509CertAssistantFactory
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class EditorUtils {
    fun readCertsFromEditor(editor: Editor?) {
        if (editor == null) return
        val project = editor.project
        val allText = editor.document.text
        try {
            val certRetriever = CertRetriever(editor)
            val certs = certRetriever.retrieveCerts(allText)
            if (project != null) {
                val tw = ToolWindowManager.getInstance(project).getToolWindow("X.509 Cert Buddy")
                if (tw != null) {
                    tw.show()
                    X509CertAssistantFactory.getInstance(project)
                        ?.addCerts(certs.stream().map { obj: RetrievedCert -> obj.certificate }
                            .collect(Collectors.toList()))
                }
            }
            addCertsToEditor(certs, editor)
        } catch (ignored: CertificateException) {
        }
    }

    fun removeEditor(editor: Editor?) {
        synchronized(lock) {
            certs.remove(editor)
            val inlayList = inlays[editor]
            inlayList?.forEach(Consumer { obj: Inlay<*>? -> obj!!.dispose() })
            inlays.remove(editor)
        }
    }

    private fun addCertsToEditor(retrievedCerts: List<RetrievedCert>, editor: Editor) {
        synchronized(lock) {
            var certsOfEditor = certs[editor]
            var certsUpdate = true
            if (certsOfEditor != null && certsOfEditor.size == retrievedCerts.size) {
                certsUpdate = false
                for (cert in retrievedCerts) {
                    if (certsOfEditor.stream().noneMatch { x: X509Certificate ->
                            try {
                                return@noneMatch x.encoded.contentEquals(cert.certificate.encoded)
                            } catch (e: CertificateEncodingException) {
                                throw RuntimeException(e)
                            }
                        }) {
                        certsUpdate = true
                        break
                    }
                }
            }
            if (!certsUpdate) return
            removeEditor(editor)
            val inlayList: MutableList<Inlay<*>?> = ArrayList()
            inlays.put(editor, inlayList)
            certsOfEditor = ArrayList()
            certs.put(editor, certsOfEditor)
            for (retrievedCert in retrievedCerts) {
                certsOfEditor.add(retrievedCert.certificate)
                inlayList.add(
                    editor.inlayModel.addBlockElement(
                        retrievedCert.offset,
                        false,
                        true,
                        0,
                        CertEditorElementRender(retrievedCert.certificate)
                    )
                )
            }
            editor.component.repaint()
        }
    }

    companion object {
        private val lock = Any()
        private val inlays: Dictionary<Editor, List<Inlay<*>?>> = Hashtable()
        private val certs: Dictionary<Editor, MutableList<X509Certificate>> = Hashtable()
    }
}
