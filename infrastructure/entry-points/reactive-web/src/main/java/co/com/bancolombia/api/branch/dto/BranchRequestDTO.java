package co.com.bancolombia.api.branch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchRequestDTO {
    private String name;
    private String franchiseId;
}
