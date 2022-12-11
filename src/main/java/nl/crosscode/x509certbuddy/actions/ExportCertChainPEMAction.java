package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.crosscode.x509certbuddy.ui.Exporters;
import org.jetbrains.annotations.NotNull;

public class ExportCertChainPEMAction extends AnAction {
    private final Exporters exporters;
    public ExportCertChainPEMAction(Exporters exporters) {
        this.exporters = exporters;
        this.getTemplatePresentation().setText("Export Cert Chain PEM");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        exporters.exportCertChainPEM(null);
    }
}
