package io.intuitdemo.router;

import io.intuitdemo.data.dto.TransactionDTO;
import io.intuitdemo.service.TransactionListenerService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class LiveTransactionUseCase {

    private final TransactionListenerService transactionListenerService;

    public LiveTransactionUseCase(TransactionListenerService transactionListenerService) {
        this.transactionListenerService = transactionListenerService;
    }

    @MessageMapping("live")
    public Flux<TransactionDTO> sayHelloReactiveFluxPayload() {
        return transactionListenerService.listenToTransactionStream();
    }
}
