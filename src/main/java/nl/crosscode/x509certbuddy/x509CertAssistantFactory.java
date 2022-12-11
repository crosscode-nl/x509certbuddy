package nl.crosscode.x509certbuddy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Factory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import nl.crosscode.x509certbuddy.ui.x509CertAssistant;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class x509CertAssistantFactory implements ToolWindowFactory {

    private static HashMap<Project,nl.crosscode.x509certbuddy.ui.x509CertAssistant> instances = new HashMap<>();

    public static synchronized x509CertAssistant getX509CertAssistant(Project project) {
        return instances.get(project);
    }

    private static synchronized x509CertAssistant getX509CertAssistant(Project project, Factory<nl.crosscode.x509certbuddy.ui.x509CertAssistant> factory) {
        x509CertAssistant value = instances.get(project);
        if (value==null) {
            value = factory.create();
            instances.put(project,value);
        }
        return value;
    }

    @Override
    public synchronized void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        nl.crosscode.x509certbuddy.ui.x509CertAssistant x509CertAssistant = getX509CertAssistant(project,()->new x509CertAssistant(toolWindow));
        Content content = toolWindow.getContentManager().getFactory().createContent(x509CertAssistant.getContent(),"",false);
        toolWindow.getContentManager().addContent(content);
    }
}
