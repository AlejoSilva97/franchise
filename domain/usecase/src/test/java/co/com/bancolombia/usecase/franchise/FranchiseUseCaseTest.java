package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.common.ErrorMessages;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;


    @Test
    void createFranchise_whenNameIsValid_thenFranchiseIsCreated() {
        when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(franchiseUseCase.createFranchise("McDonald's"))
                .assertNext(franchise -> {
                    assertNotNull(franchise.getId());
                    assertEquals("McDonald's", franchise.getName());
                    assertNotNull(franchise.getBranches());
                })
                .verifyComplete();
    }

    @Test
    void createFranchise_whenNameIsTooShort_thenReturnsError() {
        StepVerifier.create(franchiseUseCase.createFranchise("AB"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(ErrorMessages.FRANCHISE_NAME_TOO_SHORT))
                .verify();
    }

    @Test
    void updateFranchiseName_whenFranchiseExists_thenNameIsUpdated() {
        Franchise existing = Franchise.builder()
                .id("abc-123").name("Old Name").branches(new ArrayList<>()).build();

        when(franchiseRepository.findById("abc-123")).thenReturn(Mono.just(existing));
        when(franchiseRepository.updateName(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(franchiseUseCase.updateFranchiseName("abc-123", "New Name"))
                .assertNext(f -> assertEquals("New Name", f.getName()))
                .verifyComplete();
    }

    @Test
    void updateFranchiseName_whenFranchiseNotFound_thenReturnsError() {
        when(franchiseRepository.findById("xyz")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateFranchiseName("xyz", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.FRANCHISE_NOT_FOUND))
                .verify();
    }

    @Test
    void updateFranchiseName_whenNameIsTooShort_thenReturnsError() {
        StepVerifier.create(franchiseUseCase.updateFranchiseName("abc-123", "AB"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(ErrorMessages.FRANCHISE_NAME_TOO_SHORT))
                .verify();
    }

    @Test
    void getTopProductByFranchiseId_whenFranchiseExists_thenReturnsTopProducts() {
        Franchise franchise = Franchise.builder()
                .id("f1").name("Test").branches(new ArrayList<>()).build();

        TopStockProductByBranch topProduct = TopStockProductByBranch.builder()
                .id("p1").name("Burger").stock(100)
                .branchId("b1").branchName("Branch 1").build();

        when(franchiseRepository.findById("f1")).thenReturn(Mono.just(franchise));
        when(productRepository.findTopStockProductsByFranchise("f1"))
                .thenReturn(Flux.just(topProduct));

        StepVerifier.create(franchiseUseCase.getTopProductByFranchiseId("f1"))
                .assertNext(result -> {
                    assertEquals("p1", result.getId());
                    assertEquals("Branch 1", result.getBranchName());
                    assertEquals(100, result.getStock());
                })
                .verifyComplete();
    }

    @Test
    void getTopProductByFranchiseId_whenFranchiseNotFound_thenReturnsError() {
        when(franchiseRepository.findById("bad-id")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.getTopProductByFranchiseId("bad-id"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.FRANCHISE_NOT_FOUND))
                .verify();
    }
}