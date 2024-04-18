package nl.crosscode.x509certbuddy.utils

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.application
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
            var certs: List<RetrievedCert>? = null
            application.runReadAction {
                val certRetriever = CertRetriever(editor)
                certs = certRetriever.retrieveCerts(allText)
            }
            if (project != null && certs != null) {
                val tw = ToolWindowManager.getInstance(project).getToolWindow("X.509 Cert Buddy")
                if (tw != null) {
                    tw.show()
                    application.runWriteAction {
                        X509CertAssistantFactory.getInstance(project)
                            ?.addCerts(certs!!.map { it.certificate } )
                    }

                }
            }
            if (certs!=null) {
                application.runWriteAction {
                    addCertsToEditor(certs!!, editor)
                }
            }
        } catch (ignored: CertificateException) {
        }
    }

    private fun removeEditor(editor: Editor?) {
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
            application.runReadAction {
                if (certsOfEditor != null && certsOfEditor.size == retrievedCerts.size) {
                    certsUpdate = false
                    retrievedCerts.any {rc ->
                        certsOfEditor.any { rc.certificate==it }
                    }.let { if (it) {
                        certsUpdate = true
                    }}
                }
            }
            if (!certsUpdate) return
            application.runWriteAction {
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
            }
        }
        editor.component.repaint()
    }

    companion object {
        private val lock = Any()
        private val inlays: Dictionary<Editor, List<Inlay<*>?>> = Hashtable()
        private val certs: Dictionary<Editor, MutableList<X509Certificate>> = Hashtable()
    }
}
