package com.wilczewski.admin.car;

import com.wilczewski.shared.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer>, PagingAndSortingRepository<Car, Integer> {

    @Query("SELECT c FROM Car c WHERE c.parent.id is NULL")
    public List<Car> findRootCars(Sort sort);

    @Query("SELECT c FROM Car c WHERE c.parent.id is NULL")
    public Page<Car> findRootCars(Pageable pageable);

    @Query("SELECT c FROM Car c WHERE c.name LIKE %?1%")
    public Page<Car> search(String keyword, Pageable pageable);

    public Car findByName(String name);

    public Car findByAlias(String alias);

    public Long countById(Integer id);

}
