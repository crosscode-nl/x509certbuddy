package nl.crosscode.x509certbuddy;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Factory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import nl.crosscode.x509certbuddy.ui.x509CertAssistant;
import nl.crosscode.x509certbuddy.utils.EditorUtilsFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class x509CertAssistantFactory implements ToolWindowFactory, EditorFactoryListener, Disposable, DocumentListener {

    private static final HashMap<Project,nl.crosscode.x509certbuddy.ui.x509CertAssistant> instances = new HashMap<>();

    public x509CertAssistantFactory() {
        EditorFactory.getInstance().addEditorFactoryListener(this,this);
    }

    public static synchronized x509CertAssistant getInstance(Project project) {
        return instances.get(project);
    }

    private static synchronized x509CertAssistant getInstance(Project project, Factory<nl.crosscode.x509certbuddy.ui.x509CertAssistant> factory) {
        x509CertAssistant value = instances.get(project);
        if (value==null) {
            value = factory.create();
            instances.put(project,value);
        }
        return value;
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        EditorUtilsFactory.getInstance().readCertsFromEditor(event.getEditor());
        event.getEditor().getDocument().addDocumentListener(this);
        EditorFactoryListener.super.editorCreated(event);

    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        event.getEditor().getDocument().removeDocumentListener(this);
        EditorUtilsFactory.getInstance().removeEditor(event.getEditor());
        EditorFactoryListener.super.editorReleased(event);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        // TODO: Make this lighter somehow
        for (Editor editor : EditorFactory.getInstance().getEditors(event.getDocument())) {
            EditorUtilsFactory.getInstance().readCertsFromEditor(editor);
        }
        DocumentListener.super.documentChanged(event);
    }

    @Override
    public synchronized void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        nl.crosscode.x509certbuddy.ui.x509CertAssistant x509CertAssistant = getInstance(project,()->new x509CertAssistant(toolWindow));
        Content content = toolWindow.getContentManager().getFactory().createContent(x509CertAssistant.getContent(),"",false);
        toolWindow.getContentManager().addContent(content);
        for (Editor editor : EditorFactory.getInstance().getAllEditors()) {
            EditorUtilsFactory.getInstance().readCertsFromEditor(editor);
        }
    }


    @Override
    public void dispose() {

    }
}
