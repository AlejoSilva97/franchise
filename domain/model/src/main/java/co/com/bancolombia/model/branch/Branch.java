package co.com.bancolombia.model.branch;
import co.com.bancolombia.model.product.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private String id;
    private String name;
    private List<Product> products;
    private String franchiseId;
}
