package co.com.bancolombia.api.franchise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {
    private String id;
    private String name;
    private Integer stock;
}
