// Taken from: https://dzone.com/articles/listening-to-fileevents-with-java-nio

package cloud.filibuster.plugin.files;

import java.io.File;
import java.util.EventObject;

public class FileEvent extends EventObject {
    public FileEvent(File file) {
        super(file);
    }

    public File getFile() {
        return (File) getSource();
    }
}
