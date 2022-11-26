package nl.crosscode.x509certbuddy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class x509CertAssistantFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Content content = ContentFactory.SERVICE.getInstance().createContent(new x509CertAssistant().getContent(),"Hi",false);
        toolWindow.getContentManager().addContent(content);
    }
}
