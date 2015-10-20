

package com.bankapp.configs;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        //registry.addViewController("/signup").setViewName("signup");
        registry.addViewController("/login").setViewName("login");

        registry.addViewController("/sysadmin").setViewName("sysadmin");

        registry.addViewController("/myaccount").setViewName("merchant/myaccount");
        registry.addViewController("/transferfunds").setViewName("merchant/transferfunds");
        
        

    }


       

    

}

