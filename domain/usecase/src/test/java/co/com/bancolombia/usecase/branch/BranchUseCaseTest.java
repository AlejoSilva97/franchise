package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.ErrorMessages;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
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
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private BranchUseCase branchUseCase;

    // ── createBranch ───────────────────────────────────────────────

    @Test
    void createBranch_success() {
        Franchise franchise = Franchise.builder()
                .id("f1").name("Franchise Test").branches(new ArrayList<>()).build();

        when(franchiseRepository.findById("f1")).thenReturn(Mono.just(franchise));
        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(branchUseCase.createBranch("Branch Norte", "f1"))
                .assertNext(branch -> {
                    assertNotNull(branch.getId());
                    assertEquals("Branch Norte", branch.getName());
                    assertEquals("f1", branch.getFranchiseId());
                    assertNotNull(branch.getProducts());
                })
                .verifyComplete();
    }

    @Test
    void createBranch_franchiseNotFound_returnsError() {
        when(franchiseRepository.findById("bad-id")).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.createBranch("Branch Norte", "bad-id"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.FRANCHISE_NOT_FOUND))
                .verify();
    }

    // ── updateBranchName ───────────────────────────────────────────

    @Test
    void updateBranchName_success() {
        Branch existing = Branch.builder()
                .id("b1").name("Old Name").franchiseId("f1").products(new ArrayList<>()).build();

        when(branchRepository.findById("b1")).thenReturn(Mono.just(existing));
        when(branchRepository.updateName(any(Branch.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(branchUseCase.updateBranchName("b1", "New Name"))
                .assertNext(branch -> assertEquals("New Name", branch.getName()))
                .verifyComplete();
    }

    @Test
    void updateBranchName_nameTooShort_returnsError() {
        StepVerifier.create(branchUseCase.updateBranchName("b1", "AB"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(ErrorMessages.BRANCH_NAME_TOO_SHORT))
                .verify();
    }

    @Test
    void updateBranchName_branchNotFound_returnsError() {
        when(branchRepository.findById("b99")).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateBranchName("b99", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains(ErrorMessages.BRANCH_NOT_FOUND))
                .verify();
    }
}