
package com.bankapp.configs;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Account;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;

@Component
public class DataSetup implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${com.bankapp.data.doSetup}")
    private Boolean doDataSetup;

    @Value("${com.bankapp.data.roleSetup}")
    private Boolean doRoleSetup;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (doRoleSetup) {
            // Create initial roles
            for (Roles role : Roles.values()) {
                createRoleIfNotFound(role.toString());
            }
        }

        if (!doDataSetup) {
            return;
        }

        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");

        final User adminUser = new User();
        adminUser.setUsername("Test Admin");
        adminUser.setPassword(passwordEncoder.encode("test123"));
        adminUser.setEmail("test@admin.com");
        adminUser.setRole(adminRole);
        adminUser.setEnabled(true);
        adminUser.setDateOfBirth(new Date());
        adminUser.setPhoneNumber("1231231231");
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
        employeeUser.setDateOfBirth(new Date());
        employeeUser.setPhoneNumber("1231231231");
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
        managerUser.setDateOfBirth(new Date());
        managerUser.setPhoneNumber("1231231231");
        managerUser.setSecurityQuestion("Name?");
        managerUser.setSecurityAnswer("test");
        userRepository.save(managerUser);

        final Role customerRole = roleRepository.findByName("ROLE_CUSTOMER");
        User customerUser = new User();
        customerUser.setUsername("Test Customer");
        customerUser.setPassword(passwordEncoder.encode("test123"));
        customerUser.setEmail("test@customer.com");
        customerUser.setRole(customerRole);
        customerUser.setEnabled(true);
        customerUser.setDateOfBirth(new Date());
        customerUser.setPhoneNumber("1231231231");
        customerUser.setSecurityQuestion("Name?");
        customerUser.setSecurityAnswer("test");
        userRepository.save(customerUser);

        Account customerAccount = new Account();
        customerUser.setAccount(customerAccount);
        customerAccount.setUser(customerUser);
        customerAccount.setTypeOfAccount("Checkings");
        customerAccount.setCreated(new Date());
        customerAccount.setBalance(100.0);
        customerAccount.setCriticalLimit(100.0);
        accountRepository.save(customerAccount);

        final Role merchantRole = roleRepository.findByName("ROLE_MERCHANT");
        User merchantUser = new User();
        merchantUser.setUsername("Test Merchant");
        merchantUser.setPassword(passwordEncoder.encode("test123"));
        merchantUser.setEmail("test@merchant.com");
        merchantUser.setRole(merchantRole);
        merchantUser.setEnabled(true);
        merchantUser.setDateOfBirth(new Date());
        merchantUser.setPhoneNumber("1231231231");
        merchantUser.setSecurityQuestion("Name?");
        merchantUser.setSecurityAnswer("test");
        userRepository.save(merchantUser);

        Account merchantAccount = new Account();
        merchantUser.setAccount(merchantAccount);
        merchantAccount.setUser(merchantUser);
        merchantAccount.setCreated(new Date());
        merchantAccount.setTypeOfAccount("Savings");
        merchantAccount.setBalance(100.0);
        merchantAccount.setCriticalLimit(100.0);
        accountRepository.save(merchantAccount);

        final Role agencyRole = roleRepository.findByName("ROLE_AGENCY");
        User agencyUser = new User();
        agencyUser.setUsername("Test Agency");
        agencyUser.setPassword(passwordEncoder.encode("test123"));
        agencyUser.setEmail("test@agency.com");
        agencyUser.setRole(agencyRole);
        agencyUser.setEnabled(true);
        agencyUser.setDateOfBirth(new Date());
        agencyUser.setPhoneNumber("1231231231");
        agencyUser.setSecurityQuestion("Name?");
        agencyUser.setSecurityAnswer("test");
        userRepository.save(agencyUser);

        doDataSetup = true;
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