package io.intuitdemo.service;

import io.intuitdemo.data.dto.TransactionDTO;
import io.intuitdemo.util.RandomTransactionBatchGeneratorUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.util.StringUtils.hasText;

@Service
public class TransactionListenerService {

    private final RandomTransactionBatchGeneratorUtil randomTransactionBatchGeneratorUtil;
    private final TransactionBatchHandlerService transactionBatchHandlerService;

    public TransactionListenerService(RandomTransactionBatchGeneratorUtil randomTransactionBatchGeneratorUtil,
                                      TransactionBatchHandlerService transactionBatchHandlerService) {
        this.randomTransactionBatchGeneratorUtil = randomTransactionBatchGeneratorUtil;
        this.transactionBatchHandlerService = transactionBatchHandlerService;
    }

    /**
     * instead of listening to an event from external app
     */
    public Flux<TransactionDTO> listenToTransactionStream() {
        return randomTransactionBatchGeneratorUtil.createRandomStream()
                .doOnNext(transactionBatchHandlerService::handleNextBatch)
                .flatMap(Flux::fromIterable)
                .filter(transactionDTO -> hasText(transactionDTO.getCountry()));
    }
}
