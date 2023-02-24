package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import nl.crosscode.x509certbuddy.decoder.RetrievedCert;
import nl.crosscode.x509certbuddy.utils.EditorUtilsFactory;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class ReadCertsInFileAction extends AnAction {
    private static final Logger log = Logger.getInstance(ReadCertsInFileAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        log.info("Read certs in file action performed.");
        Project project = e.getProject();
        if (project==null) return;
        ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("X.509 Cert Buddy");
        if (tw != null) {
            tw.show();
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        EditorUtilsFactory.getInstance().readCertsFromEditor(editor);
    }


}
