package nl.crosscode.x509certbuddy;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.bouncycastle.mime.encoding.Base64InputStream;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Rectangle2D;
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
        /*
        Inlay<EditorCustomElementRenderer> hello = editor.getInlayModel().addBlockElement(00, false, true, 0, new EditorCustomElementRenderer() {
            @Override
            public int calcWidthInPixels(@NotNull Inlay inlay) {
                return 200;
            }

            @Override
            public int calcHeightInPixels(@NotNull Inlay inlay) {
                return 200;
            }

            @Override
            public void paint(@NotNull Inlay inlay, @NotNull Graphics2D g, @NotNull Rectangle2D targetRegion, @NotNull TextAttributes textAttributes) {
                g.drawString("Hello", (int) targetRegion.getX(), (int) targetRegion.getMaxY());
                EditorCustomElementRenderer.super.paint(inlay, g, targetRegion, textAttributes);
            }
        });

        hello.dispose();
        */
        //  TextAttributes color = editor.getColorsScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
    //    RangeHighlighter rh = editor.getMarkupModel().addRangeHighlighter(10,20, HighlighterLayer.ADDITIONAL_SYNTAX+1,color,HighlighterTargetArea.EXACT_RANGE);
//        editor.getMarkupModel().removeHighlighter(rh);

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
