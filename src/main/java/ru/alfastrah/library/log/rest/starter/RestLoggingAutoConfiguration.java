package ru.alfastrah.library.log.rest.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LogClientHttpRequestInterceptor.class)
public class RestLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogRestTemplateCustomizer.class)
    public LogRestTemplateCustomizer customRestTemplateCustomizer() {
        return new LogRestTemplateCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean(LogRequestFilter.class)
    public LogRequestFilter logRequestFilter() {
        return new LogRequestFilter();

    }
}
