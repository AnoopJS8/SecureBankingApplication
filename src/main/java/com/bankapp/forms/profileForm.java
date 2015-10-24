package com.bankapp.forms;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

public class profileForm {

    @NotNull
    String address;

    @NotNull
    String phoneNumber;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    String dateOfBirth;

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