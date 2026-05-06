package co.com.bancolombia.dynamodb.product;

import co.com.bancolombia.dynamodb.branch.BranchItem;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDynamoAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient client;

    @SuppressWarnings("unchecked")
    @Mock
    private DynamoDbAsyncTable<ProductItem> productTable;

    @SuppressWarnings("unchecked")
    @Mock
    private DynamoDbAsyncTable<BranchItem> branchTable;

    private ProductDynamoAdapter adapter;

    @BeforeEach
    void setUp() {
        when(client.table(eq("products"), any(TableSchema.class))).thenReturn(productTable);
        when(client.table(eq("branches"), any(TableSchema.class))).thenReturn(branchTable);
        adapter = new ProductDynamoAdapter(client, "products", "branches");
    }


    @Test
    void save_success() {
        Product product = Product.builder().id("p1").name("Burger").stock(50).branchId("b1").build();

        when(productTable.putItem(any(ProductItem.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.save(product))
                .assertNext(result -> {
                    assertEquals("p1", result.getId());
                    assertEquals("Burger", result.getName());
                    assertEquals(50, result.getStock());
                })
                .verifyComplete();
    }


    @Test
    void findById_success() {
        ProductItem item = new ProductItem();
        item.setId("p1");
        item.setName("Burger");
        item.setStock(50);
        item.setBranchId("b1");

        when(productTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(item));

        StepVerifier.create(adapter.findById("p1"))
                .assertNext(product -> {
                    assertEquals("p1", product.getId());
                    assertEquals("Burger", product.getName());
                    assertEquals(50, product.getStock());
                    assertEquals("b1", product.getBranchId());
                })
                .verifyComplete();
    }

    @Test
    void findById_notFound_returnsEmptyMono() {
        when(productTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.findById("not-existing"))
                .verifyComplete();
    }


    @Test
    void deleteById_success() {
        when(productTable.deleteItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.deleteById("p1"))
                .verifyComplete();
    }


    @Test
    @SuppressWarnings("unchecked")
    void findTopStockProductsByFranchise_success() {
        // Branch encontrada via GSI
        BranchItem branchItem = new BranchItem();
        branchItem.setId("b1");
        branchItem.setName("Branch Norte");
        branchItem.setFranchiseId("f1");

        // Producto con mayor stock de esa branch
        ProductItem productItem = new ProductItem();
        productItem.setId("p1");
        productItem.setName("Burger");
        productItem.setStock(100);
        productItem.setBranchId("b1");

        // Mockear índices
        DynamoDbAsyncIndex<BranchItem> branchIndex = Mockito.mock(DynamoDbAsyncIndex.class);
        DynamoDbAsyncIndex<ProductItem> productIndex = Mockito.mock(DynamoDbAsyncIndex.class);

        when(branchTable.index("franchiseId-index")).thenReturn(branchIndex);
        when(productTable.index("branchId-stock-index")).thenReturn(productIndex);

        // Page de branches
        Page<BranchItem> branchPage = Mockito.mock(Page.class);
        when(branchPage.items()).thenReturn(List.of(branchItem));
        PagePublisher<BranchItem> branchPublisher =
                PagePublisher.create(subscriber -> reactor.core.publisher.Flux.just(branchPage).subscribe(subscriber));

        // Page de productos
        Page<ProductItem> productPage = Mockito.mock(Page.class);
        when(productPage.items()).thenReturn(List.of(productItem));
        PagePublisher<ProductItem> productPublisher =
                PagePublisher.create(subscriber -> reactor.core.publisher.Flux.just(productPage).subscribe(subscriber));

        when(branchIndex.query(any(QueryEnhancedRequest.class))).thenReturn(branchPublisher);
        when(productIndex.query(any(QueryEnhancedRequest.class))).thenReturn(productPublisher);

        StepVerifier.create(adapter.findTopStockProductsByFranchise("f1"))
                .assertNext(result -> {
                    assertEquals("p1", result.getId());
                    assertEquals("Burger", result.getName());
                    assertEquals(100, result.getStock());
                    assertEquals("b1", result.getBranchId());
                    assertEquals("Branch Norte", result.getBranchName());
                })
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void findTopStockProductsByFranchise_noBranches_returnsEmpty() {
        DynamoDbAsyncIndex<BranchItem> branchIndex = Mockito.mock(DynamoDbAsyncIndex.class);
        when(branchTable.index("franchiseId-index")).thenReturn(branchIndex);

        Page<BranchItem> emptyPage = Mockito.mock(Page.class);
        when(emptyPage.items()).thenReturn(List.of());
        PagePublisher<BranchItem> emptyPublisher =
                PagePublisher.create(subscriber -> reactor.core.publisher.Flux.just(emptyPage).subscribe(subscriber));

        when(branchIndex.query(any(QueryEnhancedRequest.class))).thenReturn(emptyPublisher);

        StepVerifier.create(adapter.findTopStockProductsByFranchise("f1"))
                .verifyComplete();
    }
}