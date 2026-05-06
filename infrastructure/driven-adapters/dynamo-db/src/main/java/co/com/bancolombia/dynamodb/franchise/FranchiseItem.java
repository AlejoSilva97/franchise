package co.com.bancolombia.dynamodb.franchise;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseItem {
    private String id;
    private String name;
    @DynamoDbPartitionKey
    public String getId() { return id; }
    public String getName() { return name; }
}
