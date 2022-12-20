package nl.crosscode.x509certbuddy.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import nl.crosscode.x509certbuddy.decoder.CertRetriever;
import nl.crosscode.x509certbuddy.decoder.RetrievedCert;
import nl.crosscode.x509certbuddy.ui.CertEditorElementRender;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;
import org.jetbrains.annotations.NotNull;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class EditorUtils {

    private static Dictionary<Editor,List<Inlay>> inlays = new Hashtable<>();
    public void readCertsFromEditor(Editor editor) {
        if (editor==null) return;
        Project project = editor.getProject();
        String allText = editor.getDocument().getText();
        try {
            CertRetriever certRetriever = new CertRetriever(editor);
            List<RetrievedCert> certs = certRetriever.retrieveCerts(allText);
            if (project!=null) {
                ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("X.509 Cert Buddy");
                if (tw != null) {
                    x509CertAssistantFactory.getInstance(project).addCerts(certs.stream().map(RetrievedCert::getCertificate).collect(Collectors.toList()));
                    tw.show();
                }
            }
            addCertsToEditor(certs, editor);
        } catch (CertificateException ex) {}
    }

    public synchronized void removeEditor(Editor editor) {
        List<Inlay> inlayList = inlays.get(editor);
        if (inlayList!=null) {
            inlayList.stream().forEach(x->x.dispose());
        }
        inlays.remove(editor);
    }

    private synchronized void addCertsToEditor(List<RetrievedCert> retrievedCerts, Editor editor) {
        removeEditor(editor);
        List<Inlay> inlayList = new ArrayList<>();
        inlays.put(editor,inlayList);
        for (RetrievedCert retrievedCert : retrievedCerts) {
            inlayList.add(editor.getInlayModel().addBlockElement(retrievedCert.getOffset(),false,true,0,new CertEditorElementRender(retrievedCert.getCertificate())));
        }
    }
}
