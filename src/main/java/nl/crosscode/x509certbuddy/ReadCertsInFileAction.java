package nl.crosscode.x509certbuddy;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.bouncycastle.mime.encoding.Base64InputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class ReadCertsInFileAction extends AnAction {
    private static final Logger log = Logger.getInstance(ReadCertsInFileAction.class);
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        log.warn("Read certs in file action performed.");
        Project project = e.getProject();
        if (project==null) return;
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor==null) return;
        String allText = editor.getDocument().getText();
        try {
            CertRetriever certRetriever = new CertRetriever();
            List<X509Certificate> certs = certRetriever.retrieveCerts(allText);
            ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("X.509 Cert Buddy");
            if (tw!=null) {
                x509CertAssistantFactory.getX509CertAssistant().addCerts(certs);
                tw.show();
            }
        } catch (CertificateException ex) {}
    }
}
