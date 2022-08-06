package io.intuitdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {



    @Bean(value = "geoWebClient")
    public WebClient geoWebClient(@Value("${intuit.geo-url}") String url) {
        return WebClient.create(url);
    }

}
