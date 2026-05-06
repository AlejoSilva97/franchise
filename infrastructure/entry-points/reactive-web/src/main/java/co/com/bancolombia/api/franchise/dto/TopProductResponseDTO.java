package co.com.bancolombia.api.franchise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductResponseDTO {
    private String branchId;
    private String branchName;
    private ProductSummaryDTO product;
}
