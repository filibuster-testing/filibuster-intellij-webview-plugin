package cloud.filibuster.plugin.files;

import com.intellij.openapi.Disposable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SingleFileWatcher implements Disposable {

    protected final File file;
    protected final long period;
    protected Timer timer;
    protected List<FileListener> listeners = new ArrayList<>();

    private enum FileEventKind {
        CREATED, MODIFIED, DELETED
    }

    private long lastModifiedTime;

    private boolean timerActive = false;

    public SingleFileWatcher(File path, long period) {
        this.file = path;
        this.period = period;
    }

    public SingleFileWatcher addListener(FileListener listener) {
        listeners.add(listener);
        return this;
    }

    public SingleFileWatcher removeListener(FileListener listener) {
        listeners.remove(listener);
        return this;
    }

    protected void pollFileInfo() {
        long lastModifiedBefore = lastModifiedTime;
        lastModifiedTime = file.lastModified();
        if (lastModifiedBefore == 0L && lastModifiedTime > 0L) {
            notifyListeners(FileEventKind.CREATED);
        } else if (lastModifiedBefore > 0L && lastModifiedTime == 0L) {
            notifyListeners(FileEventKind.DELETED);
        } else if (lastModifiedTime > lastModifiedBefore) {
            notifyListeners(FileEventKind.MODIFIED);
        }
    }

    protected void notifyListeners(FileEventKind event) {
        FileEvent fileEvent = new FileEvent(file);
        for (FileListener listener : listeners) {
            switch (event) {
                case CREATED:
                    listener.onCreated(fileEvent);
                    break;
                case MODIFIED:
                    listener.onModified(fileEvent);
                    break;
                case DELETED:
                    listener.onDeleted(fileEvent);
                    break;
            }
        }
    }

    public void startWatch() {
        if (!timerActive) {
            timer = new Timer(true);
            lastModifiedTime = file.lastModified();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pollFileInfo();
                }
            }, 0, period);
            timerActive = true;
        }
    }

    public void stopWatch() {
        timer.cancel();
        timerActive = false;
    }

    @Override
    public void dispose() {
        stopWatch();
    }
}
