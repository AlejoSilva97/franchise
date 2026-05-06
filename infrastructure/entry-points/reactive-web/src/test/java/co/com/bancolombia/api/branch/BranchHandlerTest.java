package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.BranchNameRequestDTO;
import co.com.bancolombia.api.branch.dto.BranchRequestDTO;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.usecase.branch.BranchUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private BranchUseCase branchUseCase;

    @InjectMocks
    private BranchHandler branchHandler;

    // ── createBranch ───────────────────────────────────────────────

    @Test
    void createBranch_success_returns201() {
        BranchRequestDTO dto = new BranchRequestDTO("Branch Norte", "f1");

        Branch branch = Branch.builder()
                .id("b1").name("Branch Norte").franchiseId("f1").products(new ArrayList<>()).build();

        when(branchUseCase.createBranch("Branch Norte", "f1")).thenReturn(Mono.just(branch));

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(dto));

        StepVerifier.create(branchHandler.createBranch(request))
                .assertNext(response -> assertEquals(HttpStatus.CREATED, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void createBranch_emptyName_returns400() {
        BranchRequestDTO dto = new BranchRequestDTO("  ", "f1");

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(dto));

        StepVerifier.create(branchHandler.createBranch(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void createBranch_emptyFranchiseId_returns400() {
        BranchRequestDTO dto = new BranchRequestDTO("Branch Norte", "  ");

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(dto));

        StepVerifier.create(branchHandler.createBranch(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void createBranch_franchiseNotFound_returns400() {
        BranchRequestDTO dto = new BranchRequestDTO("Branch Norte", "bad-id");

        when(branchUseCase.createBranch("Branch Norte", "bad-id"))
                .thenReturn(Mono.error(new IllegalArgumentException("Franchise not found with ID: bad-id")));

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(dto));

        StepVerifier.create(branchHandler.createBranch(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    // ── updateBranchName ───────────────────────────────────────────

    @Test
    void updateBranchName_success_returns200() {
        BranchNameRequestDTO dto = new BranchNameRequestDTO("New Name");

        Branch updated = Branch.builder()
                .id("b1").name("New Name").franchiseId("f1").products(new ArrayList<>()).build();

        when(branchUseCase.updateBranchName("b1", "New Name")).thenReturn(Mono.just(updated));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "b1")
                .body(Mono.just(dto));

        StepVerifier.create(branchHandler.updateBranchName(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateBranchName_emptyName_returns400() {
        BranchNameRequestDTO dto = new BranchNameRequestDTO("  ");

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "b1")
                .body(Mono.just(dto));

        StepVerifier.create(branchHandler.updateBranchName(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateBranchName_branchNotFound_returns400() {
        BranchNameRequestDTO dto = new BranchNameRequestDTO("New Name");

        when(branchUseCase.updateBranchName("b99", "New Name"))
                .thenReturn(Mono.error(new IllegalArgumentException("Branch not found with ID: b99")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "b99")
                .body(Mono.just(dto));

        StepVerifier.create(branchHandler.updateBranchName(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }
}