package com.wilczewski.admin.user;

import com.wilczewski.shared.entity.Role;
import com.wilczewski.shared.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public List<Role> listRoles(){
        return roleRepository.findAll();
    }

    public void saveUser(User user){
        encodePassword(user);
        userRepository.save(user);
    }

    private void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(String email){
        User userByEmail = userRepository.getUserByEmail(email);

        return userByEmail == null;
    }

}
