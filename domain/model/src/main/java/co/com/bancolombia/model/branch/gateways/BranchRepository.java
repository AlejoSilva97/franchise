package co.com.bancolombia.model.branch.gateways;

import co.com.bancolombia.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(String id);
    Mono<Branch> updateName(Branch branch);
}
