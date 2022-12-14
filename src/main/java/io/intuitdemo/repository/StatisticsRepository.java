package io.intuitdemo.repository;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.intuitdemo.config.props.IntuitConfig;
import io.intuitdemo.data.TransactionStatics;
import io.intuitdemo.data.dto.TransactionDTO;
import io.intuitdemo.data.dto.TransactionStatsDTO;
import io.intuitdemo.service.StatisticsCalculatorService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.groupingBy;
import static reactor.core.publisher.Mono.just;
import static reactor.core.scheduler.Schedulers.boundedElastic;

/**
 * for real use case I would have used redis distributed cache but for demo I will use caffeine
 */
@Component
public class StatisticsRepository {

    private final TransactionRepository transactionRepository;
    private final StatisticsCalculatorService statisticsCalculatorService;
    private final IntuitConfig intuitConfig;

    private Cache<String, TransactionStatics> staticsDTOCache;

    public StatisticsRepository(TransactionRepository transactionRepository, StatisticsCalculatorService statisticsCalculatorService, IntuitConfig intuitConfig) {
        this.transactionRepository = transactionRepository;
        this.statisticsCalculatorService = statisticsCalculatorService;
        this.intuitConfig = intuitConfig;
    }

    @PostConstruct
    public void initialize() {
        staticsDTOCache = Caffeine.newBuilder()
                .maximumSize(intuitConfig.getNumberOfCountries())
                .build();
        updateStatisticsOnStart();
    }

    private void updateStatisticsOnStart() {
        transactionRepository.findByTransactionEpochSecondsAfter(now().minus(1, HOURS).getEpochSecond())
                .collectList()
                .map(transactionDTOList -> transactionDTOList.stream().collect(groupingBy(TransactionDTO::getCountry)))
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> statisticsCalculatorService.calculateNewStatisticsForCountry(entry.getKey(), entry.getValue()))
                .doOnNext(transactionStatics -> staticsDTOCache.put(transactionStatics.getCountry(), transactionStatics))
                .subscribeOn(boundedElastic())
                .subscribe();
    }

    public Mono<TransactionStatsDTO> getStatistics() {
        return just(new TransactionStatsDTO(new ArrayList<>(staticsDTOCache.asMap().values())));
    }

    public Mono<TransactionStatics> getStatisticsForCountry(String country) {
        return just(staticsDTOCache.get(country, s -> TransactionStatics.builder().build()));
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
