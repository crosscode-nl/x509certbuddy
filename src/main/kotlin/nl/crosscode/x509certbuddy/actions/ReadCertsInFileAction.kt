package nl.crosscode.x509certbuddy.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.wm.ToolWindowManager
import nl.crosscode.x509certbuddy.utils.EditorUtilsFactory.instance as editorUtils

class ReadCertsInFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        log.info("Read certs in file action performed.")
        val project = e.project ?: return
        val tw = ToolWindowManager.getInstance(project).getToolWindow("X.509 Cert Buddy")
        tw?.show()
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        editorUtils.readCertsFromEditor(editor)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    companion object {
        private val log = Logger.getInstance(
            ReadCertsInFileAction::class.java
        )
    }
}
