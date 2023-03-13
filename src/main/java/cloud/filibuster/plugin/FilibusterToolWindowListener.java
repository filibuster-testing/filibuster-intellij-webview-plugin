package cloud.filibuster.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FilibusterToolWindowListener implements ToolWindowManagerListener {
    private final Project project;

    private boolean wasLastActive = false;

    public FilibusterToolWindowListener(Project project) {
        this.project = project;
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
        ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("Filibuster");
        FilibusterViewerWindow filibusterViewerWindow = project.getService(FilibusterViewerWindowService.class).getFilibusterViewerWindow();
        if(Objects.requireNonNull(tw).isVisible())
        {
            filibusterViewerWindow.resumeUpdates();
        }
        else {
            filibusterViewerWindow.stopUpdates();
        }
        wasLastActive = tw.isActive();
    }
}
