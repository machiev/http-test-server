package machiev.httploopback.server;

import machiev.httploopback.data.HttpRequestData;

public interface HttpServerAccess<T, RD extends HttpRequestData<T>> {

    /**
     * Stops HTTP server.
     */
    void stopServer();

    /**
     * Blocking method that waits for a response.
     * @return response sent after server handles a request.
     */
     RD getResponse();
}
