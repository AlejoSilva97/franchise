package co.com.bancolombia.api.franchise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FranchiseResponseDTO {
    private String id;
    private String name;
}
