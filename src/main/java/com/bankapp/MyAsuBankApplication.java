package com.bankapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@ComponentScan
@EnableJpaRepositories(basePackages = "com.bankapp.repositories")
@EntityScan(basePackages = "com.bankapp.models")
public class MyAsuBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyAsuBankApplication.class, args);
    }
}
