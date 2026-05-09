package co.com.bancolombia.dynamodb.product;

import co.com.bancolombia.dynamodb.branch.BranchItem;
import co.com.bancolombia.dynamodb.common.AdapterErrorMessages;
import co.com.bancolombia.model.common.exception.ServiceUnavailableException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Repository
public class ProductDynamoAdapter implements ProductRepository {

    private final DynamoDbAsyncTable<ProductItem> productTable;
    private final DynamoDbAsyncTable<BranchItem> branchTable;
    private final CircuitBreaker circuitBreaker;

    public ProductDynamoAdapter(DynamoDbEnhancedAsyncClient client,
                                @Value("${aws.dynamodb.table-names.products:products}") String productsTable,
                                @Value("${aws.dynamodb.table-names.branches:branches}") String branchesTable,
                                CircuitBreakerRegistry registry) {
        this.productTable = client.table(productsTable, TableSchema.fromBean(ProductItem.class));
        this.branchTable  = client.table(branchesTable, TableSchema.fromBean(BranchItem.class));
        this.circuitBreaker = registry.circuitBreaker("databaseCircuitBreaker");
    }

    @Override
    public Mono<Product> save(Product product) {
        ProductItem item = toItem(product);
        return Mono.fromFuture(productTable.putItem(item))
                .thenReturn(product)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(CallNotPermittedException.class, ex -> {
                    return Mono.error(new ServiceUnavailableException(AdapterErrorMessages.CIRCUIT_BREAKER_OPEN));
                });
    }

    @Override
    public Mono<Product> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Mono.fromFuture(productTable.getItem(key))
                .map(this::toProduct)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(CallNotPermittedException.class, ex -> {
                    return Mono.error(new ServiceUnavailableException(AdapterErrorMessages.CIRCUIT_BREAKER_OPEN));
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Mono.fromFuture(productTable.deleteItem(key)).then()
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(CallNotPermittedException.class, ex -> {
                    return Mono.error(new ServiceUnavailableException(AdapterErrorMessages.CIRCUIT_BREAKER_OPEN));
                });
    }

    @Override
    public Mono<Product> update(Product product) {
        return save(product);
    }

    @Override
    public Flux<TopStockProductByBranch> findTopStockProductsByFranchise(String franchiseId) {
        DynamoDbAsyncIndex<BranchItem> branchIndex = branchTable.index("franchiseId-index");

        QueryEnhancedRequest branchQuery = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(franchiseId).build()))
                .build();

        return Flux.from(branchIndex.query(branchQuery))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .flatMap(this::getTopProductForBranch)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(CallNotPermittedException.class, ex ->
                        Flux.error(new ServiceUnavailableException(AdapterErrorMessages.CIRCUIT_BREAKER_OPEN))
                );
    }

    private Mono<TopStockProductByBranch> getTopProductForBranch(BranchItem branch) {
        DynamoDbAsyncIndex<ProductItem> productIndex = productTable.index("branchId-stock-index");

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(branch.getId()).build()))
                .scanIndexForward(false)
                .limit(1)
                .build();

        return Flux.from(productIndex.query(request))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .next()
                .map(product -> TopStockProductByBranch.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .stock(product.getStock())
                        .branchId(product.getBranchId())
                        .branchName(branch.getName())
                        .build());
    }

    private ProductItem toItem(Product product) {
        return ProductItem.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .build();
    }

    private Product toProduct(ProductItem item) {
        return Product.builder()
                .id(item.getId())
                .name(item.getName())
                .stock(item.getStock())
                .branchId(item.getBranchId())
                .build();
    }
}
