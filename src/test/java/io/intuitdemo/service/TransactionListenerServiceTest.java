package io.intuitdemo.service;

import io.intuitdemo.data.Product;
import io.intuitdemo.data.Severity;
import io.intuitdemo.data.dto.TransactionDTO;
import io.intuitdemo.util.RandomTransactionBatchGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionListenerServiceTest {

    private static final String ISRAEL = "Israel";
    @Mock
    private RandomTransactionBatchGeneratorUtil randomTransactionBatchGeneratorUtil;

    @Mock
    private TransactionBatchHandlerService transactionBatchHandlerService;

    @InjectMocks
    private TransactionListenerService transactionListenerService;
    private List<TransactionDTO> transactionDTOList;
    private long epochTime;

    @BeforeEach
    void setUp() {
        epochTime = Instant.now().getEpochSecond();
        transactionDTOList = List.of(buildTransactionDto(true, true, "", epochTime),
                buildTransactionDto(false, true, null, epochTime),
                buildTransactionDto(false, false, ISRAEL, epochTime));
        doReturn(Flux.just(transactionDTOList)).when(randomTransactionBatchGeneratorUtil).createRandomStream();
    }

    private TransactionDTO buildTransactionDto(boolean pass, boolean review, String country, long epochTime) {
        return TransactionDTO.builder().
                latitude(100)
                .longitude(50)
                .country(country)
                .pass(pass)
                .review(review)
                .product(Product.Mint)
                .severity(Severity.High)
                .transactionEpochSeconds(epochTime)
                .build();
    }

    @Test
    void listenToTransactionStream() {
        StepVerifier.create(transactionListenerService.listenToTransactionStream())
                .expectNext(buildTransactionDto(false, false, ISRAEL, epochTime))
                .verifyComplete();
        verify(transactionBatchHandlerService).handleNextBatch(transactionDTOList);
    }
}