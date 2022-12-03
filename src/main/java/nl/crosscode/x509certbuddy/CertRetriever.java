package nl.crosscode.x509certbuddy;

import com.intellij.openapi.editor.Editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CertRetriever {
    private final CertificateFactory certFactory;
    private final Editor editor;

    public CertRetriever(Editor editor) throws CertificateException {
        this.editor = editor;
        certFactory = CertificateFactory.getInstance("X.509");
    }

    public List<RetrievedCert> retrieveCerts(String text) throws CertificateException {
        List<RetrievedCert> certificates = new ArrayList<>();
        for (PotentialCert potentialCert : findPotentialCerts(text)) {
            try {
                certificates.add(new RetrievedCert(editor, potentialCert.getOffset(), certFromBytes(potentialCert.getPotentialCert())));
            } catch (Exception e) {} // Ignoring it for now due to the brute force nature of cert finding.
        }
        return certificates;
    }

    private X509Certificate certFromBytes(byte[] bytes) throws CertificateException {
        InputStream in = new ByteArrayInputStream(bytes);
        return (X509Certificate)certFactory.generateCertificate(in);
    }

    private List<PotentialCert> findPotentialCerts(String text) {
        List<PotentialCert> potentialCerts = new ArrayList<>();
        List<Decoder> decoders = new ArrayList<Decoder>();
        int offset = -1;
        for (char c : text.toCharArray()) {
            offset++;
            if (c=='M') {
                decoders.add(new Decoder(offset));
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

    private static void decodeToPotentialCerts(List<PotentialCert> potentialCerts, Decoder decoder) {
        SeqDerReader seqDerReader = new SeqDerReader();
        try {
            byte[] data = decoder.tryDecode();
            for (byte b : data) {
                seqDerReader.read(b);
            }
            seqDerReader.setEof();
            if (!seqDerReader.isError()) {
                potentialCerts.add(new PotentialCert(seqDerReader.getResult(),decoder.getOriginalOffset()));
            }
        } catch (OutOfMemoryError e) {}
        catch (Exception e) {}
    }
}
