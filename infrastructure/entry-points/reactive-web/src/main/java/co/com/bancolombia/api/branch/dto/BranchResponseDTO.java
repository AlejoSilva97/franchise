package co.com.bancolombia.api.branch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BranchResponseDTO {
    private String id;
    private String name;
    private String franchiseId;
}
