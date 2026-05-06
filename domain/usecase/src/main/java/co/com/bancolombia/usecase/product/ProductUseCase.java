package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.ErrorMessages;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Product> createProduct(String name, Integer stock, String branchId) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.PRODUCT_NOT_FOUND + branchId)))
                .flatMap(branch -> {
                    Product product = Product.builder()
                            .id(java.util.UUID.randomUUID().toString())
                            .name(name)
                            .stock(stock)
                            .branchId(branchId)
                            .build();
                    return productRepository.save(product);
                });

    }

    public Mono<Void> deleteProduct(String id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.PRODUCT_NOT_FOUND + id)))
                .flatMap(product -> productRepository.deleteById(id));
    }

    public Mono<Product> updateProductStock(String id, Integer stock) {
        if (stock < 0) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.PRODUCT_STOCK_NEGATIVE));
        }
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.PRODUCT_NOT_FOUND + id)))
                .flatMap(product -> {
                    product.setStock(stock);
                    return productRepository.update(product);
                });
    }

    public Mono<Product> updateProductName(String id, String name) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.PRODUCT_NOT_FOUND + id)))
                .flatMap(product -> {
                    product.setName(name);
                    return productRepository.update(product);
                });
    }
}
