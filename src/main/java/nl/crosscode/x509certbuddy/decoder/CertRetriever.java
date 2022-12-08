package nl.crosscode.x509certbuddy.decoder;

import com.intellij.openapi.editor.Editor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
                return List.of(new RetrievedCert(null,0,cert));
            } catch (IOException | CertificateException e) {
            }
        }
        return retrieveCerts(new String(data));
    }

    public List<RetrievedCert> retrieveCerts(String text) throws CertificateException {

        List<RetrievedCert> certificates = new ArrayList<>();
     //   if (getCertsFromYaml(text, certificates)) return certificates;
        getCertsFromText(text, certificates,0);
        return certificates;
    }
/*
    private boolean getCertsFromYaml(String text, List<RetrievedCert> certificates) {
        try {
            Yaml yaml = new Yaml();
            Map<Object, Object> doc = yaml.load(text);
            if (doc == null) {
                return false;
            }
            List<String> results = new ArrayList<>();
            parseYaml(doc, results);
            for (String result : results) {
                getCertsFromText(result, certificates,0);
            }
            return true;
        } catch (ParserException e) {
            return false;
        }
    }

    private void parseYaml(Map<Object,Object> doc, List<String> results) {
        for (Object key : doc.keySet()) {
            Object value = doc.get(key);
            if (value instanceof String) {
                results.add((String)value);
            }
            if (value instanceof Map) {
                parseYaml((Map<Object, Object>) value,results);
            }
        }
    }
*/
    private void getCertsFromText(String text, List<RetrievedCert> certificates, int baseOffset) {
        for (PotentialCert potentialCert : findPotentialCerts(text)) {
            try {
                certificates.add(new RetrievedCert(editor, potentialCert.getOffset()+baseOffset, certFromBytes(potentialCert.getPotentialCert())));
            } catch (Exception e) {} // Ignoring it for now due to the brute force nature of cert finding.
        }
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
