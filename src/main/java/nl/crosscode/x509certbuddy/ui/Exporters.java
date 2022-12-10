package nl.crosscode.x509certbuddy.ui;


import nl.crosscode.x509certbuddy.utils.X509Utils;
import nl.crosscode.x509certbuddy.wrappers.OpenSslWrapper;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

public class Exporters {
    private final List<X509Certificate> certificateList;
    private X509Certificate selectedCert;

    public Exporters(List<X509Certificate> certificateList) {
        this.certificateList = certificateList;
    }

    public void setSelectedCertificate(X509Certificate selected) {
        this.selectedCert = selected;
    }

    public void copyPem(ActionEvent e) {
        if (selectedCert==null) return;
        String pem = OpenSslWrapper.getPem(selectedCert);
        if (pem==null||pem.isEmpty()) return;
        CopyToClipboard(pem);
    }

    public void exportPEM(ActionEvent actionEvent) {
        if (selectedCert==null) return;
        String pem = OpenSslWrapper.getPem(selectedCert);
        if (pem==null||pem.isEmpty()) return;
        SaveToFile(pem.getBytes(StandardCharsets.UTF_8),selectedCert.getSerialNumber().toString(16)+".pem");
    }

    public void copyBase64(ActionEvent e) {
        if (selectedCert==null) return;
        CopyToClipboard(X509Utils.getBase64(selectedCert));
    }

    public void exportDER(ActionEvent actionEvent) {
        if (selectedCert==null) return;
        try {
            SaveToFile(selectedCert.getEncoded(),selectedCert.getSerialNumber().toString(16)+".der");
        } catch (CertificateEncodingException e) {
        }
    }

    private X509Certificate getParent(X509Certificate certificate) {
        if (certificate.getSubjectDN().getName().equals(certificate.getIssuerDN().getName())) return null;
        return certificateList.stream().filter(x->x.getSubjectDN().getName().equals(certificate.getIssuerDN().getName())).findFirst().orElse(null);
    }

    public void copyCertChainPEM(ActionEvent actionEvent) {
        if (selectedCert==null) return;
        StringBuilder builder = new StringBuilder();
        String pem = OpenSslWrapper.getPem(selectedCert);
        if (pem==null||pem.isEmpty()) return; // this is to check openssl is working...
        builder.append(pem);
        X509Certificate currentCert = getParent(selectedCert);
        while (currentCert!=null) {
            builder.append(OpenSslWrapper.getPem(currentCert));
            currentCert = getParent(currentCert);
        }
        CopyToClipboard(builder.toString());
    }

    public void exportCertChainPEMButton(ActionEvent actionEvent) {
    }

    private void CopyToClipboard(String data) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(data);
        clipboard.setContents(ss, ss);
    }

    private void SaveToFile(byte[] data, String fileNameHint) {

    }

}
