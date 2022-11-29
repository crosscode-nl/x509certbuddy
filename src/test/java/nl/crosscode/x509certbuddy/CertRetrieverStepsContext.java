package nl.crosscode.x509certbuddy;

import java.util.List;

public class CertRetrieverStepsContext {
    private String text;
    private List<String> base64certs;

    public List<String> getBase64certs() {
        return base64certs;
    }

    public void setBase64certs(List<String> base64certs) {
        this.base64certs = base64certs;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
