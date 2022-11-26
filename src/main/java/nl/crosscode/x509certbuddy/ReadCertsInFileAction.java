package nl.crosscode.x509certbuddy;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.bouncycastle.mime.encoding.Base64InputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReadCertsInFileAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project==null) return;
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor==null) return;
        String allText = editor.getDocument().getText();
        var b64dec = new Base64InputStream(new ByteArrayInputStream(allText.getBytes(StandardCharsets.UTF_8)));
        try {
            int byteRead = b64dec.read();
            if (byteRead==0x30) {
                // create a new sequence decoder
            }
            if (byteRead==-1) {
                // TODO: Finish reading
                    return;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
