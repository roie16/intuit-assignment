package io.intuitdemo.handler;

import io.intuitdemo.data.dto.TransactionDTO;
import io.intuitdemo.data.dto.TransactionStatsDTO;
import io.intuitdemo.repository.StatisticsRepository;
import io.intuitdemo.service.TransactionListenerService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class StatisticsUseCaseHandler {

    private final StatisticsRepository statisticsRepository;
    private final TransactionListenerService transactionListenerService;

    public StatisticsUseCaseHandler(StatisticsRepository statisticsRepository, TransactionListenerService transactionListenerService) {
        this.statisticsRepository = statisticsRepository;
        this.transactionListenerService = transactionListenerService;
    }

    public Mono<ServerResponse> getStatistics(ServerRequest ignored) {
        return ok().body(statisticsRepository.getStatistics(), TransactionStatsDTO.class);
    }

    public Mono<ServerResponse> startFlow(ServerRequest ignored) {
        return ok().body(transactionListenerService.listenToTransactionStream(), TransactionDTO.class);
    }

}
