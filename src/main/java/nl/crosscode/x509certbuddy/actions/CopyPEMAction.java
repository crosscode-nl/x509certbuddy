package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.crosscode.x509certbuddy.ui.Exporters;
import org.jetbrains.annotations.NotNull;

public class CopyPEMAction extends AnAction {

    private final Exporters exporters;
    public CopyPEMAction(Exporters exporters) {
        this.exporters = exporters;
        this.getTemplatePresentation().setText("Copy PEM");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        exporters.copyPem(null);
    }
}
