package cloud.filibuster.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

public class WindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        FilibusterViewerWindow view =  project.getService(FilibusterViewerWindowService.class).getFilibusterViewerWindow();
        Content content = toolWindow.getContentManager().getFactory().createContent(view.getContent(), "Test Executions", false);
        toolWindow.getContentManager().addContent(content);
        content.setDisposer(view);
    }
}
