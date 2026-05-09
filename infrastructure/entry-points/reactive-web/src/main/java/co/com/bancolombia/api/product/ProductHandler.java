package co.com.bancolombia.api.product;

import co.com.bancolombia.api.common.ApiErrorMessages;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.api.product.dto.ProductNameRequestDTO;
import co.com.bancolombia.api.product.dto.ProductRequestDTO;
import co.com.bancolombia.api.product.dto.ProductResponseDTO;
import co.com.bancolombia.api.product.dto.ProductStockRequestDTO;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> createProduct(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(ProductRequestDTO.class)
                .filter(dto -> dto.getName() != null && !dto.getName().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.PRODUCT_NAME_REQUIRED)))
                .filter(dto -> dto.getStock() != null && dto.getStock() >= 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.PRODUCT_STOCK_REQUIRED)))
                .filter(dto -> dto.getBranchId() != null && !dto.getBranchId().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.BRANCH_ID_REQUIRED_BODY)))
                .flatMap(dto -> productUseCase.createProduct(dto.getName(), dto.getStock(), dto.getBranchId()))
                .flatMap(product -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(toResponse(product)));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return Mono.just(id)
                .filter(productId -> productId != null && !productId.trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.PRODUCT_ID_REQUIRED)))
                .flatMap(productUseCase::deleteProduct)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(ProductStockRequestDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.REQUEST_BODY_MISSING)))
                .filter(dto -> dto.getStock() != null && dto.getStock() >= 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.PRODUCT_STOCK_REQUIRED)))
                .flatMap(dto -> {
                    if (id == null || id.isBlank()) {
                        return Mono.error(new IllegalArgumentException(ApiErrorMessages.PRODUCT_ID_REQUIRED));
                    }
                    return productUseCase.updateProductStock(id, dto.getStock());
                })
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(toResponse(product)));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(ProductNameRequestDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.REQUEST_BODY_MISSING)))
                .filter(dto -> dto.getName() != null && !dto.getName().trim().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ApiErrorMessages.PRODUCT_NAME_REQUIRED)))
                .flatMap(dto -> {
                    if (id == null || id.isBlank()) {
                        return Mono.error(new IllegalArgumentException(ApiErrorMessages.PRODUCT_ID_REQUIRED));
                    }
                    return productUseCase.updateProductName(id, dto.getName());
                })
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(toResponse(product)));
    }

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
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
