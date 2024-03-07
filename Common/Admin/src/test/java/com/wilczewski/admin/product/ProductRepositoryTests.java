package com.wilczewski.admin.product;


import com.wilczewski.shared.entity.Brand;
import com.wilczewski.shared.entity.Car;
import com.wilczewski.shared.entity.Category;
import com.wilczewski.shared.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct(){
        Brand brand = entityManager.find(Brand.class,5);
        Category category = entityManager.find(Category.class, 5);
        Car car = entityManager.find(Car.class, 3);

        Product product = new Product();
        product.setName("Lampy do auta");
        product.setAlias("lampy_bmw");
        product.setShortDescription("Krótki opis");
        product.setFullDescription("Długi opis");

        product.setBrand(brand);
        product.setCategory(category);
        product.getCar().add(car);

        product.setPrice(999);
        product.setCost(666);
        product.setEnabled(true);
        product.setInStock(true);

        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);


    }

    @Test
    public void testListAllProducts() {
        Iterable<Product> iterableProducts = productRepository.findAll();

        iterableProducts.forEach(System.out::println);

    }

    @Test
    public void testGetProduct() {
        Integer id = 2;
        Product product = productRepository.findById(id).get();
        System.out.println(product);

        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 2;
        Product product = productRepository.findById(id).get();
        product.setPrice(777);

        productRepository.save(product);

        Product updatedProduct = entityManager.find(Product.class, id);

        assertThat(updatedProduct.getPrice()).isEqualTo(777);
    }

    @Test
    public void testSaveProductWithImages(){
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();
        product.setMainImage("mainImage.jpg");
        product.addExtraImage("extra1.png");
        product.addExtraImage("extra2.png");
        product.addExtraImage("extra3.png");

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }
}
