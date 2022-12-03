package nl.crosscode.x509certbuddy;

import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.security.cert.X509Certificate;

public class CertEditorElementRender implements EditorCustomElementRenderer {
    private final X509Certificate certificate;


    public CertEditorElementRender(X509Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return 200;
    }
/*
    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return 20;
    }
*/
    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics2D g, @NotNull Rectangle2D targetRegion, @NotNull TextAttributes textAttributes) {
     //   g.get
        g.setColor(Color.gray);
        g.drawString(certificate.getSubjectDN().getName(), (int) targetRegion.getX()+10, (int) targetRegion.getMaxY()-2);
        EditorCustomElementRenderer.super.paint(inlay, g, targetRegion, textAttributes);
    }

}
