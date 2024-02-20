package com.wilczewski.admin.brand;

import com.wilczewski.shared.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer>, PagingAndSortingRepository<Brand, Integer> {
}
