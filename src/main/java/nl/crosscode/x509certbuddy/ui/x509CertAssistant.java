package nl.crosscode.x509certbuddy.ui;

import com.intellij.openapi.diagnostic.Logger;
import nl.crosscode.x509certbuddy.wrappers.X509CertWrapper;
import nl.crosscode.x509certbuddy.utils.X509Utils;
import nl.crosscode.x509certbuddy.wrappers.HexDumpWrapper;
import nl.crosscode.x509certbuddy.wrappers.OpenSslWrapper;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class x509CertAssistant {

    private static final Logger log = Logger.getInstance(x509CertAssistant.class);
    private String pemString;
    private String base64String;

    public x509CertAssistant() {
        DefaultTreeModel model = (DefaultTreeModel)certTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Certs",true);
        model.setRoot(root);
        model.reload();
        certTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                certDetails.setText("");
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)certTree.getLastSelectedPathComponent();
                if (treeNode==null) return;
                X509Certificate cert = ((X509CertWrapper) treeNode.getUserObject()).getCert();
                if (cert==null) return;
                String details = OpenSslWrapper.getCertDetails(cert);
                if (details==null) {
                    details = cert.toString();
                }
                certDetails.setText(details);
                certDetails.setCaretPosition(0);
                pemString = OpenSslWrapper.getPem(cert);
                pem.setText(pemString);
                base64String = X509Utils.getBase64(cert);
                asn1.setText(OpenSslWrapper.getAsn1(cert));
                hex.setText(HexDumpWrapper.getHex(cert));
                validation.setText(OpenSslWrapper.getValidation(cert,x509Certificates));
            }
        });

        copyPem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pemString==null) return;
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection ss = new StringSelection(pemString);
                clipboard.setContents(ss,ss);
            }
        });

        copyBase64.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (base64String==null) return;
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection ss = new StringSelection(base64String);
                clipboard.setContents(ss,ss);
            }
        });


    }


    private List<X509Certificate> x509Certificates = new ArrayList<>();

    private JPanel rootPanel;
    private JTree certTree;
    private JTabbedPane tabbedPane1;
    private JTextPane certDetails;
    private JTextPane pem;
    private JButton copyPem;
    private JButton copyBase64;
    private JTextPane asn1;
    private JTextPane hex;
    private JTextPane validation;

    public JPanel getContent() {
        log.warn("getContent is called");
        return rootPanel;
    }


    public void addCerts(List<X509Certificate> certs) {
        log.warn("Adding certs");
        x509Certificates.addAll(certs);
        DefaultTreeModel model = (DefaultTreeModel)certTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Certs",true);
        root.removeAllChildren();
        List<X509Certificate> rootCerts = certs.stream().filter(x-> !certs.stream().anyMatch(y->y.getSubjectDN().getName().equals(x.getIssuerDN().getName())&&x!=y)).collect(Collectors.toList());
        for (X509Certificate cert : rootCerts) {
            log.warn(cert.getSubjectDN().getName());
            log.warn(cert.getIssuerDN().getName());
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new X509CertWrapper(cert));
        //    treeNode.setUserObject(cert);
            addChildren(treeNode,cert,certs);
            root.add(treeNode);
        }
        model.setRoot(root);
        model.reload();
        expandAllNodes(certTree,0,0);
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public void addChildren(DefaultMutableTreeNode treeNode, X509Certificate cert, List<X509Certificate> certs) {
        List<X509Certificate> children = certs.stream().filter(x->x.getIssuerDN().getName().equals(cert.getSubjectDN().getName())&&x!=cert).collect(Collectors.toList());
        for (X509Certificate child : children) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new X509CertWrapper(child));
            addChildren(childNode,child,certs);
            treeNode.add(childNode);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
