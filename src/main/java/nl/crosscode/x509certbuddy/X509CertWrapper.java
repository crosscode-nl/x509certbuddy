package nl.crosscode.x509certbuddy;

import java.security.cert.X509Certificate;

public class X509CertWrapper {
    private final X509Certificate cert;

    public X509CertWrapper(X509Certificate cert) {
        this.cert = cert;
    }

    public X509Certificate getCert() {
        return cert;
    }

    public String toString() {
        return cert.getSubjectDN().getName() + " (0x" + cert.getSerialNumber().toString(16)+")";
    }

}
