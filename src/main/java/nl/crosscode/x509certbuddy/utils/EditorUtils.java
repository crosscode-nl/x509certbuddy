package nl.crosscode.x509certbuddy.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import nl.crosscode.x509certbuddy.decoder.CertRetriever;
import nl.crosscode.x509certbuddy.decoder.RetrievedCert;
import nl.crosscode.x509certbuddy.ui.CertEditorElementRender;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

public class EditorUtils {

    private static Dictionary<Editor,List<Inlay>> inlays = new Hashtable<>();
    private static Dictionary<Editor,List<X509Certificate>> certs = new Hashtable<>();
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
        certs.remove(editor);
        List<Inlay> inlayList = inlays.get(editor);
        if (inlayList!=null) {
            inlayList.stream().forEach(x->x.dispose());
        }
        inlays.remove(editor);
    }

    private synchronized void addCertsToEditor(List<RetrievedCert> retrievedCerts, Editor editor) {
        List<X509Certificate> certsOfEditor = certs.get(editor);
        boolean certsUpdate = true;
        if (certsOfEditor!=null&&certsOfEditor.size()==retrievedCerts.size()) {
            certsUpdate = false;
            for (RetrievedCert cert : retrievedCerts) {
                if (certsOfEditor.stream().noneMatch(x-> {
                    try {
                        return Arrays.equals(x.getEncoded(),cert.getCertificate().getEncoded());
                    } catch (CertificateEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })) {
                    certsUpdate = true;
                    break;
                }
            }
        }
        if (!certsUpdate) return;
        removeEditor(editor);
        List<Inlay> inlayList = new ArrayList<>();
        inlays.put(editor,inlayList);
        certsOfEditor = new ArrayList<>();
        certs.put(editor,certsOfEditor);
        for (RetrievedCert retrievedCert : retrievedCerts) {
            certsOfEditor.add(retrievedCert.getCertificate());
            inlayList.add(editor.getInlayModel().addBlockElement(retrievedCert.getOffset(),false,true,0,new CertEditorElementRender(retrievedCert.getCertificate())));
        }
        editor.getComponent().repaint();
    }
}
