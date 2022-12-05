package nl.crosscode.x509certbuddy.wrappers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static String getValidation(X509Certificate cert, List<X509Certificate> certificateList) {
        try {
            List<X509Certificate> trusted = new ArrayList<>();
            List<X509Certificate> untrusted = new ArrayList<>();
            X509Certificate certToFindParentOf = cert;
            while (certToFindParentOf != null) {
                X509Certificate finalCertToFindParentOf = certToFindParentOf;
                Optional<X509Certificate> foundParent = certificateList.stream().filter(x -> x.getSubjectDN().getName().equals(finalCertToFindParentOf.getIssuerDN().getName()) && x!=finalCertToFindParentOf).findFirst();
                if (foundParent.isPresent()) {
                    if (foundParent.get().getSubjectDN().getName().equals(foundParent.get().getIssuerDN().getName())) {
                        trusted.add(foundParent.get());
                    } else {
                        untrusted.add(foundParent.get());
                    }
                    certToFindParentOf = foundParent.get();
                } else {
                    certToFindParentOf = null;
                }
            }
            String certSerial = cert.getSerialNumber().toString(16);
            File tmpdir = Files.createTempDirectory("").toFile();
            try {
                writeAllCerts(certificateList, tmpdir); // TODO: Now just write all certs, but in the future we should narrow this down to the ones we actually need.
                ProcessBuilder pb = new ProcessBuilder();
                List<String> command = new ArrayList<>();
                command.add("openssl");
                command.add("verify");
                for (X509Certificate trustedCert : trusted) {
                    command.add("-trusted");
                    command.add(new File(tmpdir, trustedCert.getSerialNumber().toString(16)+".pem").getAbsolutePath());
                }
                for (X509Certificate untrustedCert : untrusted) {
                    command.add("-untrusted");
                    command.add(new File(tmpdir, untrustedCert.getSerialNumber().toString(16)+".pem").getAbsolutePath());
                }
                command.add("-verbose");
                command.add(new File(tmpdir, cert.getSerialNumber().toString(16)+".pem").getAbsolutePath());
                pb.command(command);
                try {
                    Process p = pb.start();
                    return new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    return null;
                }
            } finally {
                FileUtils.deleteDirectory(tmpdir);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static void writeAllCerts(List<X509Certificate> certsToWrite, File directory) throws IOException {
        for (X509Certificate cert : certsToWrite) {
            String pem = getPem(cert);
            File f = new File(directory,cert.getSerialNumber().toString(16)+".pem");
            FileUtils.writeStringToFile(f,pem, StandardCharsets.UTF_8);
        }
    }


}
