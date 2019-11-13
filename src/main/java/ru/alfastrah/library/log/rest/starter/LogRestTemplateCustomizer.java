package ru.alfastrah.library.log.rest.starter;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class LogRestTemplateCustomizer implements RestTemplateCustomizer {
    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
        restTemplate.getInterceptors().add(new LogClientRequest());
    }
}
