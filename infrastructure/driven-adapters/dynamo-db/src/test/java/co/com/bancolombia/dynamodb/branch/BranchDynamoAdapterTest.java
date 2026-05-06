package co.com.bancolombia.dynamodb.branch;

import co.com.bancolombia.model.branch.Branch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchDynamoAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient client;

    @SuppressWarnings("unchecked")
    @Mock
    private DynamoDbAsyncTable<BranchItem> table;

    private BranchDynamoAdapter adapter;

    @BeforeEach
    void setUp() {
        when(client.table(anyString(), any(TableSchema.class))).thenReturn(table);
        adapter = new BranchDynamoAdapter(client, "branches");
    }

    // ── save ───────────────────────────────────────────────────────

    @Test
    void save_success() {
        Branch branch = Branch.builder()
                .id("b1").name("Branch Norte").franchiseId("f1").products(new ArrayList<>()).build();

        when(table.putItem(any(BranchItem.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.save(branch))
                .assertNext(result -> {
                    assertEquals("b1", result.getId());
                    assertEquals("Branch Norte", result.getName());
                    assertEquals("f1", result.getFranchiseId());
                })
                .verifyComplete();
    }

    // ── findById ───────────────────────────────────────────────────

    @Test
    void findById_success() {
        BranchItem item = new BranchItem();
        item.setId("b1");
        item.setName("Branch Norte");
        item.setFranchiseId("f1");

        when(table.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(item));

        StepVerifier.create(adapter.findById("b1"))
                .assertNext(branch -> {
                    assertEquals("b1", branch.getId());
                    assertEquals("Branch Norte", branch.getName());
                    assertEquals("f1", branch.getFranchiseId());
                    assertNotNull(branch.getProducts());
                })
                .verifyComplete();
    }

    @Test
    void findById_notFound_returnsEmptyMono() {
        when(table.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.findById("not-existing"))
                .verifyComplete();
    }

    // ── updateName ─────────────────────────────────────────────────

    @Test
    void updateName_delegatesToSave() {
        Branch branch = Branch.builder()
                .id("b1").name("Updated Name").franchiseId("f1").products(new ArrayList<>()).build();

        when(table.putItem(any(BranchItem.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.updateName(branch))
                .assertNext(result -> assertEquals("Updated Name", result.getName()))
                .verifyComplete();
    }
}