// Taken from: https://dzone.com/articles/listening-to-fileevents-with-java-nio

package cloud.filibuster.plugin.files;

import java.util.EventListener;

public interface FileListener extends EventListener {
    public void onCreated(FileEvent event);
    public void onModified(FileEvent event);
    public void onDeleted(FileEvent event);
}