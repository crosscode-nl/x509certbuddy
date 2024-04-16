package nl.crosscode.x509certbuddy.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import nl.crosscode.x509certbuddy.X509CertAssistantFactory

class RemoveAllCertsAction : AnAction() {
    init {
        this.templatePresentation.icon = AllIcons.Actions.GC
        this.templatePresentation.text = "Remove All Certificates"
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (e.project == null) {
            e.presentation.isEnabled = false
            return
        }
        val assistant = X509CertAssistantFactory.getInstance(e.project!!)
        assistant?.removeAllCerts()
    }

    override fun update(e: AnActionEvent) {
        if (e.project == null) {
            e.presentation.isEnabled = false
            return
        }
        val assistant = X509CertAssistantFactory.getInstance(e.project!!)
        e.presentation.isEnabled = assistant?.hasCerts() ?: false
        super.update(e)
    }
}
