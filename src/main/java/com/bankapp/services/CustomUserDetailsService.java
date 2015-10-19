package com.bankapp.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Role;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;

@Service("userDetailsService")
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public CustomUserDetailsService() {
        super();
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        boolean enabled = true;
        boolean accountNotExpired = true;
        boolean credentialsNotExpired = true;
        boolean accountNotLocked = true;
        try {
            final com.bankapp.models.User user = userRepository.findByEmail(email);
            if (user == null) {
                return new User(" ", " ", enabled, true, true, true,
                        getAuthorities(roleRepository.findByName("ROLE_CUSTOMER")));
            }

            return new User(user.getEmail(), user.getPassword(), user.isEnabled(), accountNotExpired,
                    credentialsNotExpired, accountNotLocked, getAuthorities(user.getRole()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final Collection<? extends GrantedAuthority> getAuthorities(final Role role) {
        final List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        return authorities;
    }

}