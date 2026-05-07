package co.com.bancolombia.model.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FranchiseTest {

    @Test
    void shouldCreateFranchiseWithBuilder() {

        String id = "123";
        String name = "franchise a";


        Franchise franchise = Franchise.builder()
                .id(id)
                .name(name)
                .build();


        assertThat(franchise).isNotNull();
        assertThat(franchise.getId()).isEqualTo(id);
        assertThat(franchise.getName()).isEqualTo(name);
    }

    @Test
    void shouldUpdateFieldsWithSetters() {

        Franchise product = new Franchise();
        String newName = "Franchise b";


        product.setName(newName);


        assertThat(product.getName()).isEqualTo(newName);
    }

    @Test
    void shouldCloneWithToBuilder() {
        List<Product> products = List.of(new Product("P1", "producto 1", 100, "B1"));
        List<Branch> branches = List.of(new Branch("B1", "Branch North", products, "1"));

        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franchise c")
                .branches(branches)
                .build();

        Franchise modified = franchise.toBuilder()
                .name("Franchise d")
                .build();

        assertThat(modified.getId()).isEqualTo(franchise.getId());
        assertThat(modified.getName()).isEqualTo("Franchise d");
        assertThat(modified.getBranches()).isEqualTo(branches);

        assertThat(franchise.getName()).isEqualTo("Franchise c");
    }
}
