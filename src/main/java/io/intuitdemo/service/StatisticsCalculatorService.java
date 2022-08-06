package io.intuitdemo.service;

import io.intuitdemo.data.TransactionStatics;
import io.intuitdemo.data.dto.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

import static java.lang.Double.parseDouble;

@Service
@Slf4j
public class StatisticsCalculatorService {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public TransactionStatics calculateNewStatisticsForCountry(String country, List<TransactionDTO> transactionDTOList) {
        return TransactionStatics.builder()
                .country(country)
                .reviewPercentage(calculateReviewPercentage(transactionDTOList))
                .passPercentage(calculatePassedPercentage(transactionDTOList))
                .totalEventCount(transactionDTOList.size())
                .build();
    }

    private double calculateReviewPercentage(List<TransactionDTO> transactionDTOList) {
        double totalTransaction = transactionDTOList.size();
        double reviewTransactionNumber = transactionDTOList.stream().filter(TransactionDTO::isReview).count();
        return parseDouble(df.format(reviewTransactionNumber * 100 / totalTransaction));
    }

    private double calculatePassedPercentage(List<TransactionDTO> transactionDTOList) {
        double totalTransaction = transactionDTOList.size();
        double passed = transactionDTOList.stream().filter(TransactionDTO::isPass).count();
        return parseDouble(df.format(passed * 100 / totalTransaction));
    }
}
