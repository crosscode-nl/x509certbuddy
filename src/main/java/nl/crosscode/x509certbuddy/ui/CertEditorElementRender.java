package nl.crosscode.x509certbuddy.ui;

import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.security.cert.X509Certificate;

public class CertEditorElementRender implements EditorCustomElementRenderer {
    private final X509Certificate certificate;
    private Font font;


    public CertEditorElementRender(X509Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return 600;
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
        if (font==null) {
            font = g.getFont().deriveFont(g.getFont().getStyle(), g.getFont().getSize()-1);
        }
        Font oldFont = g.getFont();
        g.setFont(font);
        int x = (int)inlay.getEditor().offsetToPoint2D(inlay.getOffset()).getX();
        g.setColor(Color.gray);
         g.drawString(certificate.getSubjectDN().getName() + " (0x"+certificate.getSerialNumber().toString(16)+")", (int) targetRegion.getX()+1+x, (int) targetRegion.getMaxY()-2);
        EditorCustomElementRenderer.super.paint(inlay, g, targetRegion, textAttributes);
        g.setFont(oldFont);
    }

}
