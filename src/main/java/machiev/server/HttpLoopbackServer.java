package machiev.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import machiev.data.HttpRequestData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class HttpLoopbackServer implements HttpServerAccess {

    private HttpServer server;
    private boolean isResponse;
    private HttpRequestData response;

    public static HttpServerAccess startServer(int port, String serviceContext) throws IOException {
        HttpLoopbackServer httpServerManager = new HttpLoopbackServer();
        httpServerManager.createAndStartServer(port, serviceContext);
        return httpServerManager;
    }

    private synchronized void createAndStartServer(int port, String serviceContext) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server = server;
        server.createContext(serviceContext, new RequestHandler(this));
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                server.start();
            }
        });
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
    public synchronized HttpRequestData getResponse() {
        try {
            while (!isResponse) {
                wait();
            }
            isResponse = false;
            HttpRequestData requestRef = response;
            response = null;
            return requestRef;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void setResponse(HttpRequestData response) {
        this.response = response;
        this.isResponse = true;
        this.notify();
    }

    private static class RequestHandler implements HttpHandler {
        private final HttpLoopbackServer httpLoopbackServer;

        RequestHandler(HttpLoopbackServer httpServerManager) {
            this.httpLoopbackServer = httpServerManager;
        }

        private void sendOk(HttpExchange exchange) {
            try {
                exchange.sendResponseHeaders(204, -1L);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void handle(HttpExchange exchange) {

            sendOk(exchange);

            String bodyString = null;
            InputStream requestBody = exchange.getRequestBody();
            if (requestBody != null) {
                try {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = requestBody.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    bodyString = result.toString("UTF-8");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            httpLoopbackServer.setResponse(new HttpRequestData(exchange.getRequestURI(), exchange.getRequestMethod(), bodyString));
        }
    }

}
