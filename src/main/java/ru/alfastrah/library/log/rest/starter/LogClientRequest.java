package ru.alfastrah.library.log.rest.starter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.time.Instant;

import static java.lang.System.nanoTime;
import static org.springframework.util.StreamUtils.copyToByteArray;
import static ru.alfastrah.library.log.rest.starter.LogUtil.*;

@Slf4j
class LogClientRequest implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Instant start = Instant.now();
        boolean isRemoveMDC = false;
        if (MDC.get("UID") == null) {
            MDC.put("UID", String.valueOf(nanoTime()));
            isRemoveMDC = true;
        }
        try {
            log.info(buildRequestLog(request.getURI().getPath(), start, request.getMethodValue(), body));
            ClientHttpResponse response = execution.execute(request, body);
            log.info(buildResponseLog(request.getURI().toString(), start, response.getRawStatusCode(), copyToByteArray(response.getBody())));
            return response;
        } catch (Exception e) {
            log.error(buildErrorLog(request.getURI().toString(), start, e.getMessage()), e);
            throw e;
        } finally {
            if (isRemoveMDC) {
                MDC.remove("UID");
            }
        }
    }
}
