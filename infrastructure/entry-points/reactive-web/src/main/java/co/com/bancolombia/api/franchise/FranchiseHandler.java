package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.common.ApiErrorMessages;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.api.franchise.dto.*;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {
    private final FranchiseUseCase franchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(FranchiseRequestDTO.class)
                .filter(dto -> dto.getName() != null && !dto.getName().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.FRANCHISE_NAME_REQUIRED)))
                .flatMap(dto -> franchiseUseCase.createFranchise(dto.getName()))
                .flatMap(franchise -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(toResponse(franchise)));
    }

    public Mono<ServerResponse> getTopProductByFranchiseId(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        return franchiseUseCase.getTopProductByFranchiseId(franchiseId)
                .map(item -> TopProductResponseDTO.builder()
                        .branchId(item.getBranchId())
                        .branchName(item.getBranchName())
                        .product(ProductSummaryDTO.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .stock(item.getStock())
                                .build())
                        .build())
                .collectList()
                .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(FranchiseNameRequestDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.REQUEST_BODY_MISSING)))
                .filter(dto -> dto.getName() != null && !dto.getName().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.FRANCHISE_NAME_REQUIRED)))
                .flatMap(dto -> {
                    if (id.isBlank()) {
                        return Mono.error(new IllegalArgumentException(ApiErrorMessages.FRANCHISE_ID_REQUIRED));
                    }
                    return franchiseUseCase.updateFranchiseName(id, dto.getName());
                })
                .flatMap(franchise -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(toResponse(franchise)));
    }

    private FranchiseResponseDTO toResponse(Franchise franchise) {
        return FranchiseResponseDTO.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
    }
    private Mono<ServerResponse> badRequest(String detail) {
        return ServerResponse.badRequest().bodyValue(
                ErrorResponse.builder()
                        .code("400")
                        .message("Bad Request")
                        .detail(detail)
                        .build());
    }
    private Mono<ServerResponse> serverError() {
        return ServerResponse.status(500).bodyValue(
                ErrorResponse.builder()
                        .code("500")
                        .message("Internal Server Error")
                        .detail("An unexpected error occurred")
                        .build());
    }
}
