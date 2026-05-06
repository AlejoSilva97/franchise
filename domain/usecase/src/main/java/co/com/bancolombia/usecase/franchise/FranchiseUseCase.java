package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.common.ErrorMessages;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {
    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;

    public Mono<Franchise> createFranchise(String name) {

        if (name.length() < 3) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.FRANCHISE_NAME_TOO_SHORT));
        }

        Franchise franchise = Franchise.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(name)
                .branches(new java.util.ArrayList<>())
                .build();

        return franchiseRepository.save(franchise);
    }

    public Mono<Franchise> updateFranchiseName(String id, String name) {
        if (name.length() < 3) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.FRANCHISE_NAME_TOO_SHORT));
        }
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.FRANCHISE_NOT_FOUND + id)))
                .flatMap(franchise -> {
                    franchise.setName(name);
                    return franchiseRepository.updateName(franchise);
                });
    }

    public Flux<TopStockProductByBranch> getTopProductByFranchiseId(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.FRANCHISE_NOT_FOUND + franchiseId)))
                .flatMapMany(franchise ->
                        productRepository.findTopStockProductsByFranchise(franchiseId));
    }

}
