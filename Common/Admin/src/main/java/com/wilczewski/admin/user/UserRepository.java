package com.wilczewski.admin.user;

import com.wilczewski.shared.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);

    public Long countById(Integer id);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.Id = ?1")
    @Modifying
   public void updateStatus(Integer id, boolean enabled);

    @Query("SELECT u FROM User u WHERE CONCAT(u.Id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);
}
