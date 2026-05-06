package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Mono<Void> deleteById(String id);
    Mono<Product> update(Product product);
    Flux<TopStockProductByBranch> findTopStockProductsByFranchise(String franchiseId);
}
