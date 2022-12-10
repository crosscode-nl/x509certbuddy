package nl.crosscode.x509certbuddy.ui;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import nl.crosscode.x509certbuddy.models.CertModel;
import nl.crosscode.x509certbuddy.utils.X509Utils;
import nl.crosscode.x509certbuddy.wrappers.OpenSslWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Exporters {
    private final List<X509Certificate> certificateList;
    private X509Certificate selectedCert;

    private final JComponent parent;

    public Exporters(List<X509Certificate> certificateList, JComponent parent) {
        this.certificateList = certificateList;
        this.parent = parent;
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
        getCertChain().ifPresent(this::CopyToClipboard);

    }

    public void exportCertChainPEMButton(ActionEvent actionEvent) {
        getCertChain().ifPresent(
               certChain -> SaveToFile(certChain.getBytes(),selectedCert.getSerialNumber().toString(16)+"_chain.pem")
        );

    }

    public void copyAll(ActionEvent actionEvent) {
        CopyToClipboard(exportAll());
    }

    public void exportAll(ActionEvent actionEvent) {
        SaveToFile(exportAll().getBytes(),"all.json");
    }

    private Optional<String> getCertChain() {
        if (selectedCert==null) return Optional.empty();
        StringBuilder builder = new StringBuilder();
        String pem = OpenSslWrapper.getPem(selectedCert);
        if (pem==null||pem.isEmpty()) return Optional.empty(); // this is to check openssl is working...
        builder.append(pem);
        X509Certificate currentCert = getParent(selectedCert);
        while (currentCert!=null) {
            builder.append(OpenSslWrapper.getPem(currentCert));
            currentCert = getParent(currentCert);
        }
        return Optional.of(builder.toString());
    }

    private String exportAll() {
        var listOfCertModels = certificateList.stream().map(c-> {
            try {
                return new CertModel(c);
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        var gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        return gson.toJson(listOfCertModels);
    }

    private void CopyToClipboard(String data) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(data);
        clipboard.setContents(ss, ss);
    }

    private void SaveToFile(byte[] data, String fileNameHint) {
        FileSaverDescriptor fsd = new FileSaverDescriptor("Save file as","");
        FileSaverDialog dlg = FileChooserFactory.getInstance().createSaveFileDialog(fsd, parent);
        VirtualFileWrapper wrapper = dlg.save(fileNameHint);
        if (wrapper==null) return;
        VirtualFile vf = wrapper.getVirtualFile(true);
        if (vf==null)  return;
        try {
            vf.setBinaryContent(data);
        } catch (IOException e) { }
    }

}