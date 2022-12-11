package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.crosscode.x509certbuddy.ui.Exporters;
import org.jetbrains.annotations.NotNull;

public class CopyBase64Action extends AnAction {
    private final Exporters exporters;
    public CopyBase64Action(Exporters exporters) {
        this.exporters = exporters;
        this.getTemplatePresentation().setText("Copy Base64");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        exporters.copyBase64(null);
    }
}
