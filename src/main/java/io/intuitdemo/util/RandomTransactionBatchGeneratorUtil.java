package io.intuitdemo.util;

import io.intuitdemo.data.Product;
import io.intuitdemo.data.Severity;
import io.intuitdemo.data.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

import static java.lang.Math.random;
import static java.time.LocalDateTime.ofInstant;
import static reactor.core.publisher.Flux.interval;

/**
 * only for demo purposes so no Unittest on this class
 */
@Component
public class RandomTransactionBatchGeneratorUtil {

    public static final String REGEX = "[^\\d?!.]";
    @Value("${intuit.maxBatchSize}")
    private int maxBatchSize;
    private final WebClient geoWebClient;

    public RandomTransactionBatchGeneratorUtil(@Qualifier("geoWebClient") WebClient geoWebClient) {
        this.geoWebClient = geoWebClient;
    }

    public Flux<List<TransactionDTO>> createRandomStream() {
        return interval(Duration.ofMillis(0), Duration.ofSeconds(10))
                .flatMap(this::handleNextTransactionBatch);
    }

    private Mono<List<TransactionDTO>> handleNextTransactionBatch(Long ignored) {
        int batchSize = numberOfTransactionsInBatch();
        return Flux.range(0, batchSize)
                .flatMap(integer -> createRandomTransaction())
                .collectList();
    }

    private int numberOfTransactionsInBatch() {
        Random rand = new Random();
        return rand.nextInt(maxBatchSize);
    }

    private Mono<TransactionDTO> createRandomTransaction() {
        return geoWebClient.get().exchangeToMono(this::handleClientResponse);
    }

    private Mono<TransactionDTO> handleClientResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return clientResponse.bodyToMono(String.class)
                    .map(response -> {
                        Instant now = Instant.now();
                        return TransactionDTO.builder().
                                latitude(extractFromString(response, "<latt>"))
                                .longitude(extractFromString(response, "<longt>"))
                                .pass(random() > 0.5)
                                .review(random() > 0.5)
                                .product(random() > 0.5 ? Product.Quickbooks : Product.Mint)
                                .severity(random() > 0.5 ? Severity.High : Severity.Low)
                                .transactionEpochSeconds(now.getEpochSecond())
                                .build();
                    });

        } else {
            return Mono.empty();
        }
    }

    private double extractFromString(String response, String extractAfter) {
        int index = response.indexOf(extractAfter) + extractAfter.length();
        return Double.parseDouble(response.substring(index, index + 6).replaceAll(REGEX,""));
    }
}
