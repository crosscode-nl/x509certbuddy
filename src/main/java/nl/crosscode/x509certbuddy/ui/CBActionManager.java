package nl.crosscode.x509certbuddy.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import nl.crosscode.x509certbuddy.actions.*;

public class CBActionManager {


    static synchronized ActionGroup buildContextMenu(Exporters exporters) {

        DefaultActionGroup exportActions = new DefaultActionGroup();
        exportActions.setPopup(true);
        exportActions.getTemplatePresentation().setText("Export");
        exportActions.getTemplatePresentation().setIcon(AllIcons.Actions.Download);
        exportActions.add(new ExportPEMAction(exporters));
        exportActions.add(new ExportCertChainPEMAction(exporters));
        exportActions.add(new ExportDERAction(exporters));
        exportActions.add(new ExportAllAction(exporters));

        DefaultActionGroup copyActions = new DefaultActionGroup();
        copyActions.setPopup(true);
        copyActions.getTemplatePresentation().setText("Copy");
        copyActions.getTemplatePresentation().setIcon(AllIcons.Actions.Copy);
        copyActions.add(new CopyPEMAction(exporters));
        copyActions.add(new CopyCertChainPEMAction(exporters));
        copyActions.add(new CopyBase64Action(exporters));
        copyActions.add(new CopyAllAction(exporters));

        DefaultActionGroup treeActions = new DefaultActionGroup();
        treeActions.add(copyActions);
        treeActions.add(exportActions);
        treeActions.add(new RemoveSelectedCertAction());
        treeActions.add(new RemoveAllCertsAction());
        return treeActions;
    }
}
