package ru.alfastrah.library.log.rest.starter;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;

@UtilityClass
class LogUtil {
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_SHORT = ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    static String buildRequestLog(String uri, Instant start, String method, byte[] body) {
        return "IN url [" + uri + "] " +
                "start [" + start.atZone(systemDefault()).format(ISO_LOCAL_DATE_TIME_SHORT) + "] " +
                "method [" + method + "] " +
                "request_body [" + new String(body, UTF_8) + "]";
    }

    static String buildResponseLog(String url, Instant start, int statusCode, byte[] body) {
        return "OUT url [" + url + "] " +
                "duration [" + (currentTimeMillis() - start.toEpochMilli()) + "ms] " +
                "status [" + statusCode + "] " +
                "response_body [" + new String(body, UTF_8) + "]";

    }

    static String buildErrorLog(String uri, Instant start, String message) {
        return "ERR url [" + uri + "] " +
                "duration [" + (currentTimeMillis() - start.toEpochMilli()) + "ms] " +
                "error [" + message + "] ";
    }
}
