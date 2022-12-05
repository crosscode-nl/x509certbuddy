package nl.crosscode.x509certbuddy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import nl.crosscode.x509certbuddy.ui.x509CertAssistant;
import org.jetbrains.annotations.NotNull;

public class x509CertAssistantFactory implements ToolWindowFactory {
    private static nl.crosscode.x509certbuddy.ui.x509CertAssistant x509CertAssistant = new x509CertAssistant();

    public static x509CertAssistant getX509CertAssistant() {
        return x509CertAssistant;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Content content = toolWindow.getContentManager().getFactory().createContent(x509CertAssistant.getContent(),"",false);
        toolWindow.getContentManager().addContent(content);
    }
}
