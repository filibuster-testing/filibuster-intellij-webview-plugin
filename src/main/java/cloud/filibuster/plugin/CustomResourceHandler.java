package cloud.filibuster.plugin;

import cloud.filibuster.plugin.resource_handler.ClosedConnection;
import cloud.filibuster.plugin.resource_handler.OpenedConnection;
import cloud.filibuster.plugin.resource_handler.ResourceHandlerState;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.net.URL;

public class CustomResourceHandler implements CefResourceHandler {
    private ResourceHandlerState state = new ClosedConnection();

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        String url = request.getURL();

        if (url != null) {
            String pathToResource = url.replace("http://filibuster.local", "file:///tmp/filibuster");
            URL newUrl = getClass().getClassLoader().getResource(pathToResource);

            try {
                state = new OpenedConnection(newUrl.openConnection());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            callback.Continue();
            return true;
        }

        return false;
    }

    @Override
    public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        state.getResponseHeaders(response, responseLength, redirectUrl);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        return state.readResponse(dataOut, bytesToRead, bytesRead, callback);
    }

    @Override
    public void cancel() {
        state.close();
        state = new ClosedConnection();
    }
}
