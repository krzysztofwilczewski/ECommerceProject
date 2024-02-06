package com.wilczewski.admin.user;

import com.wilczewski.shared.entity.Role;
import com.wilczewski.shared.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewUserWithOneAdminRole(){
        Role admin = entityManager.find(Role.class, 1);
        User user = new User("admin@admin.pl", "admin", "Krzysztof", "Wilczewski");
        user.addRole(admin);

        User savedUser = repository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);

    }

    @Test
    public void testUpdateUser(){
        User admin = repository.findById(1).get();
        admin.setEnabled(true);
        repository.save(admin);
    }

    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String raw = "krzysztof";
        String encoded = passwordEncoder.encode(raw);

        System.out.println(encoded);

        boolean match = passwordEncoder.matches(raw, encoded);

        assertThat(match).isTrue();
    }

    @Test
    public void testGetUserByEmail(){
        String email = "admin@admin.pl";
        User user = repository.getUserByEmail(email);

        assertThat(user).isNotNull();
    }
}
