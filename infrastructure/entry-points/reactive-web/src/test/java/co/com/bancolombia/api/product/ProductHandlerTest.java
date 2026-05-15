package co.com.bancolombia.api.product;

import co.com.bancolombia.api.product.dto.ProductNameRequestDTO;
import co.com.bancolombia.api.product.dto.ProductRequestDTO;
import co.com.bancolombia.api.product.dto.ProductStockRequestDTO;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.product.ProductUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private ProductUseCase productUseCase;

    @InjectMocks
    private ProductHandler productHandler;

    private Product sampleProduct() {
        return Product.builder().id("p1").name("Burger").stock(50).branchId("b1").build();
    }


    @Test
    void createProduct_success_returns201() {
        ProductRequestDTO dto = new ProductRequestDTO("Burger", 50, "b1");

        when(productUseCase.createProduct("Burger", 50, "b1")).thenReturn(Mono.just(sampleProduct()));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(dto));

        StepVerifier.create(productHandler.createProduct(request))
                .assertNext(response -> assertEquals(HttpStatus.CREATED, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void createProduct_emptyName_throwError() {
        ProductRequestDTO dto = new ProductRequestDTO("  ", 50, "b1");

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(dto));

        StepVerifier.create(productHandler.createProduct(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createProduct_negativeStock_throwError() {
        ProductRequestDTO dto = new ProductRequestDTO("Burger", -1, "b1");

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(dto));

        StepVerifier.create(productHandler.createProduct(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createProduct_emptyBranchId_throwError() {
        ProductRequestDTO dto = new ProductRequestDTO("Burger", 50, "  ");

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(dto));

        StepVerifier.create(productHandler.createProduct(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }


    @Test
    void deleteProduct_success_returns204() {
        when(productUseCase.deleteProduct("p1")).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "p1")
                .body(Mono.empty());

        StepVerifier.create(productHandler.deleteProduct(request))
                .assertNext(response -> assertEquals(HttpStatus.NO_CONTENT, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void deleteProduct_notFound_throwError() {
        when(productUseCase.deleteProduct("p99"))
                .thenReturn(Mono.error(new IllegalArgumentException("Product not found with ID: p99")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "p99")
                .body(Mono.empty());

        StepVerifier.create(productHandler.deleteProduct(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }


    @Test
    void updateProductStock_success_returns200() {
        ProductStockRequestDTO dto = new ProductStockRequestDTO(100);
        Product updated = Product.builder().id("p1").name("Burger").stock(100).branchId("b1").build();

        when(productUseCase.updateProductStock("p1", 100)).thenReturn(Mono.just(updated));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "p1")
                .body(Mono.just(dto));

        StepVerifier.create(productHandler.updateProductStock(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateProductStock_negativeStock_throwError() {
        ProductStockRequestDTO dto = new ProductStockRequestDTO(-5);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "p1")
                .body(Mono.just(dto));

        StepVerifier.create(productHandler.updateProductStock(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }


    @Test
    void updateProductName_success_returns200() {
        ProductNameRequestDTO dto = new ProductNameRequestDTO("New Name");
        Product updated = Product.builder().id("p1").name("New Name").stock(50).branchId("b1").build();

        when(productUseCase.updateProductName("p1", "New Name")).thenReturn(Mono.just(updated));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "p1")
                .body(Mono.just(dto));

        StepVerifier.create(productHandler.updateProductName(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateProductName_emptyName_throwError() {
        ProductNameRequestDTO dto = new ProductNameRequestDTO("  ");

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "p1")
                .body(Mono.just(dto));

        StepVerifier.create(productHandler.updateProductName(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}