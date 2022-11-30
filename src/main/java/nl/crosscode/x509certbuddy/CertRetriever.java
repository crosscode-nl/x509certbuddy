package nl.crosscode.x509certbuddy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CertRetriever {
    CertificateFactory certFactory;

    public CertRetriever() throws CertificateException {
        certFactory = CertificateFactory.getInstance("X.509");
    }

    public List<X509Certificate> retrieveCerts(String text) throws CertificateException {
        List<X509Certificate> certificates = new ArrayList<X509Certificate>();
        for (byte[] potentialCert : findPotentialCerts(text)) {
            try {
                certificates.add(certFromBytes(potentialCert));
            } catch (Exception e) {} // Ignoring it for now due to the brute force nature of cert finding.
        }
        return certificates;
    }

    private X509Certificate certFromBytes(byte[] bytes) throws CertificateException {
        InputStream in = new ByteArrayInputStream(bytes);
        return (X509Certificate)certFactory.generateCertificate(in);
    }

    private List<byte[]> findPotentialCerts(String text) {
        List<byte[]> potentialCerts = new ArrayList<byte[]>();
        List<Decoder> decoders = new ArrayList<Decoder>();
        for (char c : text.toCharArray()) {
            if (c=='M') {
                decoders.add(new Decoder());
            }
            for (Decoder decoder : decoders) {
                if (decoder.add(c)) { // decoder is certainly done
                    decodeToPotentialCerts(potentialCerts, decoder);
                }
            }
            decoders = decoders.stream().filter(decoder -> !decoder.isDone()).collect(Collectors.toList());
        }
        for (Decoder decoder : decoders) {
            decodeToPotentialCerts(potentialCerts, decoder);
        }
        return potentialCerts;
    }

    private static void decodeToPotentialCerts(List<byte[]> potentialCerts, Decoder decoder) {
        SeqDerReader seqDerReader = new SeqDerReader();
        try {
            byte[] data = decoder.tryDecode();
            for (byte b : data) {
                seqDerReader.read(b);
            }
            seqDerReader.setEof();
            if (!seqDerReader.isError()) {
                potentialCerts.add(seqDerReader.getResult());
            }
        } catch (Exception e) {}
    }
}
