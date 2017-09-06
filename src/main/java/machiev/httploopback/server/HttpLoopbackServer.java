package machiev.httploopback.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import machiev.httploopback.data.HttpRequestData;
import machiev.httploopback.request.RequestProcessor;
import machiev.httploopback.request.StringRequestProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class HttpLoopbackServer<T, RD extends HttpRequestData<T>> implements HttpServerAccess<T, RD> {

    private HttpServer server;
    private boolean isResponse;
    private RD response;

    public static HttpServerAccess<String, HttpRequestData<String>> startServer(int port, String serviceContext) throws IOException {
        return startServer(port, serviceContext, new StringRequestProcessor());
    }

    public static <T, RD extends HttpRequestData<T>> HttpServerAccess<T, RD> startServer(int port, String serviceContext, RequestProcessor<T, RD> requestProcessor) throws IOException {
        HttpLoopbackServer<T, RD> httpServerManager = new HttpLoopbackServer<>();
        httpServerManager.createAndStartServer(port, serviceContext, requestProcessor);
        return httpServerManager;
    }

    private synchronized void createAndStartServer(int port, String serviceContext, RequestProcessor<T, RD> requestProcessor) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server = server;
        server.createContext(serviceContext, new RequestHandler<>(this, requestProcessor));
        Thread serverThread = new Thread(server::start);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @Override
    public synchronized void stopServer() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    @Override
    public synchronized RD getResponse() {
        try {
            while (!isResponse) {
                wait();
            }
            isResponse = false;
            RD requestRef = response;
            response = null;
            return requestRef;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void setResponse(RD response) {
        this.response = response;
        this.isResponse = true;
        this.notify();
    }

    private static class RequestHandler<T, RD extends HttpRequestData<T>> implements HttpHandler {
        private final HttpLoopbackServer<T, RD> httpLoopbackServer;
        private final RequestProcessor<T, RD> requestProcessor;

        RequestHandler(HttpLoopbackServer<T, RD> httpServerManager, RequestProcessor<T, RD> requestProcessor) {
            this.httpLoopbackServer = httpServerManager;
            this.requestProcessor = requestProcessor;
        }

        private void sendOk(HttpExchange exchange) {
            try {
                exchange.sendResponseHeaders(204, -1L);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private ByteArrayOutputStream getRequestBody(HttpExchange exchange) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            InputStream requestBody = exchange.getRequestBody();
            if (requestBody != null) {
                try {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = requestBody.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        }

        @Override
        public void handle(HttpExchange exchange) {

            sendOk(exchange);

            httpLoopbackServer.setResponse(requestProcessor.processRequest(getRequestBody(exchange), exchange));
        }
    }

}
