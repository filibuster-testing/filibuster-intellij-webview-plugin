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

    public FilibusterViewerWindow(Project project) {
        JBCefBrowser browser = new JBCefBrowser();
        registerAppSchemeHandler();
        browser.loadURL("file:///tmp/filibuster/index.html");
        Disposer.register(this, browser);

        this.webView = browser;
        this.singleFileWatcher = new SingleFileWatcher(new File("/tmp/filibuster/index.html"), 1000);
        singleFileWatcher.addListener(new FileAdapter() {
            private void reloadBrowser(File file) {
                browser.loadURL("file://" + file.getAbsolutePath());
            }

            public void onCreated(FileEvent event) {
                System.out.println("file.created: " + event.getFile().getName());
                reloadBrowser(event.getFile());
            }

            public void onModified(FileEvent event) {
                System.out.println("file.modified: " + event.getFile().getName());
                reloadBrowser(event.getFile());
            }

            public void onDeleted(FileEvent event) {
                System.out.println("file.deleted: " + event.getFile().getName());
                reloadBrowser(event.getFile());
            }
        }).startWatch();
    }

    public JComponent getContent() {
        return webView.getComponent();
    }

    private void registerAppSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory("http", "filibuster.local", new CustomSchemeHandlerFactory());
    }

    public void resumeUpdates() {
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
