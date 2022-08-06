package io.intuitdemo.repository;

import io.intuitdemo.data.dto.TransactionDTO;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<TransactionDTO, String> {

    Flux<TransactionDTO> findByCountryAndTransactionEpochSecondsAfter(String country, long from);
}
