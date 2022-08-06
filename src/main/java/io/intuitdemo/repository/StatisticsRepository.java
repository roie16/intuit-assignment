package io.intuitdemo.repository;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.intuitdemo.data.TransactionStatics;
import io.intuitdemo.data.dto.TransactionStatsDTO;
import io.intuitdemo.service.StatisticsCalculatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static reactor.core.publisher.Mono.just;
import static reactor.core.scheduler.Schedulers.boundedElastic;

/**
 * for real use case I would have used redis distributed cache but for demo I will use caffeine
 */
@Component
public class StatisticsRepository {

    @Value("${intuit.number-of-countries}")
    private int numberOfCountries;

    private final TransactionRepository transactionRepository;
    private final StatisticsCalculatorService statisticsCalculatorService;

    private Cache<String, TransactionStatics> staticsDTOCache;

    public StatisticsRepository(TransactionRepository transactionRepository, StatisticsCalculatorService statisticsCalculatorService) {
        this.transactionRepository = transactionRepository;
        this.statisticsCalculatorService = statisticsCalculatorService;
    }

    @PostConstruct
    public void initialize() {
        staticsDTOCache = Caffeine.newBuilder()
                .maximumSize(numberOfCountries)
                .build();
    }

    public Mono<TransactionStatsDTO> getStatistics() {
        return just(new TransactionStatsDTO(new ArrayList<>(staticsDTOCache.asMap().values())));
    }

    public TransactionStatics getStatisticsForCountry(String country) {
        return staticsDTOCache.get(country, s -> TransactionStatics.builder().build());
    }

    public void updateStatisticsForCountries(List<String> countries) {
        countries.forEach(this::updateStatisticsForCountry);
    }


    public void updateStatisticsForCountry(String country) {
        transactionRepository.findByCountryAndTransactionEpochSecondsAfter(country, now().minus(1, HOURS).getEpochSecond())
                .collectList()
                .map(transactionDTOList -> statisticsCalculatorService.calculateNewStatisticsForCountry(country, transactionDTOList))
                .doOnNext(transactionStatics -> staticsDTOCache.put(country, transactionStatics))
                .subscribeOn(boundedElastic())
                .subscribe();
    }

}
