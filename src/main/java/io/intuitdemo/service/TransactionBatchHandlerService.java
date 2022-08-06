package io.intuitdemo.service;

import io.intuitdemo.data.dto.TransactionDTO;
import io.intuitdemo.repository.StatisticsRepository;
import io.intuitdemo.repository.TransactionCacheRepository;
import io.intuitdemo.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.coordinates2country.Coordinates2Country.country;
import static java.util.Optional.ofNullable;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static reactor.core.scheduler.Schedulers.boundedElastic;

@Service
@Slf4j
public class TransactionBatchHandlerService {

    private final TransactionRepository transactionRepository;
    private final StatisticsRepository statisticsRepository;
    private final TransactionCacheRepository transactionCacheRepository;

    public TransactionBatchHandlerService(TransactionRepository transactionRepository, StatisticsRepository statisticsRepository, TransactionCacheRepository transactionCacheRepository) {
        this.transactionRepository = transactionRepository;
        this.statisticsRepository = statisticsRepository;
        this.transactionCacheRepository = transactionCacheRepository;
    }

    public void handleNextBatch(List<TransactionDTO> transactionDTOS) {
        transactionDTOS = setCountryBeforeSaving(transactionDTOS);
        List<TransactionDTO> finalTransactionDTOS = transactionDTOS; // need this for effectively final in the lambda
        saveBatchToDB(transactionDTOS)
                .doOnComplete(() -> updateStatsAfterSave(finalTransactionDTOS))
                .subscribe();
    }

    private void updateStatsAfterSave(List<TransactionDTO> finalTransactionDTOS) {
        List<String> countriesToUpdate = getCountriesToUpdate(finalTransactionDTOS);
        statisticsRepository.updateStatisticsForCountries(countriesToUpdate);
    }

    private List<String> getCountriesToUpdate(List<TransactionDTO> transactionDTOS) {
        return transactionDTOS.stream()
                .map(TransactionDTO::getCountry)
                .distinct()
                .toList();
    }

    private Flux<TransactionDTO> saveBatchToDB(List<TransactionDTO> transactionDTOS) {
        try {
            return transactionRepository.saveAll(transactionDTOS)
                    .doOnNext(transactionDTO -> transactionCacheRepository.addToCache(transactionDTO.getCountry()))
                    .subscribeOn(boundedElastic());
        } catch (Exception ex) {
            log.error("Error while saving transaction to db: ", ex);
            return Flux.empty();
        }
    }

    private List<TransactionDTO> setCountryBeforeSaving(List<TransactionDTO> transactionDTOS) {
        return transactionDTOS
                .stream()
                .flatMap(this::setCountryIfPresent)
                .collect(Collectors.toList());
    }

    private Stream<TransactionDTO> setCountryIfPresent(TransactionDTO transactionDTO) {
        try {
            return ofNullable(country(transactionDTO.getLatitude(), transactionDTO.getLongitude()))
                    .map(s -> setCountryStream(transactionDTO, s))
                    .orElseGet(Stream::empty);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return empty();
        }
    }

    private Stream<TransactionDTO> setCountryStream(TransactionDTO transactionDTO, String s) {
        transactionDTO.setCountry(s);
        return of(transactionDTO);
    }

}
