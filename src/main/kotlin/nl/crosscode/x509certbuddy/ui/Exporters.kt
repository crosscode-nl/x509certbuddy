package nl.crosscode.x509certbuddy.ui

import com.google.gson.GsonBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.util.application
import nl.crosscode.x509certbuddy.models.CertModel
import nl.crosscode.x509certbuddy.utils.getBase64
import nl.crosscode.x509certbuddy.wrappers.getPem
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.cert.CertificateEncodingException
import java.security.cert.X509Certificate
import java.util.*
import java.util.stream.Collectors
import javax.swing.JComponent


class Exporters(private val certificateList: List<X509Certificate?>, private val parent: JComponent) {
    private var selectedCert: X509Certificate? = null

    fun setSelectedCertificate(selected: X509Certificate?) {
        this.selectedCert = selected
    }

    fun copyPem() {
        selectedCert?.let {
            val pem = getPem(it)
            if (pem.isEmpty()) return
            copyToClipboard(pem)
        }
    }

    fun exportPEM() {
        selectedCert?.let {
            val pem = getPem(it)
            if (pem.isEmpty()) return
            saveToFile(pem.toByteArray(StandardCharsets.UTF_8), it.serialNumber.toString(16) + ".pem")
        }
    }

    fun copyBase64() {
        selectedCert?.let {
            copyToClipboard(getBase64(it))
        }
    }

    fun exportDER() {
        selectedCert?.run {
            try {
                saveToFile(encoded, serialNumber.toString(16) + ".der")
            } catch (ignored: CertificateEncodingException) {
            }
        }
    }

    private fun getParent(certificate: X509Certificate): X509Certificate? {
        if (certificate.subjectX500Principal.name == certificate.issuerX500Principal.name) return null
        return certificateList.stream()
            .filter { x: X509Certificate? -> x!!.subjectX500Principal.name == certificate.issuerX500Principal.name }
            .findFirst().orElse(null)
    }

    fun copyCertChainPEM() {
        certChain.ifPresent { data: String -> this.copyToClipboard(data) }
    }

    fun exportCertChainPEM() {
        certChain.ifPresent { certChain: String ->
            saveToFile(
                certChain.toByteArray(),
                selectedCert!!.serialNumber.toString(16) + "_chain.pem"
            )
        }
    }

    fun copyAll() {
        copyToClipboard(internalExportAll())
    }

    fun exportAll() {
        saveToFile(internalExportAll().toByteArray(), "all.json")
    }

    private val certChain: Optional<String>
        get() {
            if (selectedCert == null) return Optional.empty()
            val builder = StringBuilder()
            val pem = getPem(selectedCert!!)
            if (pem.isEmpty()) return Optional.empty() // this is to check openssl is working...

            builder.append(pem)
            var currentCert = getParent(selectedCert!!)
            while (currentCert != null) {
                builder.append(getPem(currentCert))
                currentCert = getParent(currentCert)
            }
            return Optional.of(builder.toString())
        }

    private fun internalExportAll(): String {
        val listOfCertModels = certificateList.stream().map { cert: X509Certificate? ->
            CertModel(
                cert!!
            )
        }.collect(Collectors.toList())
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()
        return gson.toJson(listOfCertModels)
    }

    private fun copyToClipboard(data: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val ss = StringSelection(data)
        clipboard.setContents(ss, ss)
    }

    private fun saveToFile(data: ByteArray, fileNameHint: String) {
        val fsd = FileSaverDescriptor("Save File As", "")
        val dlg = FileChooserFactory.getInstance().createSaveFileDialog(fsd, parent)
        val wrapper = dlg.save(fileNameHint) ?: return
        val vf = wrapper.getVirtualFile(true) ?: return
        if (application.isDispatchThread) {
            application.runWriteAction {
                try {
                    vf.setBinaryContent(data)
                } catch (ignored: IOException) {
                }
            }
        } else {
            application.invokeLater {
                application.runWriteAction {
                    try {
                        vf.setBinaryContent(data)
                    } catch (ignored: IOException) {
                    }
                }
            }
        }
    }
}
