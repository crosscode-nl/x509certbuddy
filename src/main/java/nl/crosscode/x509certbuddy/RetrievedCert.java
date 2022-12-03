package nl.crosscode.x509certbuddy;

import com.intellij.openapi.editor.Editor;

import java.security.cert.X509Certificate;

public class RetrievedCert {
    private Editor editor;
    private int offset;
    private X509Certificate certificate;

    public RetrievedCert(Editor editor, int offset, X509Certificate certificate) {
        this.editor = editor;
        this.offset = offset;
        this.certificate = certificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public int getOffset() {
        return offset;
    }

    public Editor getEditor() {
        return editor;
    }
}
