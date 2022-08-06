package io.intuitdemo.repository;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.intuitdemo.config.props.IntuitConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static com.github.benmanes.caffeine.cache.Scheduler.systemScheduler;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

/**
 * for real use case I would have used redis distributed cache but for demo I will use caffeine
 * this class represent a CDC solution(as in Change stream mongo I cannot get the doc after it was remove)
 * that way we have no cron jobs running but statistics are always updated in real time
 */
@Component
public class TransactionCacheRepository {

    private final IntuitConfig intuitConfig;

    private final StatisticsRepository statisticsRepository;

    private Cache<String, String> transactionCache;

    public TransactionCacheRepository(IntuitConfig intuitConfig, StatisticsRepository statisticsRepository) {
        this.intuitConfig = intuitConfig;
        this.statisticsRepository = statisticsRepository;
    }

    @PostConstruct
    public void initialize() {
        transactionCache = Caffeine.newBuilder()
                .maximumSize(intuitConfig.getCacheSize())
                .expireAfterWrite(1, TimeUnit.HOURS)
                .removalListener((key, value, cause) -> statisticsRepository.updateStatisticsForCountry(requireNonNull(value).toString()))
                .scheduler(systemScheduler())
                .build();
    }

    public void addToCache(String country) {
        transactionCache.put(randomUUID().toString(), country);
    }

}
