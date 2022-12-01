package nl.crosscode.x509certbuddy;

import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class x509CertAssistant {

    private static final Logger log = Logger.getInstance(x509CertAssistant.class);

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
                X509Certificate cert = (X509Certificate) treeNode.getUserObject();
                if (cert==null) return;
                certDetails.setText(cert.toString());
            }
        });
    }


    private List<X509Certificate> x509Certificates = new ArrayList<>();

    private JPanel rootPanel;
    private JTree certTree;
    private JTextPane certDetails;

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
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(cert);
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
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            addChildren(childNode,child,certs);
            treeNode.add(childNode);
        }
    }
}
