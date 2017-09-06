package machiev.httploopback.request;

import com.sun.net.httpserver.HttpExchange;
import machiev.httploopback.data.HttpRequestData;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class StringRequestProcessor implements RequestProcessor<String, HttpRequestData<String>> {
    public HttpRequestData<String> processRequest(ByteArrayOutputStream requestBody, HttpExchange exchange) {
        String bodyString;
        try {
            bodyString = requestBody.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return new HttpRequestData<>(exchange, bodyString);
    }
}
