package com.wilczewski.admin.user;

import com.wilczewski.shared.entity.Role;
import com.wilczewski.shared.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public List<Role> listRoles(){
        return roleRepository.findAll();
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

}
