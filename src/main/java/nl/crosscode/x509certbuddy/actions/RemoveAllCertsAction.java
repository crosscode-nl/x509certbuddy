package nl.crosscode.x509certbuddy.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.crosscode.x509certbuddy.ui.x509CertAssistant;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;
import org.jetbrains.annotations.NotNull;

public class RemoveAllCertsAction extends AnAction {

    public RemoveAllCertsAction() {
        this.getTemplatePresentation().setIcon(AllIcons.Actions.GC);
        this.getTemplatePresentation().setText("Remove All Certificates");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        x509CertAssistantFactory.getX509CertAssistant(e.getProject()).removeAllCerts(null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        x509CertAssistant assistant = x509CertAssistantFactory.getX509CertAssistant(e.getProject());
        e.getPresentation().setEnabled(assistant!=null?assistant.hasCerts():false);
        super.update(e);
    }
}
