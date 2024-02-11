package com.wilczewski.admin.security;

import com.wilczewski.admin.user.UserRepository;
import com.wilczewski.shared.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);

        if (user != null) {
            return new AdminUserDetails(user);
        }
        throw new UsernameNotFoundException("Nie znaleziono użytkownika z emailem: " + email);
    }
}
