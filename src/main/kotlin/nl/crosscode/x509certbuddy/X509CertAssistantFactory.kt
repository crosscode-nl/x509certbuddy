package nl.crosscode.x509certbuddy

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Factory
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import nl.crosscode.x509certbuddy.ui.x509CertAssistant
import nl.crosscode.x509certbuddy.utils.EditorUtilsFactory.instance

class X509CertAssistantFactory : ToolWindowFactory {
    private val autoDetectCerts = false

    @Synchronized
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val x509CertAssistant = getInstance(project) { x509CertAssistant(toolWindow) }
        val content = toolWindow.contentManager.factory.createContent(
            x509CertAssistant!!.rootPanel, "", false
        )
        toolWindow.contentManager.addContent(content)
        if (!autoDetectCerts) return
        for (editor in EditorFactory.getInstance().allEditors) {
            instance.readCertsFromEditor(editor)
        }
    }


    companion object {
        private val instances = HashMap<Project, x509CertAssistant?>()

        @Synchronized
        fun getInstance(project: Project): x509CertAssistant? {
            return instances[project]
        }

        @Synchronized
        private fun getInstance(project: Project, factory: Factory<x509CertAssistant>): x509CertAssistant? {
            var value = instances[project]
            if (value == null) {
                value = factory.create()
                instances[project] = value
            }
            return value
        }
    }
}
