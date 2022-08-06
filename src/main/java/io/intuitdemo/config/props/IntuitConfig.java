package io.intuitdemo.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "intuit")
@Data
public class IntuitConfig {

    private int maxBatchSize;
    private int numberOfCountries;
    private int cacheSize;
    private String geoUrl;
}
