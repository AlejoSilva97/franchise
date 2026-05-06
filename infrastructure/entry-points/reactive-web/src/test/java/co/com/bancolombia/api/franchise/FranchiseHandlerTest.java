package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.franchise.dto.FranchiseNameRequestDTO;
import co.com.bancolombia.api.franchise.dto.FranchiseRequestDTO;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private FranchiseUseCase franchiseUseCase;

    @InjectMocks
    private FranchiseHandler franchiseHandler;

    @Test
    void createFranchise_success_returns201() {
        FranchiseRequestDTO dto = new FranchiseRequestDTO();
        dto.setName("McDonald's");

        Franchise franchise = Franchise.builder()
                .id("1").name("McDonald's").branches(new ArrayList<>()).build();

        when(franchiseUseCase.createFranchise("McDonald's")).thenReturn(Mono.just(franchise));

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(dto));

        StepVerifier.create(franchiseHandler.createFranchise(request))
                .assertNext(response -> assertEquals(HttpStatus.CREATED, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void createFranchise_emptyName_returns400() {
        FranchiseRequestDTO dto = new FranchiseRequestDTO();
        dto.setName("  ");

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(dto));

        StepVerifier.create(franchiseHandler.createFranchise(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void createFranchise_useCaseError_returns400() {
        FranchiseRequestDTO dto = new FranchiseRequestDTO();
        dto.setName("AB");

        when(franchiseUseCase.createFranchise("AB"))
                .thenReturn(Mono.error(new IllegalArgumentException("Name too short for a franchise")));

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(dto));

        StepVerifier.create(franchiseHandler.createFranchise(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void updateFranchiseName_success_returns200() {
        FranchiseNameRequestDTO dto = new FranchiseNameRequestDTO();
        dto.setName("New Name");

        Franchise updated = Franchise.builder()
                .id("1").name("New Name").branches(new ArrayList<>()).build();

        when(franchiseUseCase.updateFranchiseName("1", "New Name")).thenReturn(Mono.just(updated));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(dto));

        StepVerifier.create(franchiseHandler.updateFranchiseName(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();
    }

    @Test
    void getTopProductByFranchiseId_franchiseNotFound_returns400() {
        when(franchiseUseCase.getTopProductByFranchiseId("bad-id"))
                .thenReturn(Flux.error(new IllegalArgumentException("Franchise not found with ID: bad-id")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "bad-id")
                .body(Mono.empty());

        StepVerifier.create(franchiseHandler.getTopProductByFranchiseId(request))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                .verifyComplete();
    }
}