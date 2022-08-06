package io.intuitdemo.service;

import io.intuitdemo.data.Product;
import io.intuitdemo.data.Severity;
import io.intuitdemo.data.TransactionStatics;
import io.intuitdemo.data.dto.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class StatisticsCalculatorServiceTest {

    private static final String ISRAEL = "Israel";
    @InjectMocks
    private StatisticsCalculatorService statisticsCalculatorService;
    List<TransactionDTO> transactionDTOList;

    @BeforeEach
    void setUp() {
        transactionDTOList = List.of(buildTransactionDto(true, true),
                buildTransactionDto(false, true),
                buildTransactionDto(false, false));
    }

    private TransactionDTO buildTransactionDto(boolean pass, boolean review) {
        return TransactionDTO.builder().
                latitude(100)
                .longitude(50)
                .pass(pass)
                .review(review)
                .product(Product.Mint)
                .severity(Severity.High)
                .transactionEpochSeconds(Instant.now().getEpochSecond())
                .build();
    }

    @Test
    void calculateNewStatisticsForCountry() {
        TransactionStatics transactionStatics = statisticsCalculatorService.calculateNewStatisticsForCountry(ISRAEL, transactionDTOList);
        assertEquals(ISRAEL, transactionStatics.getCountry());
        assertEquals(3, transactionStatics.getTotalEventCount());
        assertEquals(66.6, transactionStatics.getReviewPercentage(), 0.1);
        assertEquals(33.3, transactionStatics.getPassPercentage(), 0.1);
    }

    @Test
    void calculateNewStatisticsForCountrySingleELElement() {
        TransactionStatics transactionStatics = statisticsCalculatorService.calculateNewStatisticsForCountry(ISRAEL,
                List.of(buildTransactionDto(false, true)));
        assertEquals(ISRAEL, transactionStatics.getCountry());
        assertEquals(1, transactionStatics.getTotalEventCount());
        assertEquals(100, transactionStatics.getReviewPercentage(), 0.1);
        assertEquals(0, transactionStatics.getPassPercentage(), 0.1);
    }
}