package cloud.filibuster.plugin;

import com.intellij.openapi.project.Project;

public class FilibusterViewerWindowService {
    private final FilibusterViewerWindow filibusterViewerWindow;

    public FilibusterViewerWindow getFilibusterViewerWindow() {
        return this.filibusterViewerWindow;
    }

    public FilibusterViewerWindowService(Project project) {
        System.out.println("This is called why?");
        this.filibusterViewerWindow = new FilibusterViewerWindow(project);
    }
}
