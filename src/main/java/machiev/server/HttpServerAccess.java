package machiev.server;

import machiev.data.HttpRequestData;

public interface HttpServerAccess {

    /**
     * Stops HTTP server.
     */
    void stopServer();

    /**
     * Blocking method that waits for a response.
     * @return response sent after server handles a request.
     */
    HttpRequestData getResponse();
}
