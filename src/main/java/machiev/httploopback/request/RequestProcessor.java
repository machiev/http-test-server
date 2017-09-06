package machiev.httploopback.request;

import com.sun.net.httpserver.HttpExchange;
import machiev.httploopback.data.HttpRequestData;

import java.io.ByteArrayOutputStream;

public interface RequestProcessor<T, RD extends HttpRequestData<T>> {
    RD processRequest(ByteArrayOutputStream requestBody, HttpExchange exchange);
}
