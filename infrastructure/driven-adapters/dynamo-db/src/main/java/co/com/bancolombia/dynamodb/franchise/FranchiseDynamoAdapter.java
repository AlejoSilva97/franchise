package co.com.bancolombia.dynamodb.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class FranchiseDynamoAdapter implements FranchiseRepository {
    private final DynamoDbAsyncTable<FranchiseItem> table;
    private final CircuitBreaker circuitBreaker;

    public FranchiseDynamoAdapter(
            DynamoDbEnhancedAsyncClient client,
            @Value("${aws.dynamodb.table-names.franchises:franchises}") String tableName,
            CircuitBreakerRegistry registry
    ) {
        this.table = client.table(tableName, TableSchema.fromBean(FranchiseItem.class));
        this.circuitBreaker = registry.circuitBreaker("databaseCircuitBreaker");
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseItem item = FranchiseItem.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
        return Mono.fromFuture(table.putItem(item))
                .thenReturn(franchise)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Mono.fromFuture(table.getItem(key))
                .map(item -> Franchise.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .branches(new java.util.ArrayList<>())
                        .build())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Franchise> updateName(Franchise franchise) {
        return save(franchise);
    }

}
