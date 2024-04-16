package nl.crosscode.x509certbuddy.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import nl.crosscode.x509certbuddy.ui.Exporters
import nl.crosscode.x509certbuddy.X509CertAssistantFactory

class CopyAllAction(private val exporters: Exporters) : AnAction() {
    init {
        this.templatePresentation.text = "Copy All"
    }

    override fun actionPerformed(e: AnActionEvent) {
        exporters.copyAll()
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
