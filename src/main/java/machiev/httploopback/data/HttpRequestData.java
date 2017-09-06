package machiev.httploopback.data;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;

/**
 * Request data transfer object.
 */
public class HttpRequestData<T> {
    private final URI uri;
    private final String httpMethod;
    private final Headers requestHeaders;
    private final T body;

    public HttpRequestData(HttpExchange exchange, T body) {
        this.uri = exchange.getRequestURI();
        this.httpMethod = exchange.getRequestMethod();
        this.requestHeaders = exchange.getRequestHeaders();
        this.body = body;
    }

    public URI getUri() {
        return uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Headers getRequestHeaders() {
        return requestHeaders;
    }

    public T getBody() {
        return body;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getQuery() {
        return uri.getQuery();
    }

    @Override
    public String toString() {
        return "request URI: " + uri + "\nHTTP method: " + httpMethod + "\nrequest body: " + body;
    }
}