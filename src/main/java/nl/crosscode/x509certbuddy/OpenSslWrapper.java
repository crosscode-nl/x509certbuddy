package nl.crosscode.x509certbuddy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class OpenSslWrapper {
    public static String getCertDetails(X509Certificate cert) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("openssl","x509","-inform","DER","-noout","-text");
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            p.getOutputStream().write(cert.getEncoded());
            p.getOutputStream().flush();
            p.getOutputStream().close();
            return new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | CertificateEncodingException e) {
            return null;
        }
    }

    public static String getPem(X509Certificate cert) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("openssl","x509","-inform","DER");
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            p.getOutputStream().write(cert.getEncoded());
            p.getOutputStream().flush();
            p.getOutputStream().close();
            return new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | CertificateEncodingException e) {
            return null;
        }
    }

    public static String getAsn1(X509Certificate cert) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("openssl","asn1parse","-i","-inform","DER");
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            p.getOutputStream().write(cert.getEncoded());
            p.getOutputStream().flush();
            p.getOutputStream().close();
            return new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | CertificateEncodingException e) {
            return null;
        }
    }

    public static String getHex(X509Certificate cert) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("hexdump","-C");
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            p.getOutputStream().write(cert.getEncoded());
            p.getOutputStream().flush();
            p.getOutputStream().close();
            return new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | CertificateEncodingException e) {
            return null;
        }
    }

    public static String getBase64(X509Certificate cert) {
        try {
            return Base64.getEncoder().encodeToString(cert.getEncoded());
        } catch (CertificateEncodingException e) {
            return null;
        }
    }
}
