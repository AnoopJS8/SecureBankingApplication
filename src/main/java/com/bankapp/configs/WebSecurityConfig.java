package com.bankapp.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/home", "/login/identify", "/login/verifyIdentity", "/signup",
                        "/registrationConfirm", "/resendRegistrationToken", "/badUser", "/sysadmin",
                        "/RetrieveEmployeeDetails", "/RetrieveManagerDetails", "/change_Username_Manager",
                        "/change_Address_Manager", "/change_username", "/change_Address", "/DeleteUserManager",
                        "/DeleteUserEmployee", "/profileRequest", "/changeRequest",
                        // Resources

        "/webjars/**", "/css/**", "/js/**"

        ).permitAll().anyRequest().authenticated().and().formLogin().loginPage("/").usernameParameter("m_email")
                .passwordParameter("m_password").successHandler(new AuthSuccessHandler()).permitAll().and().logout()
                .logoutUrl("/logout")

        .logoutSuccessUrl("/").and().sessionManagement().maximumSessions(1);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(12);
    }
}