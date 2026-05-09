package co.com.bancolombia.api.config;

import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.model.common.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "500";
        String message = "Internal Server Error";
        String detail = "An internal error has occurred";

        if (error instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            code = "400";
            message = "Bad Request";
            detail = error.getMessage();
        } else if (error instanceof ServiceUnavailableException || error instanceof CallNotPermittedException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            code = "503";
            message = "Service Unavailable";
            detail = "The service is temporarily unavailable.";
        }

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorResponse.builder()
                        .code(code)
                        .message(message)
                        .detail(detail)
                        .build());
    }
}
