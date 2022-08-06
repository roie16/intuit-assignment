package io.intuitdemo.config;

import io.intuitdemo.config.props.IntuitConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    private final IntuitConfig intuitConfig;

    public WebClientConfiguration(IntuitConfig intuitConfig) {
        this.intuitConfig = intuitConfig;
    }

    @Bean(value = "geoWebClient")
    public WebClient geoWebClient() {
        return WebClient.create(intuitConfig.getGeoUrl());
    }
}
