package co.com.bancolombia.dynamodb.product;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
@DynamoDbBean
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItem {
    private String id;
    private String name;
    private Integer stock;
    private String branchId;
    @DynamoDbPartitionKey
    public String getId() { return id; }
    public String getName() { return name; }
    public Integer getStock() { return stock; }
    @DynamoDbSecondaryPartitionKey(indexNames = "branchId-stock-index")
    public String getBranchId() { return branchId; }
}
