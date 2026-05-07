package co.com.bancolombia.model.product;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void shouldCreateProductWithBuilder() {

        String id = "123";
        String name = "Laptop";
        Integer stock = 10;
        String branchId = "B01";


        Product product = Product.builder()
                .id(id)
                .name(name)
                .stock(stock)
                .branchId(branchId)
                .build();


        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getBranchId()).isEqualTo(branchId);
    }

    @Test
    void shouldUpdateFieldsWithSetters() {

        Product product = new Product();
        String newName = "Smartphone";


        product.setName(newName);
        product.setStock(50);


        assertThat(product.getName()).isEqualTo(newName);
        assertThat(product.getStock()).isEqualTo(50);
    }

    @Test
    void shouldCloneWithToBuilder() {

        Product original = new Product("1", "PC", 5, "BR1");


        Product modified = original.toBuilder()
                .stock(100)
                .build();


        assertThat(modified.getId()).isEqualTo(original.getId());
        assertThat(modified.getStock()).isEqualTo(100);
        assertThat(modified.getName()).isEqualTo(original.getName());
    }
}
