// Taken from: https://dzone.com/articles/listening-to-fileevents-with-java-nio

package cloud.filibuster.plugin;

import cloud.filibuster.plugin.files.FileAdapter;
import cloud.filibuster.plugin.files.FileEvent;
import cloud.filibuster.plugin.files.SingleFileWatcher;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefApp;

import javax.swing.*;
import java.io.File;

public class FilibusterViewerWindow implements Disposable {
    private JBCefBrowser webView;

    private final SingleFileWatcher singleFileWatcher;

    private final String TEST_PAGE_PATH = "/tmp/filibuster/index.html";

    public FilibusterViewerWindow(Project project) {
        this.webView = new JBCefBrowser();
        registerAppSchemeHandler();
        Disposer.register(this, this.webView);
        this.singleFileWatcher = new SingleFileWatcher(new File(TEST_PAGE_PATH), 1000);
        singleFileWatcher.addListener(new FileAdapter() {

            public void onCreated(FileEvent event) {
                System.out.println("file.created: " + event.getFile().getName());
                reloadFilibusterTestPage();
            }

            public void onModified(FileEvent event) {
                System.out.println("file.modified: " + event.getFile().getName());
                reloadFilibusterTestPage();
            }

            public void onDeleted(FileEvent event) {
                System.out.println("file.deleted: " + event.getFile().getName());
                reloadFilibusterTestPage();
            }
        }).startWatch();
        reloadFilibusterTestPage();
    }

    private void reloadFilibusterTestPage()
    {
        this.webView.loadURL("file://"+ TEST_PAGE_PATH);
    }

    public JComponent getContent() {
        return webView.getComponent();
    }

    private void registerAppSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory("http", "filibuster.local", new CustomSchemeHandlerFactory());
    }

    public void resumeUpdates() {
        reloadFilibusterTestPage();
        this.singleFileWatcher.startWatch();
    }

    public void stopUpdates() {
        this.singleFileWatcher.stopWatch();
    }

    @Override
    public void dispose() {
        webView.dispose();
        singleFileWatcher.dispose();
    }
}
