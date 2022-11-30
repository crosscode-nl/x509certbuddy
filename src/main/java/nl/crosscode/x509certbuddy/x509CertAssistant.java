package nl.crosscode.x509certbuddy;

import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class x509CertAssistant {

    private static final Logger log = Logger.getInstance(x509CertAssistant.class);

    public x509CertAssistant() {
    }


    private List<X509Certificate> x509Certificates = new ArrayList<>();

    private JPanel rootPanel;
    private JTree certTree;

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
        for (X509Certificate cert : certs) {
            root.add(new DefaultMutableTreeNode(cert.getSubjectDN().getName()));
        }
        model.setRoot(root);
        model.reload();
    }
}
