package machiev.data;

import java.net.URI;

/**
 * Request data transfer object.
 */
public class HttpRequestData {
    private final URI uri;
    private final String httpMethod;
    private final String body;

    public HttpRequestData(URI uri, String httpMethod, String body) {
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.body = body;
    }

    public URI getUri() {
        return uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getBody() {
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