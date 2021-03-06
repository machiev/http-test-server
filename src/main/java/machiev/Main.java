package machiev;

import machiev.httploopback.data.HttpRequestData;
import machiev.httploopback.server.HttpLoopbackServer;
import machiev.httploopback.server.HttpServerAccess;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpServerAccess httpServerManager = HttpLoopbackServer.startServer(8000, "/test");
        System.out.println("Server started, waiting for response");

        HttpRequestData response = httpServerManager.getResponse();
        System.out.println("Got response:\n" + response);
        System.out.println("Got headers:\n" + response.getRequestHeaders().entrySet());

        response = httpServerManager.getResponse();
        System.out.println("Got response:\n" + response);
        System.out.println("Path: " + response.getPath());
        System.out.println("Query: " + response.getQuery());

        System.out.println("Stopping server");
        httpServerManager.stopServer();
        System.out.println("Server stopped, exiting...");
    }
}
