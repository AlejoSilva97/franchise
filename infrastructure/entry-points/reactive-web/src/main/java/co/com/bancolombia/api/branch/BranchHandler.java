package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.BranchNameRequestDTO;
import co.com.bancolombia.api.branch.dto.BranchRequestDTO;
import co.com.bancolombia.api.branch.dto.BranchResponseDTO;
import co.com.bancolombia.api.common.ApiErrorMessages;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.usecase.branch.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {
    private final BranchUseCase branchUseCase;

    public Mono<ServerResponse> createBranch(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(BranchRequestDTO.class)
                .filter(dto -> dto.getName() != null && !dto.getName().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.BRANCH_NAME_REQUIRED)))
                .filter(dto -> dto.getFranchiseId() != null && !dto.getFranchiseId().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.FRANCHISE_ID_BODY_REQUIRED)))
                .flatMap(dto -> branchUseCase.createBranch(dto.getName(), dto.getFranchiseId()))
                .flatMap(branch -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(toResponse(branch)))
                .onErrorResume(IllegalArgumentException.class, e -> badRequest(e.getMessage()))
                .onErrorResume(e -> serverError());
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(BranchNameRequestDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.REQUEST_BODY_MISSING)))
                .filter(dto -> dto.getName() != null && !dto.getName().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.BRANCH_NAME_REQUIRED)))
                .flatMap(dto -> {
                    if (id.isBlank()) {
                        return Mono.error(new IllegalArgumentException(ApiErrorMessages.BRANCH_ID_REQUIRED));
                    }
                    return branchUseCase.updateBranchName(id, dto.getName());
                })
                .flatMap(branch -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(toResponse(branch)))
                .onErrorResume(IllegalArgumentException.class, e -> badRequest(e.getMessage()))
                .onErrorResume(e -> serverError());
    }

    private BranchResponseDTO toResponse(Branch branch) {
        return BranchResponseDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
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
