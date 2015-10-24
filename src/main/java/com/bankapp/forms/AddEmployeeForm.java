package com.bankapp.forms;

import java.util.Date;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.annotations.NotFound;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.validators.ValidEmail;

public class AddEmployeeForm {
    
    @NotEmpty    
    private String username;
    
    @NotEmpty
    @NotFound
    @ValidEmail
    private String email;
    
    Role role;
    
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @NotNull(message = "Please provide a date of birth.")
    @Past
    private Date dateOfBirth;

    public AddEmployeeForm() {
        this.role = new Role();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
