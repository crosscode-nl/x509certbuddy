package nl.crosscode.x509certbuddy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class x509CertAssistantFactory implements ToolWindowFactory {
    private static x509CertAssistant x509CertAssistant = new x509CertAssistant();

    public static x509CertAssistant getX509CertAssistant() {
        return x509CertAssistant;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Content content = ContentFactory.SERVICE.getInstance().createContent(x509CertAssistant.getContent(),"",false);
        toolWindow.getContentManager().addContent(content);
    }
}
