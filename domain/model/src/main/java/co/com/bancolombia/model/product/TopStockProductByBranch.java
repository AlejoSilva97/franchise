package co.com.bancolombia.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopStockProductByBranch {
    private String id;
    private String name;
    private Integer stock;
    private String branchId;
    private String branchName;
}
