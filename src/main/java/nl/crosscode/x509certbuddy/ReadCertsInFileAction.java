package nl.crosscode.x509certbuddy;

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
import org.jetbrains.annotations.NotNull;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadCertsInFileAction extends AnAction {
    private static final Logger log = Logger.getInstance(ReadCertsInFileAction.class);
    private final List<Inlay> inlays = new ArrayList<>();
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        log.warn("Read certs in file action performed.");
        Project project = e.getProject();
        if (project==null) return;
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor==null) return;

        String allText = editor.getDocument().getText();
        try {
            CertRetriever certRetriever = new CertRetriever(editor);
            List<RetrievedCert> certs = certRetriever.retrieveCerts(allText);
            ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("X.509 Cert Buddy");
            if (tw!=null) {
                x509CertAssistantFactory.getX509CertAssistant().addCerts(certs.stream().map(x->x.getCertificate()).collect(Collectors.toList()));
                tw.show();
            }
            addCertsToEditor(certs);
        } catch (CertificateException ex) {}
    }

    private void addCertsToEditor(List<RetrievedCert> retrievedCerts) {
        inlays.stream().forEach(x->x.dispose());
        inlays.clear();
        for (RetrievedCert retrievedCert : retrievedCerts) {
            Editor editor = retrievedCert.getEditor();
            if (editor==null) continue;

            inlays.add(editor.getInlayModel().addBlockElement(retrievedCert.getOffset(),false,true,0,new CertEditorElementRender(retrievedCert.getCertificate())));
        }

    }
}
