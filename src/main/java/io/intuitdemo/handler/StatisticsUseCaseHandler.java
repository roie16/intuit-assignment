package io.intuitdemo.handler;

import io.intuitdemo.data.dto.TransactionDTO;
import io.intuitdemo.data.dto.TransactionStatsDTO;
import io.intuitdemo.repository.StatisticsRepository;
import io.intuitdemo.service.MapCodeToCountryNameService;
import io.intuitdemo.service.TransactionListenerService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static java.lang.Integer.parseInt;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class StatisticsUseCaseHandler {
    private static final String COUNTRYCODE = "countrycode";
    private final StatisticsRepository statisticsRepository;
    private final TransactionListenerService transactionListenerService;
    private final MapCodeToCountryNameService mapCodeToCountryNameService;

    public StatisticsUseCaseHandler(StatisticsRepository statisticsRepository, TransactionListenerService transactionListenerService, MapCodeToCountryNameService mapCodeToCountryNameService) {
        this.statisticsRepository = statisticsRepository;
        this.transactionListenerService = transactionListenerService;
        this.mapCodeToCountryNameService = mapCodeToCountryNameService;
    }

    public Mono<ServerResponse> getStatistics(ServerRequest ignored) {
        return ok().body(statisticsRepository.getStatistics(), TransactionStatsDTO.class);
    }

    public Mono<ServerResponse> startFlow(ServerRequest ignored) {
        return ok().body(transactionListenerService.listenToTransactionStream(), TransactionDTO.class);
    }

    public Mono<ServerResponse> getStatisticsForCountry(ServerRequest request) {
        return ok().body(statisticsRepository
                .getStatisticsForCountry(mapCodeToCountryNameService.getNameFromCode(parseInt(request.pathVariable(COUNTRYCODE)))), TransactionStatsDTO.class);
    }
}
