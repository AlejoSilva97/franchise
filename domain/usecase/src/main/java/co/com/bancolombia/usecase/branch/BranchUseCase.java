package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.ErrorMessages;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Mono<Branch> createBranch(String name, String franchiseId){

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.FRANCHISE_NOT_FOUND + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = Branch.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .name(name)
                        .products(new java.util.ArrayList<>())
                        .franchiseId(franchiseId)
                        .build();
                    return branchRepository.save(branch);
                });
    }

    public Mono<Branch> updateBranchName(String id, String name) {
        if (name.length() < 3) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.BRANCH_NAME_TOO_SHORT));
        }
        return branchRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.BRANCH_NOT_FOUND + id)))
                .flatMap(branch -> {
                    branch.setName(name);
                    return branchRepository.updateName(branch);
                });
    }
}
