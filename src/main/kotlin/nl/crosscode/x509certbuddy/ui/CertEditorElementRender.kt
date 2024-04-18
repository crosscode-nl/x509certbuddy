package nl.crosscode.x509certbuddy.ui

import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.security.cert.X509Certificate

class CertEditorElementRender(private val certificate: X509Certificate) : EditorCustomElementRenderer {
    private var font: Font? = null


    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        return 600
    }

    override fun paint(inlay: Inlay<*>, g: Graphics2D, targetRegion: Rectangle2D, textAttributes: TextAttributes) {
        if (font == null) {
            font = g.font.deriveFont(g.font.style, (g.font.size - 1).toFloat())
        }
        val oldFont = g.font
        g.font = font
        val x = inlay.editor.offsetToPoint2D(inlay.offset).x.toInt()
        g.color = JBColor.GRAY
        g.drawString(
            certificate.subjectX500Principal.name + " (0x" + certificate.serialNumber.toString(16) + ")",
            targetRegion.x.toInt() + 1 + x,
            targetRegion.maxY.toInt() - 2
        )
        super.paint(inlay, g, targetRegion, textAttributes)
        g.font = oldFont
    }
}
