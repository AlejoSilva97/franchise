package co.com.bancolombia.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductResponseDTO {
    private String id;
    private String name;
    private Integer stock;
    private String branchId;
}
