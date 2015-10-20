package com.bankapp.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;

@Component
public class DataSetup implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // Create initial roles
        for (Roles role : Roles.values()) {
            createRoleIfNotFound(role.toString());
        }

        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        final User user = new User();
        user.setUsername("Test");
        user.setPassword(passwordEncoder.encode("test123"));
        user.setEmail("test@test.com");
        user.setRole(adminRole);
        user.setEnabled(true);
        user.setSecurityQuestion("Name?");
        user.setSecurityAnswer("test");
        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }
        return role;
    }

}