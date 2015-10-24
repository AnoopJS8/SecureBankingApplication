package com.bankapp.forms;


import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class ProfileForm {

    @NotBlank
    String username;
      
    @NotBlank
    String email;
    
    @NotBlank
    String address;

    @NotBlank
    @Pattern(regexp = "[0-9]+" , message = "Please enter a valid Phone Number")
    @Size(min = 10,max =10, message = "The Phone number should be 10 numbers long")
    String phoneNumber;
    
    @NotBlank
    @Pattern(regexp = "(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d)", message = "Please enter date in MM/dd/yyyy format")
    String dateOfBirth;

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

    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}