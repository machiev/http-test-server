# http-test-server
Simple HTTP server for requestor tests.

Server extracts a request data and makes it available via object of class `HttpRequestData`. 

Simple usage:

 - start a server
```java
public class Main {
    public static void main(String[] args) throws IOException {
        HttpServerAccess httpServerManager = HttpLoopbackServer.startServer(8000, "/test");
        HttpRequestData response = httpServerManager.getResponse();
        System.out.println("Response: " + response);
        httpServerManager.stopServer();
    }
}
```
 - make a request

`curl --data "some data" http://localhost:8000/test`
