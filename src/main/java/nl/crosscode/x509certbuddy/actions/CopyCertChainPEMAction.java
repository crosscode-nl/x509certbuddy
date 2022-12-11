package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.crosscode.x509certbuddy.ui.Exporters;
import nl.crosscode.x509certbuddy.ui.x509CertAssistant;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;
import org.jetbrains.annotations.NotNull;

public class CopyCertChainPEMAction extends AnAction {
    private final Exporters exporters;
    public CopyCertChainPEMAction(Exporters exporters) {
        this.exporters = exporters;
        this.getTemplatePresentation().setText("Copy Cert Chain PEM");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        exporters.copyCertChainPEM(null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        x509CertAssistant assistant = x509CertAssistantFactory.getX509CertAssistant(e.getProject());
        e.getPresentation().setEnabled(assistant!=null?assistant.hasCertSelected():false);
        super.update(e);
    }
}
