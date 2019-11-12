package ru.alfastrah.library.log.rest.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;

import static java.lang.System.nanoTime;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LogClientHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String uri = request.getURI().toString();
        long start = nanoTime();
        try {
            log.info(logRequest(request, body, start));
            ClientHttpResponse response = execution.execute(request, body);
            log.info(logResponse(response, uri, start));
            return response;
        } catch (Exception e) {
            log.error(logError(e.getMessage(), uri, start), e);
            throw e;
        }
    }

    private String logRequest(HttpRequest request, byte[] body, long start) {
        return "id:" + start + " | " +
                "request_body:" + new String(body, UTF_8) + " | " +
                "url:" + request.getURI() + " | " +
                "method:" + request.getMethodValue();
    }

    private String logResponse(ClientHttpResponse response, String uri, long start) throws IOException {
        return "id:" + start + " | " +
                "response_body:" + StreamUtils.copyToString(response.getBody(), UTF_8) + " | " +
                "url:" + uri + " | " +
                "status:" + response.getRawStatusCode() + " | " +
                "duration:" + (nanoTime() - start) / 1000;
    }

    private String logError(String message, String uri, long start) {
        return "id:" + start + " | " +
                "error:" + message + " | " +
                "url:" + uri + " | " +
                "duration:" + (nanoTime() - start) / 1000;
    }
}

