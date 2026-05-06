package co.com.bancolombia.dynamodb.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
public class BranchDynamoAdapter implements BranchRepository {

    private final DynamoDbAsyncTable<BranchItem> table;

    public BranchDynamoAdapter(DynamoDbEnhancedAsyncClient client,
                               @Value("${aws.dynamodb.table-names.branches:branches}") String tableName) {
        this.table = client.table(tableName, TableSchema.fromBean(BranchItem.class));
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchItem item = BranchItem.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .build();
        return Mono.fromFuture(table.putItem(item))
                .thenReturn(branch);
    }

    @Override
    public Mono<Branch> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Mono.fromFuture(table.getItem(key))
                .map(item -> Branch.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .franchiseId(item.getFranchiseId())
                        .products(new java.util.ArrayList<>())
                        .build());
    }

    @Override
    public Mono<Branch> updateName(Branch branch) {
        return save(branch);
    }
}