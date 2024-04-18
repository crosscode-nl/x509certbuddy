package nl.crosscode.x509certbuddy.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import nl.crosscode.x509certbuddy.X509CertAssistantFactory
import nl.crosscode.x509certbuddy.ui.Exporters

class ExportCertChainPEMAction(private val exporters: Exporters) : AnAction() {
    init {
        this.templatePresentation.text = "Export Cert Chain PEM"
    }

    override fun actionPerformed(e: AnActionEvent) {
        exporters.exportCertChainPEM()
    }

    override fun update(e: AnActionEvent) {
        if (e.project == null) {
            e.presentation.isEnabled = false
            return
        }
        val assistant = X509CertAssistantFactory.getInstance(e.project!!)
        e.presentation.isEnabled = assistant?.hasCertSelected() ?: false
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
