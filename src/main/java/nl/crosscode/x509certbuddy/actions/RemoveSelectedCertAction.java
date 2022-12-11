package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;
import org.jetbrains.annotations.NotNull;

public class RemoveSelectedCertAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        x509CertAssistantFactory.getX509CertAssistant(e.getProject()).removeCert(null);
    }
}
