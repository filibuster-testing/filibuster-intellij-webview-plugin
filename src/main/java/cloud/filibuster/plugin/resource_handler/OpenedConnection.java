package cloud.filibuster.plugin.resource_handler;

import org.cef.callback.CefCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class OpenedConnection implements ResourceHandlerState {
    private URLConnection connection;

    private InputStream inputStream;

    public OpenedConnection(URLConnection connection) {
        this.connection = connection;

        try {
            this.inputStream = connection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        try {
            String url = connection.getURL().toString();

            if (url.contains(".css")) {
                cefResponse.setMimeType("text/css");
            } else if (url.contains(".js")) {
                cefResponse.setMimeType("text/javascript");
            } else if (url.contains(".html")) {
                cefResponse.setMimeType("text/html");
            } else {
                // since 2021.1 all mime type must be set here, by hand
                cefResponse.setMimeType(connection.getContentType());
            }

            responseLength.set(inputStream.available());
            cefResponse.setStatus(200);
        } catch (IOException e) {
            cefResponse.setError(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND);
            cefResponse.setStatusText(e.getLocalizedMessage());
            cefResponse.setStatus(404);
        }
    }

    @Override
    public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
        int availableSize = 0;

        try {
            availableSize = inputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (availableSize > 0) {
            int maxBytesToRead = Math.min(availableSize, designedBytesToRead);
            int realNumberOfReadBytes = 0;

            try {
                realNumberOfReadBytes = inputStream.read(dataOut, 0, maxBytesToRead);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            bytesRead.set(realNumberOfReadBytes);

            return true;
        } else {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return false;
        }
    }

    @Override
    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
