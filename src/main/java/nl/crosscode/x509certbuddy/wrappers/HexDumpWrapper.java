package nl.crosscode.x509certbuddy.wrappers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class HexDumpWrapper {

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

}
