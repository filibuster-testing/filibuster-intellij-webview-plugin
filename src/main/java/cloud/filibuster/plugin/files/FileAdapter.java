package cloud.filibuster.plugin.files;

public abstract class FileAdapter implements FileListener {
    @Override
    public void onCreated(FileEvent event) {
    }

    @Override
    public void onModified(FileEvent event) {
    }

    @Override
    public void onDeleted(FileEvent event) {
    }
}
