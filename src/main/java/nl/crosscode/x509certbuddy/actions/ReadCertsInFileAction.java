package nl.crosscode.x509certbuddy.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import nl.crosscode.x509certbuddy.decoder.CertRetriever;
import nl.crosscode.x509certbuddy.decoder.RetrievedCert;
import nl.crosscode.x509certbuddy.ui.CertEditorElementRender;
import nl.crosscode.x509certbuddy.utils.EditorUtilsFactory;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;
import org.jetbrains.annotations.NotNull;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadCertsInFileAction extends AnAction {
    private static final Logger log = Logger.getInstance(ReadCertsInFileAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        log.warn("Read certs in file action performed.");
        Project project = e.getProject();
        if (project==null) return;
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        EditorUtilsFactory.getInstance().readCertsFromEditor(editor);


    }


}
