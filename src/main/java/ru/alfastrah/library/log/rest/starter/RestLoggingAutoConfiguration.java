package ru.alfastrah.library.log.rest.starter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({LogClientRequest.class, LogServerRequest.class})
@EnableConfigurationProperties({RestLoggingAutoConfiguration.ClientLoggingProperties.class, RestLoggingAutoConfiguration.ServerLoggingProperties.class})
public class RestLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogRestTemplateCustomizer.class)
    @ConditionalOnProperty(name = "library.log.rest.client.enable", havingValue = "true")
    public LogRestTemplateCustomizer logRestTemplateCustomizer() {
        return new LogRestTemplateCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean(LogServerRequest.class)
    @ConditionalOnProperty(name = "library.log.rest.server.enable", havingValue = "true")
    public LogServerRequest logServerRequest() {
        return new LogServerRequest();

    }

    @Setter
    @Getter
    @ConfigurationProperties(prefix = "library.log.rest.server")
    static class ServerLoggingProperties {
        private boolean enable;
    }

    @Setter
    @Getter
    @ConfigurationProperties(prefix = "library.log.rest.client")
    static class ClientLoggingProperties {
        private boolean enable;
    }
}
