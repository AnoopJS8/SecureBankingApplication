package com.bankapp.services;

import java.util.ArrayList;
import java.util.Arrays;
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
        try {
            final com.bankapp.models.User user = userRepository.findByEmail(email);
            
            if(user == null) {
                return new User(" ", " ", true, true, true, true, getAuthorities(Arrays.asList(roleRepository.findByName("ROLE_USER"))));
            }
            return new User(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, getAuthorities(user.getRoles()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        System.out.println(roles);
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

}