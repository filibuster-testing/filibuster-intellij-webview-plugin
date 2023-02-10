package cloud.filibuster.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

public class FilibusterToolWindowListener implements ToolWindowManagerListener {
    private final Project project;

    private boolean wasLastActive = false;

    public FilibusterToolWindowListener(Project project) {
        this.project = project;
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
        ToolWindowManager.getInstance(project).getToolWindow("Filibuster").getComponent();
        boolean isActiveNow = ToolWindowManager.getInstance(project).getToolWindow("Filibuster").isActive();

        // Do stuff?

        wasLastActive = isActiveNow;
    }
}
