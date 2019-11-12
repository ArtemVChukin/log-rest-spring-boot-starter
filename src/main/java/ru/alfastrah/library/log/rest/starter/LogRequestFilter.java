package ru.alfastrah.library.log.rest.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.System.nanoTime;
import static org.springframework.util.StreamUtils.copyToString;

public class LogRequestFilter extends OncePerRequestFilter {
    private static Logger log = LoggerFactory.getLogger(LogClientHttpRequestInterceptor.class);

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        long start = nanoTime();
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(cachingRequest, cachingResponse);
        } catch (Exception e) {
            log.error(logError(e.getMessage(), request.getRequestURI(), start), e);
            throw e;
        } finally {
            log.info(logRequest(cachingRequest, start));
            log.info(logResponse(cachingResponse, response.getStatus(), start));
            cachingResponse.copyBodyToResponse();
        }

    }

    private String logRequest(ContentCachingRequestWrapper request, long start) throws IOException {
        return "id:" + start + " | " +
                "request_body:" + copyToString(request.getInputStream(), Charset.forName(request.getCharacterEncoding())) + " | " +
                "url:" + request.getRequestURI() + " | " +
                "method:" + request.getMethod();
    }

    private String logResponse(ContentCachingResponseWrapper response, int status, long start) throws IOException {
        return "id:" + start + " | " +
                "response_body:" + copyToString(response.getContentInputStream(), Charset.forName(response.getCharacterEncoding())) + " | " +
                "status:" + status + " | " +
                "duration:" + (nanoTime() - start) / 1000;

    }

    private String logError(String message, String uri, long start) {
        return "id:" + start + " | " +
                "error:" + message + " | " +
                "url:" + uri + " | " +
                "duration:" + (nanoTime() - start) / 1000;
    }

}
