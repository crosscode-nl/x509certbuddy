package nl.crosscode.x509certbuddy.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.PopupHandler;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import nl.crosscode.x509certbuddy.decoder.CertRetriever;
import nl.crosscode.x509certbuddy.wrappers.X509CertWrapper;
import nl.crosscode.x509certbuddy.wrappers.HexDumpWrapper;
import nl.crosscode.x509certbuddy.wrappers.OpenSslWrapper;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
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
import java.util.Locale;
import java.util.stream.Collectors;

public class X509CertAssistant {

    private static final Logger log = Logger.getInstance(X509CertAssistant.class);

    private final Exporters exporters;
    private final List<X509Certificate> x509Certificates = new ArrayList<>();
    private X509Certificate selectedCertificate = null;
    private JPanel rootPanel;
    private JTree certTree;
    private JTextPane certDetailsTextPane;
    private JTextPane pemTextPane;
    private JTextPane asn1TextPane;
    private JTextPane hexTextPane;
    private JTextPane validationTextPane;
    private JScrollPane validationScrollPane;

    public X509CertAssistant(ToolWindow tw) {
        exporters = new Exporters(x509Certificates, rootPanel);
        tw.setTitleActions(List.of(CBActionManager.buildContextMenu(exporters)));
        PopupHandler.installPopupMenu(certTree, CBActionManager.buildContextMenu(exporters), "X509CertBuddy.CertTree.Actions");

        DefaultTreeModel model = (DefaultTreeModel) certTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Certs", true);
        model.setRoot(root);
        model.reload();
        certTree.addTreeSelectionListener(this::treeSelectionChanged);
        new DropTarget(rootPanel, new FileDropTargetListener(this::filesToProcess));
    }

    private void filesToProcess(List<? extends File> files) {
        for (File file : files) {
            try {
                byte[] data = FileUtils.readFileToByteArray(file);
                CertRetriever certRetriever = new CertRetriever(null);
                addCerts(certRetriever.retrieveCerts(data).stream().map(x -> x.certificate).collect(Collectors.toList()));
            } catch (IOException | CertificateException ignored) {
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
        String pemString = OpenSslWrapper.getPem(selectedCertificate);
        pemTextPane.setText(pemString);
        asn1TextPane.setText(OpenSslWrapper.getAsn1(selectedCertificate));
        hexTextPane.setContentType("text/html");
        hexTextPane.setText(HexDumpWrapper.getHex(selectedCertificate));
        validationTextPane.setContentType("text/html");
        validationTextPane.setText(OpenSslWrapper.getValidation(selectedCertificate, x509Certificates));
        // SwingUtilities.invokeLater(new Runnable() {
        //   @Override
        // public void run() {
        validationScrollPane.getViewport().setViewPosition(new Point(0, 0));
        //}
        // });
        //   ((JScrollPane)validationTextPane.getParent()).getVerticalScrollBar().setValue(0);
    }


    public void removeCert() {
        x509Certificates.remove(selectedCertificate);
        buildTree();
    }

    public void removeAllCerts() {
        x509Certificates.clear();
        buildTree();
    }


    public JPanel getContent() {
        return rootPanel;
    }

    public void addCerts(List<X509Certificate> certs) {
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
        return selectedCertificate != null;
    }

    public boolean hasCerts() {
        return certTree.getModel().getChildCount(certTree.getModel().getRoot()) > 0;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(200);
        splitPane1.setOrientation(0);
        rootPanel.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 400), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(panel1);
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        panel1.add(tabbedPane1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Details", panel2);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        certDetailsTextPane = new JTextPane();
        certDetailsTextPane.setEditable(false);
        Font certDetailsTextPaneFont = this.$$$getFont$$$("Courier New", -1, -1, certDetailsTextPane.getFont());
        if (certDetailsTextPaneFont != null) certDetailsTextPane.setFont(certDetailsTextPaneFont);
        scrollPane1.setViewportView(certDetailsTextPane);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("PEM", panel3);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pemTextPane = new JTextPane();
        pemTextPane.setEditable(false);
        Font pemTextPaneFont = this.$$$getFont$$$("Courier New", -1, -1, pemTextPane.getFont());
        if (pemTextPaneFont != null) pemTextPane.setFont(pemTextPaneFont);
        scrollPane2.setViewportView(pemTextPane);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("ASN.1", panel4);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel4.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        asn1TextPane = new JTextPane();
        asn1TextPane.setEditable(false);
        Font asn1TextPaneFont = this.$$$getFont$$$("Courier New", -1, -1, asn1TextPane.getFont());
        if (asn1TextPaneFont != null) asn1TextPane.setFont(asn1TextPaneFont);
        scrollPane3.setViewportView(asn1TextPane);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Hex", panel5);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel5.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        hexTextPane = new JTextPane();
        hexTextPane.setEditable(false);
        Font hexTextPaneFont = this.$$$getFont$$$("Courier New", -1, -1, hexTextPane.getFont());
        if (hexTextPaneFont != null) hexTextPane.setFont(hexTextPaneFont);
        scrollPane4.setViewportView(hexTextPane);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Validation", panel6);
        validationScrollPane = new JScrollPane();
        panel6.add(validationScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        validationTextPane = new JTextPane();
        validationScrollPane.setViewportView(validationTextPane);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel7);
        final JScrollPane scrollPane5 = new JScrollPane();
        panel7.add(scrollPane5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        certTree = new JTree();
        certTree.setRootVisible(false);
        scrollPane5.setViewportView(certTree);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
