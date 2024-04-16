package nl.crosscode.x509certbuddy.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.DefaultActionGroup
import nl.crosscode.x509certbuddy.actions.*

object CBActionManager {
    @JvmStatic
    @Synchronized
    fun buildContextMenu(exporters: Exporters?): ActionGroup {
        val exportActions = DefaultActionGroup()
        exportActions.isPopup = true
        exportActions.templatePresentation.text = "Export"
        exportActions.templatePresentation.icon = AllIcons.Actions.Download
        exportActions.add(ExportPEMAction(exporters!!))
        exportActions.add(ExportCertChainPEMAction(exporters))
        exportActions.add(ExportDERAction(exporters))
        exportActions.add(ExportAllAction(exporters))

        val copyActions = DefaultActionGroup()
        copyActions.isPopup = true
        copyActions.templatePresentation.text = "Copy"
        copyActions.templatePresentation.icon = AllIcons.Actions.Copy
        copyActions.add(CopyPEMAction(exporters))
        copyActions.add(CopyCertChainPEMAction(exporters))
        copyActions.add(CopyBase64Action(exporters))
        copyActions.add(CopyAllAction(exporters))

        val treeActions = DefaultActionGroup()
        treeActions.add(copyActions)
        treeActions.add(exportActions)
        treeActions.add(RemoveSelectedCertAction())
        treeActions.add(RemoveAllCertsAction())
        return treeActions
    }
}
