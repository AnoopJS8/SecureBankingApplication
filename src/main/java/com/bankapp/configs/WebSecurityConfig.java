package com.bankapp.configs;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
                .antMatchers("/", "/home", "/login/identify",
                        "/login/verifyIdentity", "/signup",
                        "/registrationConfirm", "/resendRegistrationToken",
                        "/badUser",
                        // Resources
                        "/webjars/**").permitAll().anyRequest().authenticated()
                .and().formLogin().loginPage("/login")
                .usernameParameter("m_email").passwordParameter("m_password")
                .successHandler(new AuthSuccessHandler()).permitAll().and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login").and()
                .sessionManagement().maximumSessions(1);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth)
            throws Exception {
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

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
         
        // keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
        // keytool -list -v -keystore keystore.p12 -storetype pkcs12
         
        // curl -u user:password -k https://127.0.0.1:9000/greeting
         
        final String keystoreFile = "/keystore.p12";
        final String keystorePass = "myasubank";
        final String keystoreType = "PKCS12";
        final String keystoreProvider = "SunJSSE";
        final String keystoreAlias = "group7";
     
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) (Connector con) -> {
            con.setScheme("https");
            con.setSecure(true);
            Http11NioProtocol proto = (Http11NioProtocol) con.getProtocolHandler();
            proto.setSSLEnabled(true);
            proto.setKeystoreFile(keystoreFile);
            proto.setKeystorePass(keystorePass);
            proto.setKeystoreType(keystoreType);
            proto.setProperty("keystoreProvider", keystoreProvider);
            proto.setKeyAlias(keystoreAlias);
        });
     
         
        return factory;
    }

    
}