package ru.alfastrah.library.log.rest.starter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

import static java.lang.System.nanoTime;
import static ru.alfastrah.library.log.rest.starter.LogUtil.*;

@Slf4j
public class LogServerRequest extends OncePerRequestFilter {

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Instant start = Instant.now();
        boolean isRemoveMDC = false;
        if (MDC.get("UID") == null) {
            MDC.put("UID", String.valueOf(nanoTime()));
            isRemoveMDC = true;
        }
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(cachingRequest, cachingResponse);
        } catch (Exception e) {
            log.error(buildErrorLog(request.getRequestURI(), start, e.getMessage()), e);
            throw e;
        } finally {
            log.info(buildRequestLog(cachingRequest.getRequestURI(), start, cachingRequest.getMethod(), cachingRequest.getContentAsByteArray()));
            log.info(buildResponseLog(request.getRequestURI(), start, response.getStatus(), cachingResponse.getContentAsByteArray()));
            cachingResponse.copyBodyToResponse();
            if (isRemoveMDC) {
                MDC.remove("UID");
            }
        }
    }
}
