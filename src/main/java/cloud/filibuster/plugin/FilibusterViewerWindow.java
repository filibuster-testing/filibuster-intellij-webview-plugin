// Taken from: https://dzone.com/articles/listening-to-fileevents-with-java-nio

package cloud.filibuster.plugin;

import cloud.filibuster.plugin.files.FileAdapter;
import cloud.filibuster.plugin.files.FileEvent;
import cloud.filibuster.plugin.files.FileWatcher;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefApp;

import javax.swing.*;
import java.io.File;

public class FilibusterViewerWindow implements Disposable {
    private JBCefBrowser webView;

    private FileWatcher fileWatcher;

    public FilibusterViewerWindow(Project project) {
        JBCefBrowser browser = new JBCefBrowser();
        registerAppSchemeHandler();
        browser.loadURL("file:///tmp/filibuster/index.html");
        Disposer.register(this, browser);

        this.webView = browser;
        this.fileWatcher = new FileWatcher(new File("/tmp/filibuster"));
        fileWatcher.addListener(new FileAdapter() {
            private void reloadBrowserConditionally(File file) {
                if (file.getName().endsWith("index.html")) {
                    browser.loadURL("file://" + file.getAbsolutePath());
                }
            }

            public void onCreated(FileEvent event) {
                System.out.println("file.created: " + event.getFile().getName());
                reloadBrowserConditionally(event.getFile());
            }

            public void onModified(FileEvent event) {
                System.out.println("file.modified: " + event.getFile().getName());
                reloadBrowserConditionally(event.getFile());
            }

            public void onDeleted(FileEvent event) {
                System.out.println("file.deleted: " + event.getFile().getName());
                reloadBrowserConditionally(event.getFile());
            }
        }).watch();
    }

    public JComponent getContent() {
        return webView.getComponent();
    }

    private void registerAppSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory("http", "filibuster.local", new CustomSchemeHandlerFactory());
    }

    @Override
    public void dispose() {
        webView.dispose();
    }
}
