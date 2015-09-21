package com.bankapp.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.User;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @Override
    public User registerNewUserAccount(final User user) throws EmailExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email adress: " + user.getEmail());
        }
        final User newUser = new User();

        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());

        newUser.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        return repository.save(newUser);
    }

    private boolean emailExist(String email) {
        User user = repository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }
}