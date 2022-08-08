package io.intuitdemo.router;

import io.intuitdemo.data.TransactionStatics;
import io.intuitdemo.handler.StatisticsUseCaseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class StatisticsUseCaseRouter {

    private static final String V1 = "/v1";
    private static final String STATS = "/stats";
    private static final String COUNTRY_CODE = "/{countrycode}";

    private final StatisticsUseCaseHandler statisticsUseCaseHandler;

    public StatisticsUseCaseRouter(StatisticsUseCaseHandler statisticsUseCaseHandler) {
        this.statisticsUseCaseHandler = statisticsUseCaseHandler;
    }

    @RouterOperations({
            @RouterOperation(path = V1 + STATS, beanClass = StatisticsUseCaseHandler.class, beanMethod = "getStatistics",
                    produces = APPLICATION_JSON_VALUE, method = RequestMethod.GET,
                    operation = @Operation(operationId = "evaluate expression",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionStatics.class)))),
                                    @ApiResponse(responseCode = "400", description = "error during operation",
                                            content = @Content(schema = @Schema(implementation = RuntimeException.class)))})),
            @RouterOperation(path = V1 + STATS + COUNTRY_CODE, beanClass = StatisticsUseCaseHandler.class, beanMethod = "getStatisticsForCountry",
                    produces = APPLICATION_JSON_VALUE, method = RequestMethod.GET,
                    operation = @Operation(operationId = "generateContractCodeForAddress",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(implementation = TransactionStatics.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid Eth address",
                                            content = @Content(schema = @Schema(implementation = RuntimeException.class)))},
                            parameters = {@Parameter(name = "countrycode", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, explode = Explode.FALSE, required = true)}
                    ))
    })
    @Bean
    public RouterFunction<ServerResponse> router() {
        return nest(path(V1),
                route(GET(STATS), statisticsUseCaseHandler::getStatistics)
                        .andRoute(GET(STATS + COUNTRY_CODE), statisticsUseCaseHandler::getStatisticsForCountry));
    }
}
