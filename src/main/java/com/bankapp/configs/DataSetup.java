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

        final User adminUser = new User();
        adminUser.setUsername("Test Admin");
        adminUser.setPassword(passwordEncoder.encode("test123"));
        adminUser.setEmail("test@admin.com");
        adminUser.setRole(adminRole);
        adminUser.setEnabled(true);
        adminUser.setSecurityQuestion("Name?");
        adminUser.setSecurityAnswer("test");
        userRepository.save(adminUser);

        final Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE");
        User employeeUser = new User();
        employeeUser.setUsername("Test Employee");
        employeeUser.setPassword(passwordEncoder.encode("test123"));
        employeeUser.setEmail("test@employee.com");
        employeeUser.setRole(employeeRole);
        employeeUser.setEnabled(true);
        employeeUser.setSecurityQuestion("Name?");
        employeeUser.setSecurityAnswer("test");
        userRepository.save(employeeUser);

        final Role managerRole = roleRepository.findByName("ROLE_MANAGER");
        User managerUser = new User();
        managerUser.setUsername("Test Manager");
        managerUser.setPassword(passwordEncoder.encode("test123"));
        managerUser.setEmail("test@manager.com");
        managerUser.setRole(managerRole);
        managerUser.setEnabled(true);
        managerUser.setSecurityQuestion("Name?");
        managerUser.setSecurityAnswer("test");
        userRepository.save(managerUser);

        final Role customerRole = roleRepository.findByName("ROLE_CUSTOMER");
        User customerUser = new User();
        customerUser.setUsername("Test Custoemr");
        customerUser.setPassword(passwordEncoder.encode("test123"));
        customerUser.setEmail("test@customer.com");
        customerUser.setRole(customerRole);
        customerUser.setEnabled(true);
        customerUser.setSecurityQuestion("Name?");
        customerUser.setSecurityAnswer("test");
        userRepository.save(customerUser);

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