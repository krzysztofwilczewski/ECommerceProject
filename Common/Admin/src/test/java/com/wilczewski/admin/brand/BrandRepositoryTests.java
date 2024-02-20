package com.wilczewski.admin.brand;

import com.wilczewski.shared.entity.Brand;
import com.wilczewski.shared.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;


    @Test
    public void testCreateBrand(){
        Category electric = new Category(6);
        Brand bosch = new Brand("Bosch");

        bosch.getCategories().add(electric);

        Brand saveBrand = brandRepository.save(bosch);

        assertThat(saveBrand).isNotNull();
        assertThat(saveBrand.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateBrand2(){
        Category book = new Category(6);
        Brand manual = new Brand("Manual");

        manual.getCategories().add(book);

        Brand saveBrand = brandRepository.save(manual);

        assertThat(saveBrand).isNotNull();
        assertThat(saveBrand.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateBrand3() {
        Category electric = new Category(5);
        Category manual = new Category(6);

        Brand apple = new Brand("Apple");
        apple.getCategories().add(electric);
        apple.getCategories().add(manual);

        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }
}
