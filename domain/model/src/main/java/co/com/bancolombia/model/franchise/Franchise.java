package co.com.bancolombia.model.franchise;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.product.Product;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Franchise {
    private String id;
    private String name;
    private List<Branch> branches;
}
