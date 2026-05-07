package co.com.bancolombia.model.branch;

import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FranchiseTest {

    @Test
    void shouldCreateBranchWithBuilder() {

        String id = "B1";
        String name = "branch a";


        Branch branch = Branch.builder()
                .id(id)
                .name(name)
                .build();


        assertThat(branch).isNotNull();
        assertThat(branch.getId()).isEqualTo(id);
        assertThat(branch.getName()).isEqualTo(name);
    }

    @Test
    void shouldUpdateFieldsWithSetters() {

        Branch branch = new Branch();
        String newName = "Branch b";


        branch.setName(newName);


        assertThat(branch.getName()).isEqualTo(newName);
    }

    @Test
    void shouldCloneWithToBuilder() {
        List<Product> products = List.of(new Product("P1", "producto 1", 100, "B1"));

        Branch branch = Branch.builder()
                .id("B1")
                .name("Branch c")
                .products(products)
                .build();

        Branch modified = branch.toBuilder()
                .name("Branch d")
                .build();

        assertThat(modified.getId()).isEqualTo(branch.getId());
        assertThat(modified.getName()).isEqualTo("Branch d");
        assertThat(modified.getProducts()).isEqualTo(products);

        assertThat(branch.getName()).isEqualTo("Branch c");
    }
}
