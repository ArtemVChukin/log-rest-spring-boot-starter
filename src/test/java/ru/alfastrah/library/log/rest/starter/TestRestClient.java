package ru.alfastrah.library.log.rest.starter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TestRestClient {
    static final String HOST = "http://localhost:8080";
    private final RestTemplate restTemplate;

    String health() {
        return restTemplate.getForEntity(HOST + "/health", String.class).getBody();
    }

    String string(String param, String body) {
        return restTemplate.postForObject(HOST + "/string?param=" + param, body, String.class);
    }

    TestHolder holder(String param, TestHolder body) {
        return restTemplate.postForObject(HOST + "holder?param=" + param, body, TestHolder.class);
    }

}
