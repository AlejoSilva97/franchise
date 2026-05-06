package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.ErrorMessages;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    // ── createProduct ──────────────────────────────────────────────

    @Test
    void createProduct_success() {
        Branch branch = Branch.builder()
                .id("b1").name("Branch Norte").franchiseId("f1").products(new ArrayList<>()).build();

        when(branchRepository.findById("b1")).thenReturn(Mono.just(branch));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productUseCase.createProduct("Burger", 50, "b1"))
                .assertNext(product -> {
                    assertNotNull(product.getId());
                    assertEquals("Burger", product.getName());
                    assertEquals(50, product.getStock());
                    assertEquals("b1", product.getBranchId());
                })
                .verifyComplete();
    }

    @Test
    void createProduct_branchNotFound_returnsError() {
        when(branchRepository.findById("bad-id")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.createProduct("Burger", 50, "bad-id"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND))
                .verify();
    }

    // ── deleteProduct ──────────────────────────────────────────────

    @Test
    void deleteProduct_success() {
        Product product = Product.builder().id("p1").name("Burger").stock(50).branchId("b1").build();

        when(productRepository.findById("p1")).thenReturn(Mono.just(product));
        when(productRepository.deleteById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProduct("p1"))
                .verifyComplete();
    }

    @Test
    void deleteProduct_notFound_returnsError() {
        when(productRepository.findById("p99")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProduct("p99"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND))
                .verify();
    }

    // ── updateProductStock ─────────────────────────────────────────

    @Test
    void updateProductStock_success() {
        Product product = Product.builder().id("p1").name("Burger").stock(50).branchId("b1").build();

        when(productRepository.findById("p1")).thenReturn(Mono.just(product));
        when(productRepository.update(any(Product.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productUseCase.updateProductStock("p1", 100))
                .assertNext(p -> assertEquals(100, p.getStock()))
                .verifyComplete();
    }

    @Test
    void updateProductStock_negativeStock_returnsError() {
        StepVerifier.create(productUseCase.updateProductStock("p1", -1))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(ErrorMessages.PRODUCT_STOCK_NEGATIVE))
                .verify();
    }

    @Test
    void updateProductStock_productNotFound_returnsError() {
        when(productRepository.findById("p99")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateProductStock("p99", 10))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND))
                .verify();
    }

    // ── updateProductName ──────────────────────────────────────────

    @Test
    void updateProductName_success() {
        Product product = Product.builder().id("p1").name("Old Name").stock(50).branchId("b1").build();

        when(productRepository.findById("p1")).thenReturn(Mono.just(product));
        when(productRepository.update(any(Product.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productUseCase.updateProductName("p1", "New Name"))
                .assertNext(p -> assertEquals("New Name", p.getName()))
                .verifyComplete();
    }

    @Test
    void updateProductName_productNotFound_returnsError() {
        when(productRepository.findById("p99")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateProductName("p99", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND))
                .verify();
    }
}