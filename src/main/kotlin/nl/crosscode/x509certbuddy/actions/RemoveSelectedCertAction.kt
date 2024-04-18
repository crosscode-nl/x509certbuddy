package nl.crosscode.x509certbuddy.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.application
import nl.crosscode.x509certbuddy.X509CertAssistantFactory

class RemoveSelectedCertAction : AnAction() {
    init {
        this.templatePresentation.icon = AllIcons.Actions.Cancel
        this.templatePresentation.text = "Remove Selected Certificate"
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (e.project == null) {
            e.presentation.isEnabled = false
            return
        }
        val assistant = X509CertAssistantFactory.getInstance(e.project!!)
        assistant?.removeCert()
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
        return ActionUpdateThread.EDT
    }
}
