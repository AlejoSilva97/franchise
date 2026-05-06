package co.com.bancolombia.dynamodb.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseDynamoAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient client;

    @SuppressWarnings("unchecked")
    @Mock
    private DynamoDbAsyncTable<FranchiseItem> table;

    private final CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();

    private FranchiseDynamoAdapter adapter;

    @BeforeEach
    void setUp() {
        when(client.table(anyString(), any(TableSchema.class))).thenReturn(table);
        adapter = new FranchiseDynamoAdapter(client, "franchises", registry);
    }


    @Test
    void save_success() {
        Franchise franchise = Franchise.builder()
                .id("1").name("Franchise Test").branches(new ArrayList<>()).build();

        when(table.putItem(any(FranchiseItem.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.save(franchise))
                .assertNext(result -> {
                    assertEquals("1", result.getId());
                    assertEquals("Franchise Test", result.getName());
                })
                .verifyComplete();
    }


    @Test
    void findById_success() {
        FranchiseItem item = new FranchiseItem();
        item.setId("1");
        item.setName("Franchise Test");

        when(table.getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class)))
                .thenReturn(CompletableFuture.completedFuture(item));

        StepVerifier.create(adapter.findById("1"))
                .assertNext(franchise -> {
                    assertEquals("1", franchise.getId());
                    assertEquals("Franchise Test", franchise.getName());
                    assertNotNull(franchise.getBranches());
                })
                .verifyComplete();
    }

    @Test
    void findById_whenFranchiseNotFound_returnsEmptyMono() {
        when(table.getItem(any(software.amazon.awssdk.enhanced.dynamodb.Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.findById("not-existing"))
                .verifyComplete();
    }
}