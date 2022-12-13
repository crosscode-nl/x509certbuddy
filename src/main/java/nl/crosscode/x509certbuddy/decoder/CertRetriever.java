package nl.crosscode.x509certbuddy.decoder;

import com.intellij.openapi.editor.Editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    public List<RetrievedCert> retrievedCerts(byte[] data) throws CertificateException {
        if (data==null||data.length<64) return List.of();
        if (data[0]==0x30) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                return CertificateFactory.getInstance("X.509").generateCertificates(bais).stream().map(c->new RetrievedCert(null,0,(X509Certificate)c)).collect(Collectors.toList());
            } catch (IOException | CertificateException e) {
            }
        }
        return retrieveCerts(new String(data));
    }

    public List<RetrievedCert> retrieveCerts(String text) throws CertificateException {

        List<RetrievedCert> certificates = new ArrayList<>();
        getCertsFromText(text, certificates);
        return certificates;
    }

    private void getCertsFromText(String text, List<RetrievedCert> certificates) {
        for (PotentialCert potentialCert : findPotentialCerts(text)) {
            try {
                for (X509Certificate cert : certFromBytes(potentialCert.getPotentialCert())) {
                    certificates.add(new RetrievedCert(editor, potentialCert.getOffset(), cert));
                }
            } catch (Exception e) {} // Ignoring it for now due to the brute force nature of cert finding.
        }
    }

    private List<X509Certificate> certFromBytes(byte[] bytes) throws CertificateException {
        InputStream in = new ByteArrayInputStream(bytes);
        return certFactory.generateCertificates(in).stream().map(c->(X509Certificate)c).collect(Collectors.toList());
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
        } catch (OutOfMemoryError | Exception e) {}
    }
}
