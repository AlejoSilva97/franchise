package co.com.bancolombia.dynamodb.branch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchItem {
    private String id;
    private String name;
    private String franchiseId;
    @DynamoDbPartitionKey
    public String getId() { return id; }
    public String getName() { return name; }
    @DynamoDbSecondaryPartitionKey(indexNames = "franchiseId-index")
    public String getFranchiseId() { return franchiseId; }
}
