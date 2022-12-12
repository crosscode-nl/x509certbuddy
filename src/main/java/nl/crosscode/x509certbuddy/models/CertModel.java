package nl.crosscode.x509certbuddy.models;

import java.util.Base64;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertModel {
    private final String der;
    private final String serial;
    private final String subject;
    private final String issuer;
    private final Date notBefore;
    private final Date notAfter;
    public CertModel(X509Certificate cert) throws CertificateEncodingException {
        der = Base64.getEncoder().encodeToString(cert.getEncoded());
        serial = cert.getSerialNumber().toString(16);
        subject = cert.getSubjectDN().getName();
        issuer = cert.getIssuerDN().getName();
        notBefore = cert.getNotBefore();
        notAfter = cert.getNotAfter();
    }

    public String getDer() {
        return der;
    }

    public String getSerial() {
        return serial;
    }

    public String getSubject() {
        return subject;
    }

    public String getIssuer() {
        return issuer;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

}
