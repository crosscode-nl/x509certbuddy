package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.crosscode.x509certbuddy.ui.Exporters;
import org.jetbrains.annotations.NotNull;

public class CopyAllAction extends AnAction {

    private final Exporters exporters;
    public CopyAllAction(Exporters exporters) {
        this.exporters = exporters;
        this.getTemplatePresentation().setText("Copy All");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        exporters.copyAll(null);
    }
}
