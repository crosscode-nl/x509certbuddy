package nl.crosscode.x509certbuddy.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.CloseOtherProjectsAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.PopupHandler;
import com.thaiopensource.xml.dtd.om.Def;
import nl.crosscode.x509certbuddy.actions.CopyPEMAction;
import nl.crosscode.x509certbuddy.actions.ExportPEMAction;
import nl.crosscode.x509certbuddy.actions.RemoveAllCertsAction;
import nl.crosscode.x509certbuddy.actions.RemoveSelectedCertAction;
import nl.crosscode.x509certbuddy.decoder.CertRetriever;
import nl.crosscode.x509certbuddy.decoder.RetrievedCert;
import nl.crosscode.x509certbuddy.wrappers.X509CertWrapper;
import nl.crosscode.x509certbuddy.wrappers.HexDumpWrapper;
import nl.crosscode.x509certbuddy.wrappers.OpenSslWrapper;
import nl.crosscode.x509certbuddy.x509CertAssistantFactory;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class x509CertAssistant {

    private static final Logger log = Logger.getInstance(x509CertAssistant.class);
    private String pemString;

    private Exporters exporters;
//    private String base64String;
    private final List<X509Certificate> x509Certificates = new ArrayList<>();
    private X509Certificate selectedCertificate = null;
    private JPanel rootPanel;
    private JTree certTree;
    private JTextPane certDetailsTextPane;
    private JTextPane pemTextPane;
    private JTextPane asn1TextPane;
    private JTextPane hexTextPane;
    private JTextPane validationTextPane;

    public x509CertAssistant(ToolWindow tw) {
        log.warn("Constructed x509CertAssistant");
        exporters = new Exporters(x509Certificates,rootPanel);
        tw.setTitleActions(List.of(CBActionManager.buildContextMenu(exporters)));
        PopupHandler.installPopupMenu(certTree,CBActionManager.buildContextMenu(exporters), "X509CertBuddy.CertTree.Actions");

        DefaultTreeModel model = (DefaultTreeModel) certTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Certs", true);
        model.setRoot(root);
        model.reload();
        certTree.addTreeSelectionListener(this::treeSelectionChanged);
        new DropTarget(rootPanel, new FileDropTargetListener(this::filesToProcess));
    }

    private void filesToProcess(List<File> files) {
        for (File file : files) {
            try {
                byte[] data = FileUtils.readFileToByteArray(file);
                CertRetriever certRetriever = new CertRetriever(null);
                addCerts(certRetriever.retrievedCerts(data).stream().map(RetrievedCert::getCertificate).collect(Collectors.toList()));
            } catch (IOException | CertificateException e) {
            }
        }
    }

    private void treeSelectionChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) certTree.getLastSelectedPathComponent();
        if (treeNode == null) {
            selectedCertificate = null;
            exporters.setSelectedCertificate(null);
            updateFieldsWithCertDetails();
            return;
        }
        selectedCertificate = ((X509CertWrapper) treeNode.getUserObject()).getCert();
        exporters.setSelectedCertificate(selectedCertificate);
        updateFieldsWithCertDetails();
    }

    private void updateFieldsWithCertDetails() {
        certDetailsTextPane.setText(null);
        pemTextPane.setText(null);
        asn1TextPane.setText(null);
        hexTextPane.setText(null);
        validationTextPane.setText(null);
        if (selectedCertificate == null) return;
        String details = OpenSslWrapper.getCertDetails(selectedCertificate);
        if (details == null) {
            details = selectedCertificate.toString();
        }
        certDetailsTextPane.setText(details);
        certDetailsTextPane.setCaretPosition(0);
        pemString = OpenSslWrapper.getPem(selectedCertificate);
        pemTextPane.setText(pemString);
        asn1TextPane.setText(OpenSslWrapper.getAsn1(selectedCertificate));
        hexTextPane.setText(HexDumpWrapper.getHex(selectedCertificate));
        validationTextPane.setText(OpenSslWrapper.getValidation(selectedCertificate, x509Certificates));
    }


    public void removeCert(ActionEvent e) {
        x509Certificates.remove(selectedCertificate);
        buildTree();
    }

    public void removeAllCerts(ActionEvent e) {
        x509Certificates.clear();
        buildTree();
    }



    public JPanel getContent() {
        log.warn("getContent is called");
        return rootPanel;
    }

    public void addCerts(List<X509Certificate> certs) {
        boolean newCerts = false;
        for (X509Certificate cert : certs) {
            if (x509Certificates.stream().noneMatch(x-> {
                try {
                    return Arrays.equals(x.getEncoded(),cert.getEncoded());
                } catch (CertificateEncodingException e) {
                    throw new RuntimeException(e);
                }
            })) {
                newCerts = true;
                break;
            }
        }

        if (!newCerts) return;
        x509Certificates.addAll(certs);
        removeDuplicateCerts();
        buildTree();
    }

    private void removeDuplicateCerts() {
        List<X509Certificate> certs = new ArrayList<>();
        for (X509Certificate cert : x509Certificates) {
            if (certs.stream().anyMatch(x -> {
                try {
                    return Arrays.equals(x.getEncoded(), cert.getEncoded());
                } catch (CertificateEncodingException e) {
                    return false;
                }
            })) continue;
            certs.add(cert);
        }
        x509Certificates.clear();
        x509Certificates.addAll(certs);
    }

    private void buildTree() {
        selectedCertificate = null;
        DefaultTreeModel model = (DefaultTreeModel) certTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Certs", true);
        root.removeAllChildren();
        List<X509Certificate> rootCerts = x509Certificates.stream().filter(x -> !x509Certificates.stream().anyMatch(y -> y.getSubjectDN().getName().equals(x.getIssuerDN().getName()) && x != y)).collect(Collectors.toList());
        for (X509Certificate cert : rootCerts) {
            log.warn(cert.getSubjectDN().getName());
            log.warn(cert.getIssuerDN().getName());
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new X509CertWrapper(cert));
            addChildren(treeNode, cert, x509Certificates);
            root.add(treeNode);
        }
        model.setRoot(root);
        model.reload();
        expandAllNodes(certTree, 0, 0);
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public void addChildren(DefaultMutableTreeNode treeNode, X509Certificate cert, List<X509Certificate> certs) {
        List<X509Certificate> children = certs.stream().filter(x -> x.getIssuerDN().getName().equals(cert.getSubjectDN().getName()) && x != cert).collect(Collectors.toList());
        for (X509Certificate child : children) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new X509CertWrapper(child));
            addChildren(childNode, child, certs);
            treeNode.add(childNode);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public boolean hasCertSelected() {
        return selectedCertificate!=null;
    }

    public boolean hasCerts() {
        return certTree.getModel().getChildCount(certTree.getModel().getRoot())>0;
    }
}
