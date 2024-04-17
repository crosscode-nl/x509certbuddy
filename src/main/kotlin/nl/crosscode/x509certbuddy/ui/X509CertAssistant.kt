package nl.crosscode.x509certbuddy.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.PopupHandler
import nl.crosscode.x509certbuddy.decoder.CertRetriever
import nl.crosscode.x509certbuddy.decoder.RetrievedCert
import nl.crosscode.x509certbuddy.ui.CBActionManager.buildContextMenu
import nl.crosscode.x509certbuddy.wrappers.*
import java.awt.dnd.DropTarget
import java.io.File
import java.io.IOException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class X509CertAssistant(tw: ToolWindow) {
    private val exporters: Exporters

    private val x509Certificates: MutableList<X509Certificate> = ArrayList()
    private var selectedCertificate: X509Certificate? = null
    var rootPanel: JPanel? = null
    var certTree: JTree? = null
    var certDetailsTextPane: JTextPane? = null
    var pemTextPane: JTextPane? = null
    var asn1TextPane: JTextPane? = null
    var hexTextPane: JTextPane? = null
    var validationTextPane: JTextPane? = null

    init {
        exporters = Exporters(x509Certificates, rootPanel!!)
        tw.setTitleActions(java.util.List.of(buildContextMenu(exporters)))
        PopupHandler.installPopupMenu(certTree!!, buildContextMenu(exporters), "X509CertBuddy.CertTree.Actions")

        val model = certTree?.model as DefaultTreeModel
        val root = DefaultMutableTreeNode("Certs", true)
        model.setRoot(root)
        model.reload()
        certTree?.addTreeSelectionListener { this.treeSelectionChanged() }
        DropTarget(
            rootPanel,
            FileDropTargetListener { files: List<File> -> this.filesToProcess(files) }
        )
    }

    private fun filesToProcess(files: List<File>) {
        for (file in files) {
            try {
                val data = file.readBytes()
                val certRetriever = CertRetriever(null)
                addCerts(certRetriever.retrieveCerts(data).stream().map { x: RetrievedCert -> x.certificate }
                    .toList())
            } catch (e: IOException) {
            } catch (e: CertificateException) {
            }
        }
    }

    private fun resetUIToNoTreeSelection() {
        selectedCertificate = null
        exporters.setSelectedCertificate(null)
        updateFieldsWithCertDetails()
    }

    private fun treeSelectionChanged() {
        certTree?.let {
            when (val l = it.lastSelectedPathComponent) {
                is DefaultMutableTreeNode -> {
                    when (val userObject = l.userObject) {
                        is X509CertWrapper -> {
                            selectedCertificate = userObject.cert
                            exporters.setSelectedCertificate(selectedCertificate)
                            updateFieldsWithCertDetails()
                        }
                        else -> {
                            resetUIToNoTreeSelection()
                            return
                        }
                    }
                }
                else -> {
                    resetUIToNoTreeSelection()
                    return
                }
            }
        } ?: resetUIToNoTreeSelection()
    }

    private fun updateFieldsWithCertDetails() {
        certDetailsTextPane!!.text = null
        pemTextPane!!.text = null
        asn1TextPane!!.text = null
        hexTextPane!!.text = null
        validationTextPane!!.text = null
        if (selectedCertificate == null) return
        var details = getCertDetails(selectedCertificate!!)
        if (details == null) {
            details = selectedCertificate.toString()
        }
        certDetailsTextPane?.text = details
        certDetailsTextPane?.caretPosition = 0
        val pemString = getPem(selectedCertificate!!)
        pemTextPane?.text = pemString
        asn1TextPane?.text = getAsn1(selectedCertificate!!)
        hexTextPane?.text = getHex(selectedCertificate!!)
        validationTextPane?.text = getValidation(selectedCertificate!!, x509Certificates)
    }


    fun removeCert() {
        x509Certificates.remove(selectedCertificate)
        buildTree()
    }

    fun removeAllCerts() {
        x509Certificates.clear()
        buildTree()
    }


    fun addCerts(certs: List<X509Certificate>) {
        var newCerts = false
        for (cert in certs) {
            if (x509Certificates.stream().noneMatch { x: X509Certificate? ->
                    try {
                        return@noneMatch x!!.encoded.contentEquals(cert.encoded)
                    } catch (e: CertificateEncodingException) {
                        throw RuntimeException(e)
                    }
                }) {
                newCerts = true
                break
            }
        }

        if (!newCerts) return
        x509Certificates.addAll(certs)
        removeDuplicateCerts()
        buildTree()
    }

    private fun removeDuplicateCerts() {
        val certs: MutableList<X509Certificate> = ArrayList()
        for (cert in x509Certificates) {
            if (certs.stream().anyMatch { x: X509Certificate ->
                    try {
                        return@anyMatch x.encoded.contentEquals(cert.encoded)
                    } catch (e: CertificateEncodingException) {
                        return@anyMatch false
                    }
                }) continue
            certs.add(cert)
        }
        x509Certificates.clear()
        x509Certificates.addAll(certs)
    }

    private fun buildTree() {
        selectedCertificate = null
        val model = certTree!!.model as DefaultTreeModel
        val root = DefaultMutableTreeNode("Certs", true)
        root.removeAllChildren()
        val rootCerts = x509Certificates.stream().filter { x: X509Certificate? ->
            x509Certificates.stream()
                .noneMatch { y: X509Certificate? -> y!!.subjectX500Principal.name == x!!.issuerX500Principal.name && x !== y }
        }
            .toList()
        for (cert in rootCerts) {
            val treeNode = DefaultMutableTreeNode(
                X509CertWrapper(
                    cert!!
                )
            )
            addChildren(treeNode, cert, x509Certificates)
            root.add(treeNode)
        }
        model.setRoot(root)
        model.reload()
        expandAllNodes(certTree, 0, 0)
    }

    private fun expandAllNodes(tree: JTree?, startingIndex: Int, rowCount: Int) {
        for (i in startingIndex until rowCount) {
            tree!!.expandRow(i)
        }

        if (tree!!.rowCount != rowCount) {
            expandAllNodes(tree, rowCount, tree.rowCount)
        }
    }

    fun addChildren(treeNode: DefaultMutableTreeNode, cert: X509Certificate?, certs: List<X509Certificate?>) {
        val children = certs.stream()
            .filter { x: X509Certificate? -> x!!.issuerX500Principal.name == cert!!.subjectX500Principal.name && x !== cert }
            .toList()
        for (child in children) {
            val childNode = DefaultMutableTreeNode(
                X509CertWrapper(
                    child!!
                )
            )
            addChildren(childNode, child, certs)
            treeNode.add(childNode)
        }
    }

    private fun createUIComponents() {
        // TODO: place custom component creation code here
    }

    fun hasCertSelected(): Boolean {
        return selectedCertificate != null
    }

    fun hasCerts(): Boolean {
        val ct = certTree ?: return false
        return ct.model.getChildCount(ct.model.root) > 0
    }

    companion object {
        private val log = Logger.getInstance(
            X509CertAssistant::class.java
        )
    }
}
